package dev.lochert.ds.blockchain.http

import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URI

object HttpUtil {

    fun sendPostRequest(url: String, jsonBody: String): Pair<Int, String> {
        val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        // Write JSON body to request
        val outputStream: OutputStream = connection.outputStream
        outputStream.write(jsonBody.toByteArray(Charsets.UTF_8))
        outputStream.flush()
        outputStream.close()

        // Read response
        val responseCode = connection.responseCode
        val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

        println("Response Code: $responseCode")
        println("Response: $responseMessage")
        return Pair(responseCode, responseMessage)
    }

    fun sendGetRequest(url: String): Pair<Int, String> {
        val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json")

        // Read response
        val responseCode = connection.responseCode
        val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

        println("Response Code: $responseCode")
        println("Response: $responseMessage")
        return Pair(responseCode, responseMessage)
    }

}