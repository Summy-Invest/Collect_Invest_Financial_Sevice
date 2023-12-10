package invest.collect.com.routes

import invest.collect.com.controllers.FinancialController
import invest.collect.com.entities.Message
import invest.collect.com.entities.Transaction
import invest.collect.com.entities.Wallet
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
        put("/topUp/{userId}/{amount}") {
            val userId: Long = call.parameters["userId"]!!.toLong()
            val amount: Int = call.parameters["amount"]!!.toInt()
            try {
                walletController.topUp(url, userId, amount)
                call.respond(HttpStatusCode.OK, "$amount")
            }
            catch (e: Throwable) {
                call.respondText(text = Json.encodeToString(Message("Error while updating balance")),
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.BadRequest
                )
            }
        }

        post("/buy/{userId}/{amount}") {
            val userId: Long = call.parameters["userId"]!!.toLong()
            val amount: Int = call.parameters["amount"]!!.toInt()
            val status = walletController.buyCollectible(url, userId, amount)
            call.respond(status)
        }

        post("/sell/{userId}/{amount}") {
            val userId: Long = call.parameters["userId"]!!.toLong()
            val amount: Int = call.parameters["amount"]!!.toInt()
            val status = walletController.sellCollectible(url, userId, amount)
            call.respond(status)
        }

        get("/getWallet/{userId}") {
            val userId: Long = call.parameters["userId"]!!.toLong()
            try{
                val wallet: Wallet = walletController.getWallet(userId, url)
                call.respond(wallet)
            }
            catch (e: Throwable) {
                call.respondText(text = Json.encodeToString(Message("Error while getting balance")),
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.BadRequest
                )
            }
        }
    }
}
