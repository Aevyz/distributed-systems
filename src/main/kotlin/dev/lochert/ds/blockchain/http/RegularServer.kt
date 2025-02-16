//package dev.lochert.ds.blockchain.http
//
//
//import com.sun.net.httpserver.HttpServer
//import dev.lochert.ds.blockchain.Constants
//import dev.lochert.ds.blockchain.address.Address
//import dev.lochert.ds.blockchain.address.AddressList
//import dev.lochert.ds.blockchain.block.Block
//import dev.lochert.ds.blockchain.block.BlockChain
//import dev.lochert.ds.blockchain.http.handlers.*
//import kotlinx.serialization.json.Json
//import java.net.InetAddress
//import java.net.InetSocketAddress
//import kotlin.random.Random
//
//
//// Partially inspired by ChatGPT
//fun main(args: Array<String>) {
//    val port = if (args.isNotEmpty()) args[0].toInt() else 9000+ Random.nextInt(1000)
//
//
//    fun queryAddresses(thisAddress:Address) : AddressList{
//        println("Querying for Addresses")
//        val addressList = AddressList()
//        addressList.addressList.addAll(Constants.initialList)
//
//        Constants.initialList.parallelStream().forEach {
//            try{
//                val (_, rBody) = HttpUtil.sendPostRequest("http://${it.ip}:${it.port}/address", Json.encodeToString(thisAddress))
//                val receivedAddressList = Json.decodeFromString<List<Address>>(rBody)
//
//                synchronized(addressList){
//                    addressList.addressList.addAll(receivedAddressList)
//                }
//
//            }catch (e:Exception){
//                println("Failed to connect to http://${it.ip}:${it.port}/address")
//            }
//        }
//        return addressList
//    }
//    fun queryBlockChain(addressList: AddressList):BlockChain{
//        println("Querying For Blocks (Longest Wins)")
//        val blockChainsFound = addressList.addressList.mapNotNull {
//            try {
//                if(port.toUShort() ==it.port && it.ip=="localhost"){
//                    null
//                }else {
//                    val (_, rBody) = HttpUtil.sendGetRequest("http://${it.ip}:${it.port}/block")
//                    val blocks = Json.decodeFromString<List<Block>>(rBody)
//                    val blockChain = BlockChain(blocks.first()) // Genesis Block needs to be added like this
//
//                    println("Success: http://${it.ip}:${it.port}/block returned ${blocks.size} blocks")
//                    for (block in blocks) {
//                        if(block==blocks.first()){ // Skip genesis block
//                            continue
//                        }
//                        blockChain.addBlock(block) // Also Validates if Blocks are correct
//                    }
//                    blockChain
//                }
//            } catch (e: Exception) {
//                println("Failed: http://${it.ip}:${it.port}/block with error ${e.javaClass}")
//                null
//            }
//        }
//        return blockChainsFound.maxBy { it.listOfBlocks.size }
//    }
//
//    val hostname = InetAddress.getLocalHost().hostName
//    val addressList = queryAddresses(Address(hostname, port.toUShort()))
//    val blockChain = queryBlockChain(addressList)
//
//
//
//    val server = HttpServer.create(InetSocketAddress(port), 0)
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
//    server.createContext("/control/force/repopulate-address", ControlAddrPopulateHandler(blockChain))
//    // Sends a message to each node in the address list and asks them for their addresses
//    server.createContext("/control/force/search-blockchain", ControlAddrPopulateHandler(blockChain))
//
//    server.executor = null
//    server.start()
//    println("Server started on port http://$hostname:$port")
//}
