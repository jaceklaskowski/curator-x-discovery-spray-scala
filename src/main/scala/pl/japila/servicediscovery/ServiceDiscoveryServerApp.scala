package pl.japila.servicediscovery

import akka.actor.{Props, Actor, ExtendedActorSystem, ActorSystem}
import akka.http.model.{HttpResponse, HttpRequest}
import akka.http.server.Directives
import akka.stream.scaladsl.Flow
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.control.NonFatal

object ServiceDiscoveryServerApp extends App with Routes with Directives {

  val interface = if (args.length >= 1) args(0) else "localhost"
  val port = if (args.length >= 2) args(1).toInt else 8080
  println(s"interface=$interface port=$port")

  val conf: Config = ConfigFactory.parseString( """
    akka.loglevel         = INFO
    akka.log-dead-letters = off
                                                """)
  implicit val system = ActorSystem("ServiceDiscovery", conf)
  import system.dispatcher

  import akka.http.Http
  val binding = Http().bind(interface, port)

  import akka.stream.FlowMaterializer
  implicit val fm = FlowMaterializer()

  val route: Flow[HttpRequest, HttpResponse] = shutdownGetRoute ~ index ~ service
  binding startHandlingWith route
}
