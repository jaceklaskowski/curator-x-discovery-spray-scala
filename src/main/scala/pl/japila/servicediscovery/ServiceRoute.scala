package pl.japila.servicediscovery

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.http.marshalling.ToResponseMarshallable
import akka.http.model.MediaTypes
import akka.http.model.StatusCodes._
import akka.http.server.Directives._
import akka.http.server.PathMatchers.Segment
import akka.http.server._
import akka.stream.FlowMaterializer
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode
import spray.json.DefaultJsonProtocol

import scala.util.Try

trait ServiceRoute {
  val hosts = "127.0.0.1:2181"
  implicit lazy val client = createClient(hosts)
  client.start

  Seq("/runtime", "/configuration", "/licences").foreach(createPath)

  val sid = java.util.UUID.randomUUID()
  registerService(sid)

  def createClient(hosts: String): CuratorFramework = {
    val zookeeperConnectionString = hosts
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy)
  }

  def createPath(p: String)(implicit c: CuratorFramework) = {
    Try(client.create().creatingParentsIfNeeded().forPath(p))
  }

  def registerService(sid: UUID)(implicit client: CuratorFramework) = {
    val sName = "My brand new service".toCharArray.map(_.toByte)
    val sPath = "/runtime/" + sid
    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(sPath, sName)
  }

  def service(implicit system: ActorSystem, mat: FlowMaterializer): Route =
    pathPrefix("v1" / "service") {
      val a = system.actorOf(ServiceDiscoveryActor.props)
      serviceGetMeta(a) ~ serviceGetIdRoute ~ serviceGetRoute
    }

  def serviceGetIdRoute(implicit system: ActorSystem) = {
    import system._
    get {
      path(JavaUUID) { serviceId =>
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

  def serviceGetRoute(implicit system: ActorSystem, mat: FlowMaterializer) = {
    import system._
    get {
      complete {
        import akka.http.marshalling._
        // CLEVER: apply is implicit
        import akka.http.marshalling.ToResponseMarshallable._
        import akka.http.marshallers.sprayjson.SprayJsonSupport._
        // Needed for default JSON protocol implicits
        import DefaultJsonProtocol._
        childrenFor("/runtime")
      }
    } ~ post {
      import akka.http.marshallers.sprayjson.SprayJsonSupport._
      import ServiceJsonProtocol._
      entity(as[Service]) { service =>
        complete {
          service
        }
      }
    }
  }

  case class Service(name: String)

  object ServiceJsonProtocol extends DefaultJsonProtocol {
    implicit val serviceFormat = jsonFormat1(Service)
  }

  def serviceGetMeta(a: ActorRef)(implicit system: ActorSystem) = {
    import system._
    get {
      path("meta") {
        complete {
          import akka.http.marshallers.sprayjson.SprayJsonSupport._
          import ServiceJsonProtocol._
          Service("hello world")
        }
      }
    }
  }

  def childrenFor(path: String = "/runtime")(implicit system: ActorSystem) = {
    import system._
    val children = client.getChildren().forPath(path)
    import scala.collection.JavaConversions.asScalaBuffer
    asScalaBuffer(children).toArray
  }
}
