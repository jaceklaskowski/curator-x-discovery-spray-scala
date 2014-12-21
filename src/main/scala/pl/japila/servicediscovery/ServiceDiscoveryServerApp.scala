package pl.japila.servicediscovery

import akka.actor.ActorSystem
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode
import spray.http.MediaTypes
import spray.httpx.SprayJsonSupport
import spray.json.JsonFormat
import spray.routing.SimpleRoutingApp

import scala.util.Try

object ServiceDiscoveryServerApp extends App with SimpleRoutingApp {

  implicit lazy val actorSystem = ActorSystem()

  val hosts = "127.0.0.1:2181"
  lazy val client = createClient(hosts)

  lazy val route = rejectEmptyResponse {
    pathPrefix(separateOnSlashes("v1/service")) {
      pathPrefix(Segment) { name =>
        path(IntNumber) { id =>
          println(s">>> Received...($name, $id)")
          get {
            respondWithMediaType(MediaTypes.`application/json`) {
              complete {
                s"Received $name $id\n"
              }
            }
          }
        }
      } ~
      get {
        complete {
          val children = client.getChildren().forPath("/runtime")
          import spray.json._
          import DefaultJsonProtocol._
          import spray.httpx.SprayJsonSupport._
          import scala.collection.JavaConversions.asScalaBuffer
          asScalaBuffer(children).toList
        }
      }
    }
  }

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
  startServer(interface, port)(route)
}
