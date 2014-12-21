curator-x-discovery-spray-scala
===============================

curator-x-discovery project from Apache Curator with Spray and Scala

Build the assembly with `assemble`. Run `zkServer start` in a console and then the project as follows:

    java -jar target/scala-2.11/curator-x-discovery-spray-scala-assembly-1.0.jar

You should see on the console:

    [main-EventThread] INFO org.apache.curator.framework.state.ConnectionStateManager - State change: CONNECTED
    interface=localhost port=8080
    [INFO] [12/21/2014 22:52:57.811] [default-akka.actor.default-dispatcher-3] [akka://default/user/IO-HTTP/listener-0] Bound to localhost/127.0.0.1:8080
