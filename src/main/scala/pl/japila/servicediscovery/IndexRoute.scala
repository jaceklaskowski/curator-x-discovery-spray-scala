package pl.japila.servicediscovery

import akka.actor.ActorSystem
import akka.http.marshallers.xml.ScalaXmlSupport
import akka.http.model.{MediaTypes, HttpEntity, HttpResponse}
import akka.http.server.Directives._
import akka.http.server._

/**
 * "Borrowed" from https://github.com/akka/akka/blob/akka-stream-and-http-experimental-1.0-M1/akka-http-tests/src/test/scala/akka/http/server/TestServer.scala
 */
trait IndexRoute {

  def index(system: ActorSystem): Route = {
    import system._
    path("") {
      get {
        import ScalaXmlSupport._
        complete(indexPage)
      }
    }
  }

  lazy val indexPage =
    <html>
      <body>
        <h1>Say hello to <i>ServiceDiscovery</i> service!</h1>
        <p>Defined resources:</p>
        <ul>
          <li><a href="/v1/service">/v1/service</a></li>
          <li><a href="/v1/service/serviceName/5">/v1/service/serviceName/5</a></li>
          <li><a href="/shutdown">/shutdown</a> aka SYSTEM DOWN - use with caution!</li>
        </ul>
      </body>
    </html>
}
