package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.http.server.Server
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals


class IntegrationSubgraph {


    @BeforeEach
    fun before() {
        Constants.maintainIntervalSeconds = 1
        Constants.initialAddressSet = emptySet()
        Constants.maintainBlockchain = null
        Constants.maintainAddressStrategies = setOf(AddressMaintenanceStrategyEnum.SubgraphConnection)
        Constants.setRandom = Random(1)
        Constants.enableMaintainLoop = false

        println(
            "\n" +
                    "\n" +
                    "\n" +
                    "\nNEW TEST\n\n\n\n"
        )
    }

    @Test
    fun noErrorNone() {
        var portCounter: UShort = 9000U
        Constants.enableMaintainLoop = true
        val a = Server(portCounter++, true)
        a.startServer()
        Thread.sleep(2000)

        assertEquals(0, a.addressList.addressList.size)

        a.stopServer()
        Thread.sleep(500)


    }

    @Test
    fun noErrorTwo() {
        var portCounter: UShort = 9100U
        Constants.enableMaintainLoop = true
        val a = Server(portCounter++, true)
        val b = Server(portCounter++, true)
        val c = Server(portCounter++, true)

        a.addressList.addAddress(b.addressList.ownAddress)
        a.addressList.addAddress(c.addressList.ownAddress)
        b.addressList.addAddress(a.addressList.ownAddress)
        b.addressList.addAddress(c.addressList.ownAddress)
        c.addressList.addAddress(a.addressList.ownAddress)
        c.addressList.addAddress(b.addressList.ownAddress)

        assertEquals(2, a.addressList.addressList.size)
        assertEquals(2, b.addressList.addressList.size)
        assertEquals(2, c.addressList.addressList.size)

        a.startServer()
        b.startServer()
        c.startServer()



        Thread.sleep(2000)
        a.stopServer()
        b.stopServer()
        c.stopServer()
        Thread.sleep(500)
    }

    @Test
    fun noErrorOne() {
        var portCounter: UShort = 9200U
        Constants.enableMaintainLoop = true
        val a = Server(portCounter++, true)
        val b = Server(portCounter++, true)

        a.addressList.addAddress(b.addressList.ownAddress)
        b.addressList.addAddress(a.addressList.ownAddress)


        a.startServer()
        b.startServer()

        Thread.sleep(2000)
        assertEquals(1, a.addressList.addressList.size)
        assertEquals(1, b.addressList.addressList.size)
        a.stopServer()
        b.stopServer()
        Thread.sleep(500)
    }

    @Test
    fun testIntegration() {
        val a = Server(8000U, initial = true)
        val b = Server(8001U, initial = true)
        val c = Server(8002U, initial = true)
        val d = Server(8003U, initial = true)
        val e = Server(8004U, initial = true)

        a.addressList.addAddress(b.addressList.ownAddress)
        a.addressList.addAddress(c.addressList.ownAddress)
        a.addressList.addAddress(d.addressList.ownAddress)
        a.addressList.addAddress(e.addressList.ownAddress)

        b.addressList.addAddress(a.addressList.ownAddress)
        b.addressList.addAddress(c.addressList.ownAddress)
        b.addressList.addAddress(d.addressList.ownAddress)

        c.addressList.addAddress(a.addressList.ownAddress)
        c.addressList.addAddress(b.addressList.ownAddress)
        c.addressList.addAddress(d.addressList.ownAddress)


        d.addressList.addAddress(a.addressList.ownAddress)
        d.addressList.addAddress(b.addressList.ownAddress)
        d.addressList.addAddress(c.addressList.ownAddress)

        e.addressList.addAddress(a.addressList.ownAddress)
        e.addressList.addAddress(a.addressList.ownAddress)
        e.enableMaintainLoop = true

        a.startServer()
        b.startServer()
        c.startServer()
        d.startServer()
        e.startServer()

        Thread.sleep(200)
        assertEquals(4, a.addressList.addressList.size)
        assertEquals(3, b.addressList.addressList.size)
        assertEquals(3, c.addressList.addressList.size)
        assertEquals(3, d.addressList.addressList.size)

        assertEquals(1, e.addressList.addressList.size)
        Thread.sleep(1000)


        assertEquals(4, a.addressList.addressList.size)
        assertEquals(4, b.addressList.addressList.size)
        assertEquals(4, c.addressList.addressList.size)
        assertEquals(3, d.addressList.addressList.size)

        assertEquals(3, e.addressList.addressList.size)

        a.stopServer()
        b.stopServer()
        c.stopServer()
        d.stopServer()
        e.stopServer()

        Thread.sleep(500)
    }
}