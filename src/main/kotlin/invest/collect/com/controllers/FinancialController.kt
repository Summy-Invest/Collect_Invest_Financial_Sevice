package invest.collect.com.controllers

import invest.collect.com.entities.*
import invest.collect.com.utils.HttpClientFactory
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.lang.Exception

class FinancialController {
    private suspend fun getWallet(userId: Long, url: String): Wallet {
        HttpClientFactory.createHttpClient().use { client ->
            val response: HttpResponse = client.get("$url/financialService/wallet/getWallet/$userId")
            when (response.status){
                HttpStatusCode.OK -> {
                    return response.body<Wallet>()
                }

                else -> {
                    throw Exception("Error while receiving wallet")
                }
            }
        }
    }

    private suspend fun withdrawBalance(url: String, userId: Long, amount: Int){
        HttpClientFactory.createHttpClient().use { client ->
            val wallet = getWallet(userId, url)
            if (wallet.balance < amount){
                throw IllegalArgumentException("Not enough money in wallet")
            }
            val response: HttpResponse = client.put("$url/financialService/wallet/withdrawBalance/$userId/$amount")
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

    suspend fun topUp(url: String, userId: Long, amount: Int) {
        HttpClientFactory.createHttpClient().use { client ->
            val response: HttpResponse = client.put("$url/financialService/wallet/topUpBalance/$userId/$amount")
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

    private suspend fun createTransaction(url: String, userId: Long, amount: Int): Long{
        HttpClientFactory.createHttpClient().use { client ->
            val walletId = getWallet(userId, "http://localhost:8080").id
            val newTransaction = Transaction(walletId = walletId, amount = amount)
            val response: HttpResponse = client.post("$url/financialService/transaction/createTransaction/"){
                contentType(ContentType.Application.Json)
                setBody(newTransaction)
            }
            when (response.status){
                HttpStatusCode.OK -> {
                    return response.body<Long>()
                }

                else -> {
                    throw Exception("Error while creating transaction")
                }
            }
        }
    }

    private suspend fun updateStatus(url: String, status: Status){
        HttpClientFactory.createHttpClient().use { client ->
            val response: HttpResponse = client.patch("$url/financialService/transaction/updateStatus/"){
                contentType(ContentType.Application.Json)
                setBody(status)
            }
            when (response.status){
                HttpStatusCode.OK -> {
                    return
                }

                else -> {
                    throw Exception("Error while updating status")
                }
            }
        }
    }

    suspend fun buyCollectible(url: String, userId: Long, amount: Int): Status{
        val transactionId = createTransaction(url, userId, amount)
        try {
            withdrawBalance(url, userId, amount)
        }
        catch (e: IllegalArgumentException){
            val status = Status(id = transactionId, status = "not enough money")
            updateStatus(url, status)
            return status
        }
        val status = Status(id = transactionId, status = "success")
        updateStatus(url, status)
        return status
    }
}
