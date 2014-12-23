package pl.japila.servicediscovery

import akka.actor.ActorSystem
import akka.http.marshallers.xml.ScalaXmlSupport
import akka.http.model.{HttpResponse, HttpRequest}
import akka.stream.scaladsl.Flow
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode
import spray.json.DefaultJsonProtocol

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

  import akka.http.server._
  import Directives._
  import ScalaXmlSupport._

  private val shutdown: Route =
    path("shutdown") {
      get {
        complete {
          println(">>> Shutting down now ...")
          system.shutdown()
          "Shutting down now ..."
        }
      }
    }

  private val index: Route =
    path("") {
      get {
        complete(indexPage)
      }
    }

  private val service: Route =
    // format: OFF
    pathPrefix(separateOnSlashes("v1/service")) {
      path(Segment / IntNumber) { (name, id) =>
        get {
          complete {
            s"Received $name $id\n"
          }
        }
      } ~
      get {
        complete {
          val children = client.getChildren().forPath("/runtime")

          import akka.http.marshallers.sprayjson._
          import SprayJsonSupport._
          import DefaultJsonProtocol._
          import scala.collection.JavaConversions.asScalaBuffer
          asScalaBuffer(children).toSeq
        }
      }
    } // format: ON

  val route: Route = shutdown ~ index ~ service

  import akka.http.Http
  val binding = Http().bind(interface = interface, port = port)

  val materializedMap = binding startHandlingWith route

}
