package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import dev.lochert.ds.blockchain.http.server.Server
import dev.lochert.ds.blockchain.pki.RSAKeyPairs
import kotlinx.serialization.json.Json

open class BalanceHandler(val server: Server) : HttpHandler {

    override fun handle(exchange: HttpExchange) {
        val map = mutableMapOf<String, Int>()
        println("TODO: Add Logic to count transactions here")
        val unknownMiner = "Unknown Miners (Pub Key not in Database)"
        for (block in server.blockChain.listOfBlocks) {
            if (block.miner !in map) {
                map[block.miner] = Constants.miningReward
            } else {
                map[block.miner] = map[block.miner]!! + Constants.miningReward
            }
        }
        println(map)
        val response = Json.encodeToString(
            map.map { (key, value) ->
                BalanceOutput(value, RSAKeyPairs.getRSAKeyPair(key)?.toString() ?: unknownMiner, key)
            }
        )
        println(response)


        sendResponse(exchange, response, 200)
    }
}

data class BalanceOutput(val balance: Int, val shortString: String, val publicKeyString: String)