//package dev.lochert.ds.blockchain.ledger
//
//import dev.lochert.ds.blockchain.block.Block
//import dev.lochert.ds.blockchain.block.BlockChain
//import dev.lochert.ds.blockchain.block.BlockProposal
//import org.junit.jupiter.api.BeforeAll
//import org.junit.jupiter.api.Test
//import kotlin.test.assertTrue
//
//class LedgerActionsTest {
//
//    val repeats = 5
//    val ledger = BlockChain(Block.genesisNode)
//
//    @Test
//    fun getBlockByHash() {
//        val testBlock = BlockProposal(Block.genesisNode, "FirstGeneration").generateBlock()
//        val firstHash = testBlock.blockHash
//        ledger.addBlock(testBlock)
//        ledger.addBlock("secondGeneration")
//        ledger.addBlock("thirdGeneration")
//        println(ledger.searchForBlock(firstHash))
//        assertTrue(firstHash == ledger.searchForBlock(firstHash).blockHash)
//
//    }
//
//    @Test
//    fun getBlocksByHash() {
//        val testBlock = BlockProposal(Block.genesisNode, "FirstGeneration").generateBlock()
//
//        ledger.addBlock(testBlock)
//        ledger.addBlock("secondGeneration")
//        ledger.addBlock("thirdGeneration")
//        val testHash = ledger.listOfBlocks[1].blockHash
//        println(ledger.searchForBlocksFrom(testHash))
//        println()
//        println("full Ledger")
//        ledger.allBlocks().forEach{
//            println(it)
//        }
//        println()
//        println("subLedger")
//        ledger.searchForBlocksFrom(testHash).forEach{
//            println(it)
//        }
//        assert(ledger.searchForBlocksFrom(testHash) == ledger.allBlocks().subList(1, ledger.allBlocks().size))
//    }
//}
