package dev.lochert.ds.blockchain.http.server.regular

import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.http.server.Server
import java.net.InetAddress

import java.net.ServerSocket

var counter: UShort = 9000U

fun main() {
    try {
        val ownPort = counter
        if (isPortAvailable(ownPort.toInt())) {
            // If the port is available, start the server
            Server(ownPort).startServer()
        } else {
            throw Exception("Port $ownPort is already in use")
        }
    } catch (e: Exception) {
        println("Trying next port ${++counter}")
        main()
    }
}


// Function to check if the port is available
fun isPortAvailable(port: Int): Boolean {
    return try {
        val serverSocket = ServerSocket(port)
        serverSocket.close()  // Close the socket immediately if the port is available
        true  // Port is available
    } catch (e: Exception) {
        false  // Port is already in use
    }
}
