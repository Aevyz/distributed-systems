package dev.lochert.ds.blockchain.http.server.strategy.address.subgraph

import dev.lochert.ds.blockchain.address.Address
import java.net.InetAddress

object LastGraph {
    var lastGraph = "No Graph Generated So Far"

    fun updateGraph(){
        SubgraphStrategy.executeSubGraphStrategy(Address(InetAddress.getLocalHost().hostName, 8080U), update = false)
    }
}