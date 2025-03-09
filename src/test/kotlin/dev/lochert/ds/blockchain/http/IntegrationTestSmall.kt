package dev.lochert.ds.blockchain.http

import dev.lochert.ds.blockchain.http.server.Server
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.LastGraph
import org.junit.jupiter.api.Test
import java.net.InetAddress
import kotlin.concurrent.thread

object IntegrationUtils{
    var c:UShort = 0U
    fun startInitialServer(): Server {
        return Server().also { it.startServer() }
    }
    fun startSecondaryServers(): Server{
        return Server((9000U+ c++).toUShort()).also { it.startServer() }
    }
}
class IntegrationTestSmall {

    val repetitions = 15
    @Test
    fun testAttempt(){
        val threadList = mutableListOf<Thread>()
        threadList+=thread{
            IntegrationUtils.startInitialServer()
        }
        repeat(15){

            Thread.sleep(100)
            threadList+=thread{
                IntegrationUtils.startSecondaryServers()
            }
        }
        Thread.sleep(500)
        LastGraph.updateGraph()
        for(i in 9000 .. 9000+repetitions-1){
            HttpUtil.sendGetRequest("http://${InetAddress.getLocalHost().hostName}:$i/control/add-block/$i")
            Thread.sleep(200)
        }
        Thread.sleep(60000)
        threadList.forEach { it.interrupt() }
        threadList.forEach { it.join() }


    }
}