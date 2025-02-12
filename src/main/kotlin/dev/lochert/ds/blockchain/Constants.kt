package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address

object Constants {
    const val DIFFICULTY = 5

    const val HASH_ALGORITHM = "SHA3-512"

    const val ADDRESS_PROPAGATION_RANDOM_SAMPLE = 5

    val initialList = listOf(
        Address("localhost", 8080U),
    )
}