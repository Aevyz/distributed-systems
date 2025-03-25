package dev.lochert.ds.blockchain.http.server

import com.sun.net.httpserver.HttpServer
import dev.lochert.ds.blockchain.AddressStrategyEnum
import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.Transactions.Transactions
import dev.lochert.ds.blockchain.*
import dev.lochert.ds.blockchain.Constants.maintainIntervalSeconds
import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.block.Block
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.http.HttpUtil
import dev.lochert.ds.blockchain.http.handlers.*
import dev.lochert.ds.blockchain.http.server.strategy.address.naive.NaiveStrategy
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.SubgraphStrategy
import dev.lochert.ds.blockchain.http.server.strategy.maintenance.*
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress
import kotlin.concurrent.thread

class Server{

    val hostname: String = getOwnIpAddress()
    val port: UShort
    val addressList: AddressList
    var blockChain: BlockChain
    var enableMaintainLoop = Constants.enableMaintainLoop
    var maintainThread: Thread? = null
    var transactions: Transactions = Transactions()
    // Initial Constructor
    @Deprecated("Don't use this")
    constructor(){
        this.port = 8080U

        addressList= AddressList(Address(hostname, port))
        blockChain = BlockChain(Block.genesisNode)

    }
    constructor(port: UShort = 9999U, initial: Boolean = false) {
        this.port = port
        if (initial) {
            addressList = AddressList(Address(hostname, port))
            blockChain = BlockChain(Block.genesisNode)
        } else {

            addressList = queryAddresses(Address(hostname, port))
            blockChain = queryBlockChain(addressList)
        }
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
        maintainThread?.interrupt()
        httpServer!!.stop(0)
    }
    fun startServer(){
        httpServer = HttpServer.create(InetSocketAddress(port.toInt()), 100)
        httpServer!!.createContext("/", { HttpUtil.sendResponse(it, "Ok", 200) })
        // Address handlers (GET & POST)
        httpServer!!.createContext("/address", AddressHandler(addressList))
        httpServer!!.createContext("/address-graph.svg", AddressGraphHandler(addressList))

        // Block handlers (GET & POST)
        httpServer!!.createContext("/block", BlockHandler(this, addressList))

        // Get a specific block by hash or by index (genesis block is 0)
        httpServer!!.createContext("/block/hash", BlockHandlerHash(blockChain))
        httpServer!!.createContext("/block/index", BlockHandlerIndex(blockChain))

        // Get blocks starting from block with this hash
        httpServer!!.createContext("/block/hash/from", BlocksHandlerHash(blockChain))

        // Get all saved transactions
        httpServer!!.createContext( "/transactions/all", TransactionsHandler(addressList, transactions))
        // Generate a random transaction
        httpServer!!.createContext( "/transactions/create", TransactionsHandler(addressList, transactions))
        // post endpoint
        httpServer!!.createContext( "/transactions/post", TransactionsHandler(addressList, transactions))

        // Send a node the instruction to add a block
        // /control/add-block/bla (I was lazy and wanted to add blocks via HTTP Get)
        httpServer!!.createContext("/control/add-block", ControlAddHandler(this, addressList, blockChain, transactions))

        // Sends a message to each node in the address list and asks them for their addresses
        httpServer!!.createContext("/control/populate-addresslist", ControlAddrPopulateHandler(blockChain))

        httpServer!!.executor = null
        httpServer!!.start()
        println("Server started on port http://$hostname:$port (http://localhost:$port)")
        maintainThread = thread {
            try {
                println("[Maintain] [${addressList.ownAddress}] Maintain Thread with $enableMaintainLoop ($maintainIntervalSeconds)")
                while (enableMaintainLoop) {
                    println(maintainIntervalSeconds * 1000L)
                    Thread.sleep(maintainIntervalSeconds * 1000L)
                    println("[Maintain] [${addressList.ownAddress}] Loop Awake")
                    if (Constants.maintainAddressStrategies.contains(AddressMaintenanceStrategyEnum.RemoveUnresponsiveNodes)) {
                        println("[Maintain] [${addressList.ownAddress}] Removing Unresponsive Nodes")
                        RemoveInactiveMaintenance().maintain(addressList)
                    }
                    if (Constants.maintainAddressStrategies.contains(AddressMaintenanceStrategyEnum.SubgraphConnection)) {
                        println("[Maintain] [${addressList.ownAddress}] Subgraph Connections")
                        ConnectionPointMaintenance().maintain(addressList)
                    }
                    if (Constants.maintainAddressStrategies.contains(AddressMaintenanceStrategyEnum.NoGreaterThirdDegree)) {
                        println("[Maintain] [${addressList.ownAddress}] No Three Jump")
                        NoThreeJumpsMaintenance().maintain(addressList)
                    }
                    if (Constants.maintainAddressStrategies.contains(AddressMaintenanceStrategyEnum.BackupToFile)) {
                        println("[Maintain] [${addressList.ownAddress}] Backing Up to File")
                        BackupAddressMaintenance().maintain(addressList)
                    }
                    when (Constants.maintainBlockchain) {
                        BlockchainMaintenanceStrategyEnum.ImmediateLongestLowestHash -> {
                            val currentBlockchain = blockChain
                            val proposalBlockchain = ImmediateLongestBlockchainLowestHash().maintain(addressList)

                            println("[Maintain] [${addressList.ownAddress}] Should Update Blockchain? ${currentBlockchain.lastInfo()} vs ${proposalBlockchain?.lastInfo()}")

                            if (proposalBlockchain != null) {
                                if (currentBlockchain.listOfBlocks.size < proposalBlockchain.listOfBlocks.size) {
                                    blockChain = proposalBlockchain
                                    println("[Maintain] [${addressList.ownAddress}]: Replace due to size (new = ${proposalBlockchain.lastInfo()})")
                                    continue
                                }
                                if (currentBlockchain.listOfBlocks.size == proposalBlockchain.listOfBlocks.size) {
//                                    println("${proposalBlockchain.lastHash().compareTo(currentBlockchain.lastHash()) < 0} ${proposalBlockchain.lastHash() < currentBlockchain.lastHash()}")

                                    if (proposalBlockchain.lastHash().compareTo(currentBlockchain.lastHash()) < 0) {
                                        println("[Maintain] [${addressList.ownAddress}]: Replace due to hash (size same) (new = ${proposalBlockchain.lastInfo()})")
                                        blockChain = proposalBlockchain
                                        continue
                                    }
                                }
                                println("[Maintain] [${addressList.ownAddress}] Has Longest Chain")
                            }
                        }

                        null -> continue
                    }


                }
            } catch (e: InterruptedException) {
                println("[Maintain] [${addressList.ownAddress}] [${addressList.ownAddress}] Shutting Down due to Interrupt")
            }

        }
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
            return blockChainsFound
                .sortedWith(compareByDescending<BlockChain> { it.listOfBlocks.size }.thenBy { it.listOfBlocks.last().blockHash })
                .first()
        }
    }

}