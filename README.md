curator-x-discovery-spray-scala
===============================

curator-x-discovery project from Apache Curator with Spray and Scala

Build the assembly with `assemble`. Run `zkServer start` in a console and then the project as follows:

    java -jar target/scala-2.11/curator-x-discovery-spray-scala-assembly-1.0.jar

You should see on the console:

    [main-EventThread] INFO org.apache.curator.framework.state.ConnectionStateManager - State change: CONNECTED
    interface=localhost port=8080
    [INFO] [12/21/2014 22:52:57.811] [default-akka.actor.default-dispatcher-3] [akka://default/user/IO-HTTP/listener-0] Bound to localhost/127.0.0.1:8080

## Operations - REST API

* GET: Retrieve the information identified by the URL resource e.g. GET a particular Web page or image.
* HEAD: Identical to GET except that the server returns header information only, not the actual information identified by the URL resource. Useful to obtain metainformation about the entity implied by the request without transferring the entity-body itself. Often used to test hypertext links for validity, accessibility, and recent modification.
* POST: Submit data to the Web server such as 1) post a message to a bulletin board, newsgroup or mailing list, 2) provide input data - typically from a CGI form - to a data-handling process, 3) add a record directly to a database.
* PUT: Set (place/replace) the data for a particular URL to the new data submitted by the client. For example, upload a new Web page to a server.
* DELETE: Remove the data associated with the URL resource. For example, delete a Web page.

`/v1/service/`

<table>
    <tr>
        <td>Method</td>
        <td>Action</td>
        <td>Return Code</td>
    </tr>
    <tr>
        <td>GET</td>
        <td>Returns the available services</td>
        <td>200</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>Unsupported</td>
        <td>405</td>
    </tr>
    <tr>
        <td>PUT</td>
        <td>&nbsp;</td>
        <td>405</td>
    </tr>
    <tr>
        <td>DELETE</td>
        <td>&nbsp;</td>
        <td>405</td>
    </tr>
</table>

* Sample request

        curl -v -XPOST -H "Content-type: application/json" -d "{'zzz'}" http://localhost:8080/itcfg/v1/service/

* Sample response
