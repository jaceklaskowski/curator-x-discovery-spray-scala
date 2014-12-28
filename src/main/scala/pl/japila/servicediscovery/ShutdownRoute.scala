package pl.japila.servicediscovery

import akka.actor.ActorSystem
import akka.http.marshallers.xml.ScalaXmlSupport
import akka.http.server.Directives._
import akka.http.server._

trait ShutdownRoute {
  def shutdownGetRoute(system: ActorSystem): Route = {
    import system._
    get {
      path("shutdown") {
        system.shutdown()
        complete("Shutting down now...")
      }
    }
  }
}
