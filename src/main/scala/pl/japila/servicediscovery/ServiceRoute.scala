package pl.japila.servicediscovery

import akka.actor.ActorSystem
import akka.http.model.MediaTypes
import akka.http.server.Directives._
import akka.http.server.PathMatchers.Segment
import akka.http.server._
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

import scala.util.Try

trait ServiceRoute {
  val hosts = "127.0.0.1:2181"
  lazy val client = createClient(hosts)

  def createClient(hosts: String) = {
    val zookeeperConnectionString = hosts
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy)
  }

  client.start

  Try(client.create().forPath("/runtime"))

  val sId = java.util.UUID.randomUUID()
  val sName = "My brand new service".toCharArray.map(_.toByte)
  val sPath = "/runtime/" + sId
  client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(sPath, sName)

  def service(implicit system: ActorSystem): Route =
    pathPrefix("v1") {
      serviceGetIdRoute ~ serviceGetRoute
    }

  def serviceGetIdRoute(implicit system: ActorSystem) = {
    import system._
    get {
      path("service" / JavaUUID) { serviceId =>
        try {
          val d = new String(client.getData.forPath(s"/runtime/$serviceId"))
          complete(s"Hello, I'm $d!")
        } catch {
          case e: Exception =>
            val c = childrenFor("/runtime")
            val html = c.map { sid => s"""<a href="/v1/service/$sid">$sid</a>"""}
            complete {
              s"""|Exception thrown\n
                |${e.getStackTrace.take(15).mkString("\n")}\n
                |Available service ids:\n
                |${html.mkString("\n")}
                |""".stripMargin
            }
        }
      }
    }
  }

  def serviceGetRoute(implicit system: ActorSystem) = {
    import system._
    get {
      path("service") {
        complete {
          import spray.json.DefaultJsonProtocol._
          import akka.http.marshallers.sprayjson._
          import SprayJsonSupport._
          childrenFor("/runtime")
        }
      }
    }
  }

  def childrenFor(path: String = "/runtime")(implicit system: ActorSystem) = {
    import system._
    val children = client.getChildren().forPath(path)
    import scala.collection.JavaConversions.asScalaBuffer
    asScalaBuffer(children).toSeq
  }
}
