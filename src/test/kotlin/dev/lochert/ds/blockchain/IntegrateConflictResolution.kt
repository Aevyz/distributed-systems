package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.http.server.Server
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class IntegrateConflictResolution {
    @BeforeEach
    fun before() {
        Constants.maintainIntervalSeconds = 1
        Constants.initialAddressSet = emptySet()
        Constants.maintainBlockchain = BlockchainMaintenanceStrategyEnum.ImmediateLongestLowestHash
        Constants.maintainAddressStrategies = setOf()

        Constants.setRandom = Random(1)
        Constants.enableMaintainLoop = true
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

        val servers = listOf(a, b, c, d, e, f, g)

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


        a.blockChain.addBlock("A Side 1")
        a.blockChain.addBlock("A Side 2")

        d.blockChain.addBlock("D Side 1")

        g.blockChain.addBlock("G Side 1")

        println(a.blockChain.listOfBlocks.last().blockHash)
        println(d.blockChain.listOfBlocks.last().blockHash)
        println(g.blockChain.listOfBlocks.last().blockHash)

        assertEquals(1, a.addressList.addressList.size)
        assertEquals(2, b.addressList.addressList.size)
        assertEquals(2, c.addressList.addressList.size)
        assertEquals(2, d.addressList.addressList.size)
        assertEquals(2, e.addressList.addressList.size)
        assertEquals(2, f.addressList.addressList.size)
        assertEquals(1, g.addressList.addressList.size)

        assertContains(b.addressList.addressList, a.addressList.ownAddress)
        assertContains(b.addressList.addressList, c.addressList.ownAddress)
        fun print() {
            for (server in servers) {
                println("${server.addressList.ownAddress}: ${server.blockChain.lastInfo()}")
            }
        }

        a.startServer()
        b.startServer()
        c.startServer()
        d.startServer()
        e.startServer()
        f.startServer()
        g.startServer()

        Thread.sleep(100)

        val hashA = a.blockChain.lastHash()
        val hashD = d.blockChain.lastHash()
        val hashG = g.blockChain.lastHash()

        val genesisHash = b.blockChain.lastHash()


        assertEquals(hashA, a.blockChain.lastHash())
        assertEquals(genesisHash, b.blockChain.lastHash())
        assertEquals(genesisHash, c.blockChain.lastHash())
        assertEquals(hashD, d.blockChain.lastHash())
        assertEquals(genesisHash, e.blockChain.lastHash())
        assertEquals(genesisHash, f.blockChain.lastHash())
        assertEquals(hashG, g.blockChain.lastHash())

        Thread.sleep(Constants.maintainIntervalSeconds * 1000L + 100)
        print()

        assertEquals(hashA, a.blockChain.lastHash())
        assertEquals(hashA, b.blockChain.lastHash())
        assertEquals(hashD, c.blockChain.lastHash())
        assertEquals(hashD, d.blockChain.lastHash())
        assertEquals(hashD, e.blockChain.lastHash())
        assertEquals(hashG, f.blockChain.lastHash())
        assertEquals(hashG, g.blockChain.lastHash())

        Thread.sleep(Constants.maintainIntervalSeconds * 1000L + 100)
        print()

        assertEquals(hashA, a.blockChain.lastHash())
        assertEquals(hashA, b.blockChain.lastHash())
        assertEquals(hashA, c.blockChain.lastHash())
        assertEquals(hashD, d.blockChain.lastHash())
        assertEquals(hashD, e.blockChain.lastHash())
        assertEquals(hashD, f.blockChain.lastHash())
        assertEquals(hashG, g.blockChain.lastHash())

        Thread.sleep(Constants.maintainIntervalSeconds * 1000L + 100)
        print()

        assertEquals(hashA, a.blockChain.lastHash())
        assertEquals(hashA, b.blockChain.lastHash())
        assertEquals(hashA, c.blockChain.lastHash())
        assertEquals(hashA, d.blockChain.lastHash())
        assertEquals(hashD, e.blockChain.lastHash())
        assertEquals(hashD, f.blockChain.lastHash())
        assertEquals(hashD, g.blockChain.lastHash())

        Thread.sleep(Constants.maintainIntervalSeconds * 1000L + 100)
        print()

        assertEquals(hashA, a.blockChain.lastHash())
        assertEquals(hashA, b.blockChain.lastHash())
        assertEquals(hashA, c.blockChain.lastHash())
        assertEquals(hashA, d.blockChain.lastHash())
        assertEquals(hashA, e.blockChain.lastHash())
        assertEquals(hashD, f.blockChain.lastHash())
        assertEquals(hashD, g.blockChain.lastHash())


        Thread.sleep(Constants.maintainIntervalSeconds * 1000L + 100)
        print()

        assertEquals(hashA, a.blockChain.lastHash())
        assertEquals(hashA, b.blockChain.lastHash())
        assertEquals(hashA, c.blockChain.lastHash())
        assertEquals(hashA, d.blockChain.lastHash())
        assertEquals(hashA, e.blockChain.lastHash())
        assertEquals(hashA, f.blockChain.lastHash())
        assertEquals(hashD, g.blockChain.lastHash())


        Thread.sleep(Constants.maintainIntervalSeconds * 1000L + 100)
        print()

        assertEquals(hashA, a.blockChain.lastHash())
        assertEquals(hashA, b.blockChain.lastHash())
        assertEquals(hashA, c.blockChain.lastHash())
        assertEquals(hashA, d.blockChain.lastHash())
        assertEquals(hashA, e.blockChain.lastHash())
        assertEquals(hashA, f.blockChain.lastHash())
        assertEquals(hashA, g.blockChain.lastHash())

    }
}