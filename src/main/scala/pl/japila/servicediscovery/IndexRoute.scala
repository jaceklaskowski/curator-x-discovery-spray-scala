package pl.japila.servicediscovery

import akka.http.model.{MediaTypes, HttpEntity, HttpResponse}

/**
 * "Borrowed" from https://github.com/akka/akka/blob/releasing-akka-stream-and-http-experimental-0.11/akka-http-tests/src/test/scala/akka/http/server/TestServer.scala
 */
trait IndexRoute {
  lazy val index =
    <html>
      <body>
        <h1>Say hello to <i>ServiceDiscovery</i> service!</h1>
        <p>Defined resources:</p>
        <ul>
          <li><a href="/v1/service/serviceName/5">/v1/service/serviceName/5</a></li>
        </ul>
      </body>
    </html>
}
