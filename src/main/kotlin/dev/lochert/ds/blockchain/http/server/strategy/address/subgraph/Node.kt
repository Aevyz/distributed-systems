package dev.lochert.ds.blockchain.http.server.strategy.address.subgraph

import dev.lochert.ds.blockchain.address.Address

// This code has been generated with the assistance of ChatGPT
data class Node(val address: Address, val neighbors:MutableList<Node> = mutableListOf()){
    override fun toString(): String {
        return "Node(address=$address, neighbors=${neighbors.map { it.address }})"
    }

    // We need to override hashCode and Equals, or else we get a stack overflow when querying neighbor!

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        return result
    }

}
