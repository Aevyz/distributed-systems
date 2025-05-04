import com.google.protobuf.InvalidProtocolBufferException
import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.HttpExchange
import onion.Onion.OnionPacket
import onion.OnionPacketKt
import java.io.OutputStream
import java.net.InetSocketAddress
import java.security.PublicKey

class OnionHttpServer(private val port: Int = 8080) {
    private val server: HttpServer = HttpServer.create(InetSocketAddress(port), 0)

    val publicKeyMap = mutableMapOf<PublicKey, NodeAddress>()

    init {
        server.createContext("/onion") { exchange -> handlePost(exchange) }
        server.executor = null
    }

    fun start() {
        println("Server started at http://localhost:$port/onion")
        server.start()
    }

    fun stop(delay: Int = 0) {
        server.stop(delay)
    }
    fun println(msg: Any){
        print("[$port] ")
        System.out.println(msg)
    }

    private fun handlePost(exchange: HttpExchange) {
        if (exchange.requestMethod != "POST") {
            exchange.sendResponseHeaders(405, -1) // Method Not Allowed
            return
        }

        try {
            val body = exchange.requestBody.readBytes()
            val packet = OnionPacket.parseFrom(body)

            when (packet.payloadCase) {
                OnionPacket.PayloadCase.ENCRYPTED_CONTENT -> {
                    println("Received ENCRYPTED_CONTENT")
                }
                OnionPacket.PayloadCase.COVER_TRAFFIC -> {
                    println("Received COVER_TRAFFIC")
                }
                OnionPacket.PayloadCase.HTTP_GET_REQUEST -> {
                    println("Received HTTP_GET_REQUEST")
                }
                OnionPacket.PayloadCase.HTTP_GET_RESPONSE -> {
                    println("Received HTTP_GET_RESPONSE")
                }
                OnionPacket.PayloadCase.HTTP_POST_REQUEST -> {
                    println("Received HTTP_POST_REQUEST")
                }
                OnionPacket.PayloadCase.PAYLOAD_NOT_SET -> {
                    println("Payload not set in OnionPacket")
                }
                OnionPacket.PayloadCase.SIMPLE_MESSAGE ->{
                    println("Simple Message - ${packet.simpleMessage.message}")
                }
                null -> {
                    println("Unknown payload case")
                }
            }

            val response = "Packet processed"
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        } catch (e: InvalidProtocolBufferException) {
            println("Failed to parse protobuf: ${e.message}")
            exchange.sendResponseHeaders(400, -1)
        } catch (e: Exception) {
            println("Error handling request: ${e.message}")
            exchange.sendResponseHeaders(500, -1)
        }
    }
}

// --- Entry point to start the server ---
fun main() {
    val server = OnionHttpServer()
    server.start()
}

// This code has been generated with the assistance of ChatGPT
// End of generated code
