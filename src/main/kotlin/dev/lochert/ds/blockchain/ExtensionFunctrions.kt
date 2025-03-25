package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.block.BlockChain
import java.net.Inet4Address
import java.net.NetworkInterface

fun String.isAlphanumeric(): Boolean {
    return this.matches(Regex("^[a-zA-Z0-9]+$"))
}

fun getOwnIpAddress(): String {

    val interfaces = NetworkInterface.getNetworkInterfaces()
    for (networkInterface in interfaces) {
        for (address in networkInterface.inetAddresses) {
            if (!address.isLoopbackAddress && address is Inet4Address) {
                return address.hostAddress
            }
        }
    }
    throw IllegalStateException("No valid IP address found")
}

fun BlockChain.lastInfo() = "${this.lastHash().slice(0..4)} (${this.listOfBlocks.size})"

fun main() {
    println(getOwnIpAddress())
}