package dev.lochert.ds.blockchain.http.server.strategy.address.subgraph

import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.getOwnIpAddress

object LastGraph {
    var lastGraph = "No Graph Generated So Far"

    fun updateGraph(address: Address = Address(getOwnIpAddress(), 8080U)) {
        SubgraphStrategy.executeSubGraphStrategy(address, update = false)
    }
}