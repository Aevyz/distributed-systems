package dev.lochert.ds.blockchain.http

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.block.Block
import dev.lochert.ds.blockchain.http.server.Server
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class IntegrationTest{
    val waitDuration:Long = 500
    val longWaitDuration:Long = 1000

    @BeforeEach
    fun setUp() {
        Constants.DIFFICULTY = 1
    }

    @AfterEach
    fun tearDown() {
        println("Sleeping for .5s to release port")
        Thread.sleep(500)
    }

    @Test
    fun MultipleServerTestSend() {
        val initialServer = Server()
        Thread.sleep(1000)
        val threads = mutableListOf<Thread>()
        val servers = mutableListOf(initialServer)

        try {
            // Start initial server and wait for it to fully start
            val initialThread = thread {
                println("Starting initial server...")
                initialServer.startServer()
            }
            initialThread.join() // Wait for initial server

            val regularServers = mutableListOf<Server>()
            repeat(101){
                val server = Server((9000U+it.toUInt()).toUShort())

                val t = thread {
                    println("Starting server on port ${server.port}")
                    server.startServer()
                }
                threads.add(t)
                regularServers+=server

            }


            assertAddressListDoesNotContainSelf(servers)
            assertAllServersSameBlocks(servers)

            assertAddressListContains(initialServer, *regularServers.toTypedArray())

            servers[0].addBlock("Initial8080")
            assertAllServersSameBlocksLocal(servers)


            regularServers.forEach {
                println("\n" +
                        "\n" +
                        "\nSending ${it.port}")
                it.addBlock(it.port.toString())
                Thread.sleep(3000)
                assertAllServersSameBlocksLocal(servers)
            }




        } catch (e: Exception) {
            println("Test failed: ${e.message}")
        } finally {
            Thread.sleep(100000)
            assertAllServersSameBlocksLocal(servers)
            println("Stopping all servers...")
//            initialServer.stopServer()
            servers.forEach { it.stopServer() }
            threads.forEach { it.interrupt() }
            threads.forEach { it.join() }
        }
    }
    private fun assertAllServersSameBlocksLocal(servers: List<Server>){
        val reference = servers.first().blockChain
        for(server in servers){
            assertEquals(reference.listOfBlocks.size, server.blockChain.listOfBlocks.size)
            reference.listOfBlocks.forEachIndexed { index, _ ->
                assertEquals(reference.listOfBlocks[index], server.blockChain.listOfBlocks[index])
            }
        }
    }
    private fun assertAllServersSameBlocks(servers:List<Server>){
        println("Asserting All Blocks are the same")
        val eachDevicesBlocks = servers.map {
            println("\tSending to ${it.addressList.ownAddress.toUrl("block")}")
            Pair(
                it.addressList.ownAddress,
                Json.decodeFromString<List<Block>>(HttpUtil.sendGetRequest(it.addressList.ownAddress.toUrl("block")).second))
        }.toList()
        val reference = eachDevicesBlocks.first()
        eachDevicesBlocks.forEach {
            assertEquals(reference.second.size, it.second.size)
            for(i in 0 until reference.second.size){
                assertEquals(reference.second[i], it.second[i])
            }
        }
    }

    private fun assertAddressListDoesNotContainSelf(servers: List<Server>){
        println("Asserting Address List Not Contain Self")
        servers.forEach {
            assertFalse { it.addressList.addressList.contains(it.addressList.ownAddress) }
        }
    }
    private fun assertAddressListContains(server: Server, vararg addresses:Address){
        println("Asserting Address List")
        val addressesFoundOnServer = Json.decodeFromString<List<Address>>(
            HttpUtil.sendGetRequest(server.addressList.ownAddress.toUrl("address")).second)
        addresses.forEach {
            assertContains(addressesFoundOnServer, it)
        }
    }
    private fun assertAddressListContains(server: Server, vararg servers:Server){
        assertAddressListContains(server, *servers.map { it.addressList.ownAddress }.toTypedArray())
    }



}