package dev.lochert.ds.blockchain.http.server.regular

import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.http.server.Server
import java.net.InetAddress


var counter:UShort = 9000U
fun main() {
    try {
        val ownPort = counter
        Server(ownPort).startServer()
    }
    catch (e:Exception){
        counter++
        println("Trying next port $counter")
        main()
    }
}