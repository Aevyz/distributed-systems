package dev.lochert.ds.blockchain.http.server

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.LastGraph
import kotlin.concurrent.thread
import kotlin.random.Random

fun main(args: Array<String>) {
    val initialAddress = Address("initial", 8080U)
    Constants.initialAddressSet = setOf(initialAddress)
    println("Args: ${args.contentToString()}")

    thread {
        while (true) {
            Thread.sleep(10000)
            LastGraph.updateGraph(initialAddress)
        }
    }
    if (args.isEmpty()) {
        Server().startServer()
    } else {
        Thread.sleep(1000 + Random.nextLong(10000))
        Server(args[0].toUShort()).startServer()
    }

}