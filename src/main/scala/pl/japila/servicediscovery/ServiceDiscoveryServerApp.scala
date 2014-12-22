package pl.japila.servicediscovery

import akka.actor.ActorSystem
import akka.http.marshallers.xml.ScalaXmlSupport
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

import scala.util.Try

object ServiceDiscoveryServerApp extends App with IndexRoute {

  val hosts = "127.0.0.1:2181"
  lazy val client = createClient(hosts)

  def createClient(hosts: String) = {
    val zookeeperConnectionString = hosts
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy)
  }

  client.start

  Try(client.create().forPath("/runtime"))

  client.create().withMode(CreateMode.EPHEMERAL).forPath(s"/runtime/${java.util.UUID.randomUUID()}")
  client.create().withMode(CreateMode.EPHEMERAL).forPath(s"/runtime/${java.util.UUID.randomUUID()}")

  val interface = if (args.length >= 1) args(0) else "localhost"
  val port = if (args.length >= 2) args(1).toInt else 8080
  println(s"interface=$interface port=$port")

  import akka.stream.FlowMaterializer

  val conf: Config = ConfigFactory.parseString( """
    akka.loglevel = INFO
    akka.log-dead-letters = off
                                                """)
  implicit val system = ActorSystem("ServiceDiscovery", conf)
  import system.dispatcher
  implicit val fm = FlowMaterializer()

  import akka.http.Http

  val binding = Http().bind(interface = interface, port = port)

  import akka.http.server._
  import Directives._
  import ScalaXmlSupport._

  val materializedMap = binding startHandlingWith {
    rejectEmptyResponse {
      get {
        path("") {
          complete(index)
        } ~
          pathPrefix(separateOnSlashes("v1/service")) {
            pathPrefix(Segment) { name =>
              path(IntNumber) { id =>
                println(s">>> Received...($name, $id)")
                get {
                  complete {
                    s"Received $name $id\n"
                  }
                }
              }
            } ~
              get {
                complete {
                  val children = client.getChildren().forPath("/runtime")
                  import scala.collection.JavaConversions.asScalaBuffer
                  asScalaBuffer(children).toSeq.toString
                }
              }
          }
      }
    }
  }

//  println(s"Server online at http://$interface:$port/\nPress RETURN to stop...")
//  io.StdIn.readLine()
//  binding.unbind(materializedMap).onComplete(_ ⇒ system.shutdown())

}
