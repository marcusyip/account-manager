import com.example.dtos.CreateTransferDTO
import com.example.entities.Account
import com.example.entities.Transfer
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.math.BigDecimal
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, world!", response.bodyAsText())
    }

    @Test
    fun testGetAccount1() = testApplication {
        val client = createClient {}
        val response = client.get("/v1/accounts/12345678")

        assertEquals(HttpStatusCode.OK, response.status)
        val account = Json.decodeFromString<Account>(response.bodyAsText())
        assertEquals(12345678, account.id)
        assertEquals("HKD", account.currency)
//        assertEquals(12345678, account.balance)
    }

        @Test
    fun testGetAccount2() = testApplication {
        val client = createClient {}
        val response = client.get("/v1/accounts/88888888")

        assertEquals(HttpStatusCode.OK, response.status)
        val account = Json.decodeFromString<Account>(response.bodyAsText())
        assertEquals(88888888, account.id)
        assertEquals("HKD", account.currency)
    }


    @Test
    fun testCreateTransfer() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/v1/transfers") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateTransferDTO(
                    senderAccountId = 12345678,
                    recipientAccountId = 88888888,
                    currency = "HKD",
                    amount = BigDecimal(100),
                    idempotencyKey = "testing001"
                )
            )
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val transfer = Json.decodeFromString<Transfer>(response.bodyAsText())
        assertEquals("testing001", transfer.idempotencyKey)
        assertEquals("HKD", transfer.currency)
        assertEquals("done", transfer.status)
    }
}