package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.http.server.Server
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class IntegrateRemoveInactive {


    @BeforeEach
    fun before() {
        Constants.maintainIntervalSeconds = 1
        Constants.initialAddressSet = emptySet()
        Constants.maintainBlockchain = null
        Constants.maintainAddressStrategies = setOf(AddressMaintenanceStrategyEnum.RemoveUnresponsiveNodes)
        Constants.setRandom = Random(1)
        Constants.enableMaintainLoop = false
    }

    @Test
    fun testIntegrationSingle() {

        var portCounter: UShort = 9000U
        val listOfServers = mutableListOf<Server>(Server(portCounter, true).also { it.startServer() })
        listOfServers.first().enableMaintainLoop = true
        Constants.initialAddressSet = setOf(Address(portCounter++))

        Thread.sleep(200)
        repeat(10) {
            listOfServers += Server(portCounter++).also { it.startServer() }

            Thread.sleep(200)
        }

        for (server in listOfServers) {
            assertTrue { server.addressList.addressList.size >= Constants.subgraphConnectionPoints }
        }

        for (server in listOfServers - listOfServers.first()) {
            println("[Unit Test] Shutting down ${server.addressList.ownAddress}")
            server.stopServer()
            Thread.sleep(2000)
            assertFalse { listOfServers.first().addressList.addressList.contains(server.addressList.ownAddress) }
        }
    }


    @Test
    fun testIntegrationAll() {
        Constants.enableMaintainLoop = true

        var portCounter: UShort = 9500U
        val listOfServers = mutableListOf<Server>(Server(portCounter, true).also { it.startServer() })
        Constants.initialAddressSet = setOf(Address(portCounter++))

        Thread.sleep(200)
        repeat(10) {
            listOfServers += Server(portCounter++).also { it.startServer() }

            Thread.sleep(200)
        }

        for (server in listOfServers) {
            assertTrue { server.addressList.addressList.size >= Constants.subgraphConnectionPoints }
        }

        val runningServers = mutableListOf<Server>().also { it.addAll(listOfServers) }

        for (server in listOfServers - listOfServers.first()) {
            server.stopServer()
            runningServers -= server
            Thread.sleep(2000)
            runningServers.forEach {
                println("[Unit Test] Checking ${server.addressList.ownAddress} is not in ${it.addressList.ownAddress}'s address list (${it.addressList.addressList})")
                assertFalse { it.addressList.addressList.contains(server.addressList.ownAddress) }
            }
        }
    }
}