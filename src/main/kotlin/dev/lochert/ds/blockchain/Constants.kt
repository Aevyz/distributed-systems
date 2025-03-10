package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address

object Constants {
    var DIFFICULTY = 2

    const val HASH_ALGORITHM = "SHA3-512"


    var initialAddressSet = setOf(
        Address(getOwnIpAddress(), 8080U), // Localhost Initial
        Address("172.18.0.2", 8080U), // Docker Initial
    )

    var addressStrategy = AddressStrategyEnum.Subgraph

    // Address Search goes 3 deep or terminates at 20 connections
    var subgraphMaxDepth = 3
    var subgraphMaxSearch = 20

    // Connect at 3 points
    var subgraphConnectionPoints = 3
}



