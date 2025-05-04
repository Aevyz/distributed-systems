package dev.lochert.ds.blockchain.http

import dev.lochert.ds.blockchain.getOwnIpAddress
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.LastGraph
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class IntegrationTestLarge {
    var repetitions = 500

    @Test
    fun testAttempt(){
        val threadList = mutableListOf<Thread>()
        threadList+=thread{
            IntegrationUtils.startInitialServer()
        }

        repeat(repetitions){

            Thread.sleep(100)
            threadList+=thread{
                IntegrationUtils.startSecondaryServers()
            }
        }
        Thread.sleep(500)
        LastGraph.updateGraph()
        for(i in 9000 .. 9000+(repetitions-1)){
            HttpUtil.sendGetRequest("http://${getOwnIpAddress()}:$i/control/add-block/$i")
            Thread.sleep(400)
        }
        Thread.sleep(60000)
        threadList.forEach { it.interrupt() }
        threadList.forEach { it.join() }


    }
}