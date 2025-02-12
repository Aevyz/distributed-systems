package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address

object Constants {
    const val DIFFICULTY = 2

    const val HASH_ALGORITHM = "SHA3-512"

    const val ADDRESS_PROPAGATION_RANDOM_SAMPLE = 5

    val initialList = listOf(
        Address("localhost", 9000),
        Address("localhost", 9001),
        Address("localhost", 9002),
        Address("localhost", 9003)
    )
}