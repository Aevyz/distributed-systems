

import com.google.protobuf.ByteString
import com.google.protobuf.kotlin.toByteString
import onion.Onion
import java.util.Date
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class,ExperimentalTime::class)
class ProtobufBuilder {

    companion object{
        val defaultDelay = 5000u
        fun randomNodes(numberOfNodes: UInt=3U): List<RSAKeyPair> {
            return RSAKeyPairs.listOfKeyPairs.shuffled().subList(0, numberOfNodes.toInt())
        }
        fun randomNode(): RSAKeyPair{
            return RSAKeyPairs.listOfKeyPairs.random()
        }
        fun randomPadding(min: UInt = 20u, max: UInt = 100u): ByteString {
            return Random.nextBytes(Random.nextInt(min.toInt()..max.toInt())).toByteString()
        }
        fun generateBackIdentifier(): Uuid {
            return Uuid.random()
        }

        fun createDebugMessage(msg: String, delay: UInt = defaultDelay): Onion.OnionPacket {
            return Onion.OnionPacket.newBuilder().setSimpleMessage(
                Onion.SimpleMessage.newBuilder().setMessage(msg).build()
            )
                .setRandomPadding(randomPadding())
                .setDelayMs(delay.toInt())
                .build()

        }

        fun packMessage(onionPacket: Onion.OnionPacket, delay: UInt = defaultDelay){
            return packMessage(onionPacket, randomNode(), delay)
        }
        fun packMessage(onionPacket: Onion.OnionPacket, targetPublicKey: RSAKeyPair, delay: UInt = defaultDelay){
            val encryptedPayload = targetPublicKey.encrypt(onionPacket.toByteArray())

            val encryptedContent = Onion.EncryptedContent.newBuilder()
                .setCiphertext(encryptedPayload)
                .setType("RSA")
                .setPublicKey(targetPublicKey.publicKey.encoded.toByteString())
                .setDestination(targetPublicKey.domainPort)
                .setDebugPlaintext(onionPacket.toString())

            val newOnionPacket = Onion.OnionPacket.newBuilder()
                .setEncryptedContent(encryptedContent)
                .setDelayMs(delay.toInt())
                .setRandomPadding(randomPadding())
                .setBackIdentifier(generateBackIdentifier().toString())
                .setTimestamp(Clock.System.now().toString())
                .build()
        }
    }
}