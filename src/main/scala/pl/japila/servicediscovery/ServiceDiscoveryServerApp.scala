package pl.japila.servicediscovery

import akka.actor.ActorSystem
import akka.http.marshallers.xml.ScalaXmlSupport
import akka.http.model.{HttpResponse, HttpRequest}
import akka.http.server.Directives
import akka.stream.scaladsl.Flow
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

import scala.util.Try

object ServiceDiscoveryServerApp extends App with Routes {

  val interface = if (args.length >= 1) args(0) else "localhost"
  val port = if (args.length >= 2) args(1).toInt else 8080
  println(s"interface=$interface port=$port")

  val conf: Config = ConfigFactory.parseString( """
    akka.loglevel = INFO
    akka.log-dead-letters = off
                                                """)
  implicit val system = ActorSystem("ServiceDiscovery", conf)
  import system.dispatcher

  import akka.http.Http
  val binding = Http().bind(interface = interface, port = port)

  import akka.stream.FlowMaterializer

  import Directives._
  val route = shutdownGetRoute(system) ~ index(system) ~ service(system)
  implicit val fm = FlowMaterializer()
  val materializedMap = binding startHandlingWith route

}
