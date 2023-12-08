package invest.collect.com.routes

import invest.collect.com.controllers.FinancialController
import invest.collect.com.entities.Message
import invest.collect.com.entities.Transaction
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.configureRouting() {

    val walletController = FinancialController()
    val url = "http://localhost:8080"

    routing {
        put("/topUp") {
            val transaction = call.receive<Transaction>()
            try {
                walletController.topUp(url, transaction)
                call.respond(HttpStatusCode.OK, "${transaction.amount}")
            }
            catch (e: Throwable) {
                call.respondText(text = Json.encodeToString(Message("Error while updating balance")),
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.OK
                )
            }
        }
        post("/buy") {
            val transaction = call.receive<Transaction>()
            val status = walletController.buyCollectible(url, transaction)
            call.respond(status)
        }
    }
}
