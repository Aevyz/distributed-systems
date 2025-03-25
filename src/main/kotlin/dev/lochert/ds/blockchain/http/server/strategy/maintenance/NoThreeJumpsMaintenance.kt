package dev.lochert.ds.blockchain.http.server.strategy.maintenance

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.http.HttpUtil
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.SubgraphStrategy.addressMapToGraph
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.SubgraphStrategy.createAddressMap
import kotlinx.serialization.json.Json

class NoThreeJumpsMaintenance : AddressMaintenance {
    override fun maintain(addressList: AddressList) {
        val g = addressMapToGraph(createAddressMap(addressList.addressList + Constants.initialAddressSet, false))
        if (g.nodes.isEmpty()) {
            return
        }
        val selfNode = g.nodes.find { it.address == addressList.ownAddress }!!
        val directlyConnected = selfNode.neighbors.toSet()
        val level2Connected = directlyConnected.flatMap { directlyConnectedNode ->
            directlyConnectedNode.neighbors.map { it.address }
        }.toSet()

        val level3AndBeyond =
            g.nodes.map { it.address } - directlyConnected.map { it.address }.toSet() - level2Connected
        if (level3AndBeyond.isNotEmpty()) {

            val address = level3AndBeyond.sortedBy { it.toString() }.random(Constants.setRandom)
            try {
                val (_, _) = HttpUtil.sendPostRequest(
                    "http://${address.ip}:${address.port}/address",
                    Json.encodeToString(addressList.ownAddress)
                )
                addressList.addAddress(address)
            } catch (e: Exception) {
                println("(${addressList.ownAddress}) Encountered an error that prevented the adding of $address")
                e.printStackTrace()
            }
        }

    }
}