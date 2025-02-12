package dev.lochert.ds.blockchain.block

import dev.lochert.ds.blockchain.Constants
import java.security.MessageDigest

class BlockUtils {
    companion object{

        fun hashByteArray(input: ByteArray): ByteArray {
            return MessageDigest.getInstance(Constants.HASH_ALGORITHM).digest(input)
        }

        @ExperimentalStdlibApi
        fun hasLeadingZeroBytes(hash: ByteArray, zeroBytes: Int = Constants.DIFFICULTY): Boolean {
            return hash.toHexString().take(zeroBytes).all { it == '0' }
        }
    }
}