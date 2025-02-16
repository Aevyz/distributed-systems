package dev.lochert.ds.blockchain.http

import com.sun.net.httpserver.HttpExchange
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URI

object HttpUtil {
    //Chat GPT
    fun sendPostRequest(url: String, jsonBody: String): Pair<Int, String> {
        val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.connectTimeout=5000
        connection.readTimeout=10000

        // Write JSON body to request
        val outputStream: OutputStream = connection.outputStream
        outputStream.write(jsonBody.toByteArray(Charsets.UTF_8))
        outputStream.flush()
        outputStream.close()

        // Read response
        val responseCode = connection.responseCode
        val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

//        println("Response Code: $responseCode")
//        println("Response: $responseMessage")
        return Pair(responseCode, responseMessage)
    }

    fun sendGetRequest(url: String): Pair<Int, String> {
        val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout=5000
        connection.readTimeout=10000
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json")

        // Read response
        val responseCode = connection.responseCode
        val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

//        println("Response Code: $responseCode")
//        println("Response: $responseMessage")
        return Pair(responseCode, responseMessage)
    }
    /**
     * Utility function to send an HTTP response.
     */
    fun sendResponse(exchange: HttpExchange, response: String, code: Int = 200) {
        exchange.sendResponseHeaders(code, response.toByteArray().size.toLong())
        val os: OutputStream = exchange.responseBody
        os.write(response.toByteArray())
        os.close()
    }
}