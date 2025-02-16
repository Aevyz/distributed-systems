package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import kotlinx.serialization.json.Json

// Partially inspired by ChatGPT
/**
 * Handles `/address` endpoint
 * - GET: Returns all addresses
 * - POST: Placeholder for adding an address
 */
class AddressHandler(val addressList: AddressList) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        println("${addressList.ownAddress}: Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")
        when (exchange.requestMethod) {
            "GET" -> handleGet(exchange)
            "POST" -> handlePost(exchange)
            else -> sendResponse(exchange, "Method Not Allowed", 405)
        }
    }

    private fun handleGet(exchange: HttpExchange) {
        try{
            val response = Json.encodeToString(addressList.addressList) // Convert addresses to JSON
            sendResponse(exchange, response, 200)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun handlePost(exchange: HttpExchange) {
        try {
            val requestBody = exchange.requestBody.bufferedReader().use { it.readText() }
            val addressAsObj = Json.decodeFromString<Address>(requestBody)
            addressList.addAddress(addressAsObj)
            sendResponse(exchange, Json.encodeToString(addressList.addressList), 201)
        }
        catch(e:Exception){
            sendResponse(exchange, "Encountered error: ${e.javaClass} - Most likely due to invalid Port", 400)
        }

    }
}