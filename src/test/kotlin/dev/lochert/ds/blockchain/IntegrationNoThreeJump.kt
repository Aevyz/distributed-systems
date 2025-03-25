package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.http.server.Server
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals


class IntegrationNoThreeJump {
    @BeforeEach
    fun before() {
        Constants.maintainIntervalSeconds = 1
        Constants.initialAddressSet = emptySet()
        Constants.maintainBlockchain = null
        Constants.maintainAddressStrategies = setOf(AddressMaintenanceStrategyEnum.NoGreaterThirdDegree)
        Constants.setRandom = Random(1)
        Constants.enableMaintainLoop = false
    }

    @Test
    fun testRandom() {
        val listOfInt = mutableListOf(1, 2, 3, 4, 5, 6)

        repeat(10) {
            println(listOfInt.shuffled(Constants.setRandom))
        }
    }

    @Test
    fun noCrashSolo() {
        val a = Server(7000U, initial = true)
        a.enableMaintainLoop = true
        a.startServer()
        assertEquals(0, a.addressList.addressList.size)
        Thread.sleep(2000)
        a.stopServer()
    }

    @Test
    fun noCrashOne() {
        val a = Server(10000U, initial = true)
        val b = Server(10001U, initial = true)
        a.enableMaintainLoop = true
        a.startServer()
        b.enableMaintainLoop = true
        b.startServer()


        a.addressList.addAddress(b.addressList.ownAddress)

        b.addressList.addAddress(a.addressList.ownAddress)

        Thread.sleep(2000)
        assertEquals(1, a.addressList.addressList.size)
        assertEquals(1, b.addressList.addressList.size)
        a.stopServer()
        b.stopServer()
    }

    @Test
    fun noCrashTwo() {
        val a = Server(9000U, initial = true)
        val b = Server(9001U, initial = true)
        val c = Server(9002U, initial = true)
        a.enableMaintainLoop = true
        a.startServer()
        b.enableMaintainLoop = true
        b.startServer()
        c.enableMaintainLoop = true
        c.startServer()


        a.addressList.addAddress(b.addressList.ownAddress)
        a.addressList.addAddress(c.addressList.ownAddress)
        b.addressList.addAddress(a.addressList.ownAddress)
        b.addressList.addAddress(c.addressList.ownAddress)
        c.addressList.addAddress(a.addressList.ownAddress)
        c.addressList.addAddress(b.addressList.ownAddress)

        Thread.sleep(2000)
        assertEquals(2, a.addressList.addressList.size)
        assertEquals(2, b.addressList.addressList.size)
        assertEquals(2, c.addressList.addressList.size)
        a.stopServer()
        b.stopServer()
    }

    @Test
    fun testIntegration() {
        val a = Server(8000U, initial = true)
        val b = Server(8001U, initial = true)
        val c = Server(8002U, initial = true)
        val d = Server(8003U, initial = true)
        val e = Server(8004U, initial = true)
        val f = Server(8005U, initial = true)
        val g = Server(8006U, initial = true)

        a.addressList.addAddress(b.addressList.ownAddress)

        b.addressList.addAddress(a.addressList.ownAddress)
        b.addressList.addAddress(c.addressList.ownAddress)

        c.addressList.addAddress(b.addressList.ownAddress)
        c.addressList.addAddress(d.addressList.ownAddress)

        d.addressList.addAddress(c.addressList.ownAddress)
        d.addressList.addAddress(e.addressList.ownAddress)

        e.addressList.addAddress(d.addressList.ownAddress)
        e.addressList.addAddress(f.addressList.ownAddress)

        f.addressList.addAddress(e.addressList.ownAddress)
        f.addressList.addAddress(g.addressList.ownAddress)

        g.addressList.addAddress(f.addressList.ownAddress)

        d.enableMaintainLoop = true

        a.startServer()
        b.startServer()
        c.startServer()
        d.startServer()
        e.startServer()
        f.startServer()
        g.startServer()

        Thread.sleep(200)

        assertEquals(2, b.addressList.addressList.size)
        assertEquals(2, c.addressList.addressList.size)
        assertEquals(2, d.addressList.addressList.size)
        assertEquals(2, e.addressList.addressList.size)
        assertEquals(2, f.addressList.addressList.size)

        Thread.sleep(1000)
        assertEquals(2, a.addressList.addressList.size)
        assertEquals(2, b.addressList.addressList.size)
        assertEquals(2, c.addressList.addressList.size)
        assertEquals(3, d.addressList.addressList.size)
        assertEquals(2, e.addressList.addressList.size)
        assertEquals(2, f.addressList.addressList.size)
        assertEquals(1, g.addressList.addressList.size)

        Thread.sleep(1000)
        assertEquals(2, a.addressList.addressList.size)
        assertEquals(2, b.addressList.addressList.size)
        assertEquals(2, c.addressList.addressList.size)
        assertEquals(4, d.addressList.addressList.size)
        assertEquals(2, e.addressList.addressList.size)
        assertEquals(2, f.addressList.addressList.size)
        assertEquals(2, g.addressList.addressList.size)
    }
}