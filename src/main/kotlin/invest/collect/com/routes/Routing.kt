package invest.collect.com.routes

import invest.collect.com.controllers.TopUpController
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

    val topUpController = TopUpController()

    routing {
        put("/topUp") {
            val transaction = call.receive<Transaction>()
            try {
                topUpController.topUp(transaction, "http://localhost:8080")
                call.respond(HttpStatusCode.OK, "${transaction.moneyAmount}")
            }
            catch (e: Throwable) {
                call.respondText(text = Json.encodeToString(Message("Error while updating balance")),
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.OK
                )
            }
        }
        post("/buy") {
            //TODO
        }
    }
}
