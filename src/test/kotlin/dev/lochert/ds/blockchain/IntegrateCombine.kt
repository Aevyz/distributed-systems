//package dev.lochert.ds.blockchain
//
//import dev.lochert.ds.blockchain.http.server.Server
//import org.junit.jupiter.api.BeforeEach
//import kotlin.random.Random
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//
//class IntegrateCombine {
//    @BeforeEach
//    fun before() {
//        Constants.maintainIntervalSeconds = 1
//        Constants.initialAddressSet = emptySet()
//        Constants.maintainBlockchain = BlockchainMaintenanceStrategyEnum.ImmediateLongestLowestHash
//        Constants.maintainAddressStrategies = setOf(
//            AddressMaintenanceStrategyEnum.NoGreaterThirdDegree,
//            AddressMaintenanceStrategyEnum.SubgraphConnection,
//            AddressMaintenanceStrategyEnum.RemoveUnresponsiveNodes,
//            AddressMaintenanceStrategyEnum.BackupToFile
//        )
//
//        Constants.setRandom = Random(1)
//        Constants.enableMaintainLoop = true
//    }
//
//
//    @Test
//    fun testIntegration() {
//        var port: UShort = 8100U
//        val a1 = Server(port++, initial = true)
//        val a2 = Server(port++, initial = true)
//        val a3 = Server(port++, initial = true)
//        val a4 = Server(port++, initial = true)
//        val b1 = Server(port++, initial = true)
//        val b2 = Server(port++, initial = true)
//        val b3 = Server(port++, initial = true)
//        val b4 = Server(port++, initial = true)
//        val b5 = Server(port++, initial = true)
//        val b6 = Server(port++, initial = true)
//        val b7 = Server(port++, initial = true)
//        val c1 = Server(port++, initial = true)
//        val c2 = Server(port++, initial = true)
//
//        val servers = listOf(a1, a2, a3, a4, b1, b2, b3, b4, b5, b6, b7, c1, c2)
//
//        fun Server.addAddress(vararg servers: Server) {
//            for (server in servers) {
//                this.addressList.addressList.add(server.addressList.ownAddress)
//            }
//        }
//
//        a1.addAddress(a1, a2, a3, a4)
//        a2.addAddress(a1, a2, a3, a4)
//        a3.addAddress(a1, a2, a3, a4)
//        a4.addAddress(a1, a2, a3, a4)
//
//        b1.addAddress(b2)
//        b2.addAddress(b1, b3)
//        b3.addAddress(b2, b4)
//        b4.addAddress(b3, b5)
//        b5.addAddress(b4, b6)
//        b6.addAddress(b5, b7)
//        b7.addAddress(b6)
//
//        c1.addAddress(c2)
//        c2.addAddress(c1)
//
//        a1.blockChain.addBlock("a1_1")
//        a1.blockChain.addBlock("a1_2")
//        a1.blockChain.addBlock("a1_3")
//        a3.blockChain.addBlock("a3_1")
//
//        b1.blockChain.addBlock("b1")
//        b4.blockChain.addBlock("b2_1")
//        b4.blockChain.addBlock("b2_2")
//
//        repeat(4) {
//            c1.blockChain.addBlock("c1_$it")
//        }
//
//        servers.forEach { it.startServer() }
//
//        Thread.sleep(1500)
//
//        servers.forEach {
//            println("[UT] [Assert] For ${it.addressList.ownAddress}")
//            if (it == c1 || it == c2) {
//                assertEquals(1, it.addressList.addressList.size)
//            } else {
//                assertTrue(3 <= it.addressList.addressList.size)
//            }
//        }
//        Thread.sleep(Constants.maintainIntervalSeconds * 4 * 1000)
//        assertEquals(a1.blockChain.listOfBlocks.toString(), a2.blockChain.listOfBlocks.toString())
//        assertEquals(a1.blockChain.listOfBlocks.toString(), a3.blockChain.listOfBlocks.toString())
//        assertEquals(a1.blockChain.listOfBlocks.toString(), a4.blockChain.listOfBlocks.toString())
//
//
//
//        assertEquals(b1.blockChain.listOfBlocks.toString(), b2.blockChain.listOfBlocks.toString())
//        assertEquals(b1.blockChain.listOfBlocks.toString(), b3.blockChain.listOfBlocks.toString())
//        assertEquals(b1.blockChain.listOfBlocks.toString(), b4.blockChain.listOfBlocks.toString())
//        assertEquals(b1.blockChain.listOfBlocks.toString(), b5.blockChain.listOfBlocks.toString())
//        assertEquals(b1.blockChain.listOfBlocks.toString(), b6.blockChain.listOfBlocks.toString())
//        assertEquals(b1.blockChain.listOfBlocks.toString(), b7.blockChain.listOfBlocks.toString())
//
//        assertEquals(c1.blockChain.listOfBlocks.toString(), c2.blockChain.listOfBlocks.toString())
//
//        val newServer = Server(port, initial = true)
//        newServer.addAddress(a1, b1, c1)
//        newServer.startServer()
//
//        Thread.sleep(Constants.maintainIntervalSeconds * 1000 * 4)
//
//        servers.forEach {
//            assertEquals(
//                newServer.blockChain.listOfBlocks.toString(),
//                it.blockChain.listOfBlocks.toString()
//            )
//        }
//
//
//        //Three Jumps
//    }
//}