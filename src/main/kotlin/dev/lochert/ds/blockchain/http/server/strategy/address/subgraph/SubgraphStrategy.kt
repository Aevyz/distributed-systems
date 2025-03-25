package dev.lochert.ds.blockchain.http.server.strategy.address.subgraph

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.getOwnIpAddress
import dev.lochert.ds.blockchain.http.HttpUtil
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.Graph.Companion.printConnectedComponents
import kotlinx.serialization.json.Json

object SubgraphStrategy {
    fun queryForAddresses(listOfAddresses:Set<Address>):Map<Address, Set<Address>>{
        val rMap = mutableMapOf<Address, Set<Address>>()
        listOfAddresses.parallelStream().forEach { address ->
            try {
                val (_, rBody) = HttpUtil.sendGetRequest("http://${address.ip}:${address.port}/address")
                val receivedAddressList = Json.decodeFromString<List<Address>>(rBody)

                synchronized(rMap) {
                    rMap[address] = receivedAddressList.toSet()
                }

            } catch (e: Exception) {
                println("Failed to connect to http://${address.ip}:${address.port}/address")
            }
        }
        return rMap
    }

    fun createAddressMap(
        initialAddressSet: Set<Address> = Constants.initialAddressSet,
        ignoreLimit: Boolean = false
    ): MutableMap<Address, Set<Address>> {
        val discoveredAddresses = initialAddressSet.toMutableSet()
        val queriedAddresses = mutableSetOf<Address>()
        val addressMap = mutableMapOf<Address, Set<Address>>() // Store the merged results

        repeat(Constants.subgraphMaxDepth) {
            val newAddresses = discoveredAddresses - queriedAddresses
            // If Discovered Addresses are under the limit and we have new Addresses
            if ((ignoreLimit || discoveredAddresses.size < Constants.subgraphMaxSearch) && !newAddresses.isEmpty()) {
                val resultMap = queryForAddresses(newAddresses)
                queriedAddresses.addAll(newAddresses)

                // Merge new results into the main map
                resultMap.forEach { (address, connections) ->
                    addressMap[address] = connections
                    discoveredAddresses.addAll(connections)
                }
            }
        }
        return addressMap
    }

    fun executeSubGraphStrategy(
        ownAddress: Address,
        initialAddressSet: Set<Address> = Constants.initialAddressSet,
        update: Boolean = true,
        ignoreLimit: Boolean = false
    ): AddressList {

        val g = addressMapToGraph(createAddressMap(initialAddressSet, ignoreLimit))

        printConnectedComponents(g)
        LastGraph.lastGraph = g.toSVG()
        val connections = g.giveConnections()
        println("${ownAddress}: Connecting to ${connections.map { it.port }}")
        if(update){
            connections.forEach { address ->
                try{
                    val (_, _) = HttpUtil.sendPostRequest("http://${address.ip}:${address.port}/address", Json.encodeToString(ownAddress))
                }catch (e:Exception){
                    println("Failed to connect to http://${address.ip}:${address.port}/address")
                }
            }
        }

        return AddressList(ownAddress, connections)
    }

    fun addressMapToGraph(addressMap: Map<Address, Set<Address>>, ignoreAddresses: List<Address> = emptyList()): Graph {
        val g = Graph()
        val allNodes = mutableMapOf<Address, Node>()
        (addressMap.keys + addressMap.values.flatten()).toSet().forEach {
            if (!ignoreAddresses.contains(it)) {
                val node = Node(it)
                allNodes[it] = node
                g.addNode(node)
            }
        }


        addressMap.forEach { (key, addresses) ->
            if (!ignoreAddresses.contains(key)) {
                addresses.forEach {
                    if (!ignoreAddresses.contains(it)) {
                        g.connect(allNodes[key]!!, allNodes[it]!!)
                    }
                }
            }
        }
        return g
    }
}
fun main(){
    SubgraphStrategy.executeSubGraphStrategy(Address(getOwnIpAddress(), 8080U))
}