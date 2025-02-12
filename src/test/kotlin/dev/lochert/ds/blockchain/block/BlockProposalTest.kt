@file:OptIn(ExperimentalStdlibApi::class)

package dev.lochert.ds.blockchain.block

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random
import kotlin.test.assertContentEquals

class BlockProposalTest {
    val content = "Unit Test Content"
    @Test
    fun generateBlock() {
        val blockProposal = BlockProposal(
            Random.Default.nextBytes(256/8).toHexString(),
            content
        )
        val block = blockProposal.generateBlock()
        assertEquals(content, block.content)
    }
    @Test
    fun generateFollowingBlock(){
        val firstBlockProposal = BlockProposal(
            Random.nextBytes(256/8).toHexString(),
            content +1
        )
        val firstBlock = Block.genesisNode
        val secondBlockProposal = BlockProposal(
            firstBlock.blockHash,
            content +1
        )
        val secondBlock = secondBlockProposal.generateBlock()
        assertEquals(firstBlock.blockHash, secondBlock.parentHash)
        assertTrue(Block.isValid(firstBlock))
        assertTrue(Block.isValid(secondBlock))
        println(firstBlock)
        println(secondBlock)
    }
    @Test
    fun generateBlocks(){
        val repeats=10

        val list = mutableListOf(Block.genesisNode)
        repeat(repeats){
            val previousBlock = list.last()
            val blockProposal = BlockProposal(previousBlock, it.toString())
            list+blockProposal.generateBlock()
        }

        list.forEachIndexed { index, block ->
            assertTrue(Block.isValid(block))
            if(!block.isGenesis()){
                assertEquals(list[index-1].blockHash, block.blockHash)
            }
        }
    }

}