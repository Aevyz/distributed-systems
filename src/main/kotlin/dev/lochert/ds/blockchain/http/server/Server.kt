package dev.lochert.ds.blockchain.http.server

import com.sun.net.httpserver.HttpServer
import dev.lochert.ds.blockchain.AddressStrategyEnum
import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.Transactions.Transaction
import dev.lochert.ds.blockchain.Transactions.Transactions
import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.block.Block
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.getOwnIpAddress
import dev.lochert.ds.blockchain.http.HttpUtil
import dev.lochert.ds.blockchain.http.handlers.*
import dev.lochert.ds.blockchain.http.server.strategy.address.naive.NaiveStrategy
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.SubgraphStrategy
import dev.lochert.ds.blockchain.isAlphanumeric
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress

class Server{

    val hostname: String = getOwnIpAddress()
    val port: UShort
    val addressList: AddressList
    var blockChain: BlockChain
    var transactions: Transactions
    // Initial Constructor
    constructor(){
        this.port = 8080U

        addressList= AddressList(Address(hostname, port))
        blockChain = BlockChain(Block.genesisNode)
        transactions = Transactions()
    }
    constructor(port:UShort, addressList: AddressList, blockChain: BlockChain) {
        this.port = port
        this.addressList = addressList
        this.blockChain = blockChain
    }

    constructor(port:UShort = 9999U) {
        this.port = port
        addressList = queryAddresses(Address(hostname, port))
        blockChain = queryBlockChain(addressList)
        transactions = Transactions()
    }
    fun queryAddresses(address: Address): AddressList {
        return when(Constants.addressStrategy){
            AddressStrategyEnum.Naive -> NaiveStrategy.queryAddressesNaively(address)
            AddressStrategyEnum.Subgraph -> SubgraphStrategy.executeSubGraphStrategy(address)
        }
    }
    fun addBlock(content: String){
        assert(content.isAlphanumeric())
        HttpUtil.sendGetRequest(addressList.ownAddress.toUrl("control", "add-block", content))
    }
    var httpServer:HttpServer? = null

    fun stopServer(){
        httpServer!!.stop(0)
    }
    fun startServer(){
        httpServer = HttpServer.create(InetSocketAddress(port.toInt()), 100)
        // Address handlers (GET & POST)
        httpServer!!.createContext("/address", AddressHandler(addressList))
        httpServer!!.createContext("/address-graph.svg", AddressGraphHandler(addressList))

        // Block handlers (GET & POST)
        httpServer!!.createContext("/block", BlockHandler(this, addressList, blockChain))

        // Get a specific block by hash or by index (genesis block is 0)
        httpServer!!.createContext("/block/hash", BlockHandlerHash(blockChain))
        httpServer!!.createContext("/block/index", BlockHandlerIndex(blockChain))

        // Get blocks starting from block with this hash
        httpServer!!.createContext("/block/hash/from", BlocksHandlerHash(blockChain))

        // Get all saved transactions
        httpServer!!.createContext( "/transactions/all", TransactionsHandler(transactions))

        httpServer!!.createContext( "/transactions/create", TransactionsHandler(transactions))

        // Send a node the instruction to add a block
        // /control/add-block/bla (I was lazy and wanted to add blocks via HTTP Get)
        httpServer!!.createContext("/control/add-block", ControlAddHandler(this, addressList, blockChain))

        // Sends a message to each node in the address list and asks them for their addresses
        httpServer!!.createContext("/control/populate-addresslist", ControlAddrPopulateHandler(blockChain))

        httpServer!!.executor = null
        httpServer!!.start()
        println("Server started on port http://$hostname:$port (http://localhost:$port)")
    }
    companion object{
        fun queryBlockChain(addressList: AddressList):BlockChain{
            println("Querying For Blocks (Longest Wins)")
            val blockChainsFound = addressList.addressList.mapNotNull {
                try {

                    val (_, rBody) = HttpUtil.sendGetRequest("http://${it.ip}:${it.port}/block")
                    val blocks = Json.decodeFromString<List<Block>>(rBody)
                    val blockChain = BlockChain(blocks.first()) // Genesis Block needs to be added like this

                    println("Success: http://${it.ip}:${it.port}/block returned ${blocks.size} blocks")
                    for (block in blocks) {
                        if(block==blocks.first()){ // Skip genesis block
                            continue
                        }
                        blockChain.addBlock(block) // Also Validates if Blocks are correct
                    }
                    blockChain

                } catch (e: Exception) {
                    println("Failed: http://${it.ip}:${it.port}/block with error ${e.javaClass}")
                    null
                }
            }
            return blockChainsFound.maxBy { it.listOfBlocks.size }
        }
    }

}