package invest.collect.com.controllers

import invest.collect.com.entities.Transaction
import invest.collect.com.utils.HttpClientFactory
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.lang.Exception

class TopUpController {
    suspend fun topUp(transaction: Transaction, url: String) {
        HttpClientFactory.createHttpClient().use { client ->
            val response: HttpResponse = client.put("url/financialService/wallet/topUpBalance/"){
                contentType(ContentType.Application.Json)
                setBody(transaction)
            }
            when (response.status){
                HttpStatusCode.OK -> {
                    return
                }

                else -> {
                    throw Exception("Error while updating balance")
                }
            }
        }
    }
}