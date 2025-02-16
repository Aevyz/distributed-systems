package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address
import java.net.InetAddress

object Constants {
    var DIFFICULTY = 2

    const val HASH_ALGORITHM = "SHA3-512"

    const val ADDRESS_PROPAGATION_RANDOM_SAMPLE = 5

    val initialList = listOf(
        Address(InetAddress.getLocalHost().hostName, 8080U),
    )
}