package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address
import java.net.InetAddress

object Constants {
    var DIFFICULTY = 2

    const val HASH_ALGORITHM = "SHA3-512"


    val initialAddressSet = setOf(
        Address(InetAddress.getLocalHost().hostName, 8080U),
    )

    var addressStrategy = AddressStrategyEnum.Subgraph

    // Address Search goes 3 deep or terminates at 20 connections
    var subgraphMaxDepth = 3
    var subgraphMaxSearch = 20

    // Connect at 3 points
    var subgraphConnectionPoints = 3
}



