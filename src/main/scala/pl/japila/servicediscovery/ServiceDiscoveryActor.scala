package pl.japila.servicediscovery

import akka.actor.{Props, Actor, ActorLogging}
import akka.stream.actor.{ZeroRequestStrategy, RequestStrategy, ActorSubscriber}
import akka.stream.scaladsl.{ImplicitFlowMaterializer, Source}

class ServiceDiscoveryActor(input: List[String]) extends ActorSubscriber with ActorLogging with ImplicitFlowMaterializer {
  import ServiceDiscoveryActor._

  override protected def requestStrategy: RequestStrategy = ZeroRequestStrategy

  val flow = Source(input).map(_.toUpperCase)

  def receive = {
    case "run" =>
      import akka.pattern.pipe
      import context.dispatcher
      log.debug("run flow executed")
      flow.fold("")(_ + _) pipeTo sender()
    case m => println(s"$m received")
  }
}

object ServiceDiscoveryActor {
  val props = Props(classOf[ServiceDiscoveryActor], List("a", "b", "c"))
}
