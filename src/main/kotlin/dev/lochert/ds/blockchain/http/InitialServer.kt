//package dev.lochert.ds.blockchain.http
//
//import com.sun.net.httpserver.HttpServer
//import dev.lochert.ds.blockchain.address.AddressList
//import dev.lochert.ds.blockchain.block.Block
//import dev.lochert.ds.blockchain.block.BlockChain
//import dev.lochert.ds.blockchain.http.handlers.*
//import java.net.InetAddress
//import java.net.InetSocketAddress
//
//// Partially inspired by ChatGPT
//@Deprecated("Use New Class")
//fun main(args: Array<String>) {
//    val port = if (args.isNotEmpty()) args[0].toInt() else 8080
//    val server = HttpServer.create(InetSocketAddress(port), 0)
//
//    val addressList = AddressList(mutableSetOf())
//
//
//    val blockChain = BlockChain(Block.genesisNode)
//    //TODO: Remove this for the final deploy
//    println("Populating Address and Block List")
//    repeat(4){
//        blockChain.addBlock(it.toString())
////        addressList.addAddress("demo-address", (it.toUShort()+10000U).toUShort())
//    }
//
//    // Address handlers (GET & POST)
//    server.createContext("/address", AddressHandler(addressList))
//
//    // Block handlers (GET & POST)
//    server.createContext("/block", BlockHandler(addressList, blockChain))
//
//    // Get a specific block by hash or by index (genesis block is 0)
//    server.createContext("/block/hash", BlockHandlerHash(blockChain))
//    server.createContext("/block/index", BlockHandlerIndex(blockChain))
//
//    // Send a node the instruction to add a block
//    // /control/add-block/bla (I was lazy and wanted to add blocks via HTTP Get)
//    server.createContext("/control/add-block", ControlAddHandler(addressList, blockChain))
//
//    // Sends a message to each node in the address list and asks them for their addresses
//    server.createContext("/control/populate-addresslist", ControlAddrPopulateHandler(blockChain))
//
//    server.executor = null
//    server.start()
//    println("Server started on port $port")
//}