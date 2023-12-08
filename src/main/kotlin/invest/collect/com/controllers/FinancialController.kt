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
                    throw Exception("Error while updating balance")
                }
            }
        }
    }
    private suspend fun withdrawBalance(url: String, transaction: Transaction){
        HttpClientFactory.createHttpClient().use { client ->
            val userId = transaction.userId;
            val wallet = getWallet(userId, "http://localhost:8080");
            if (wallet.balance < transaction.amount){
                throw IllegalArgumentException("Not enough money in wallet")
            }
            val response: HttpResponse = client.put("$url/financialService/wallet/withdrawBalance/"){
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
    suspend fun topUp(url: String, transaction: Transaction) {
        HttpClientFactory.createHttpClient().use { client ->
            val response: HttpResponse = client.put("$url/financialService/wallet/topUpBalance/"){
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
    private suspend fun createTransaction(url: String, transaction: Transaction): Long{
        HttpClientFactory.createHttpClient().use { client ->
            val userId = transaction.userId;
            val walletId = getWallet(userId, "http://localhost:8080").id;
            val newTransaction = Transaction(walletId, transaction.amount);
            val response: HttpResponse = client.post("$url/financialService/transaction/createTransaction/"){
                contentType(ContentType.Application.Json)
                setBody(newTransaction)
            }
            when (response.status){
                HttpStatusCode.OK -> {
                    return response.body<Long>();
                }

                else -> {
                    throw Exception("Error while updating balance")
                }
            }
        }
    }

    private suspend fun updateStatus(url: String, status: Status){
        HttpClientFactory.createHttpClient().use { client ->
            val response: HttpResponse = client.put("$url/financialService/transaction/updateStatus/"){
                contentType(ContentType.Application.Json)
                setBody(status)
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

    suspend fun buyCollectible(url: String, purchase: Transaction): Status{
        val transactionId = createTransaction(url, purchase)
        try {
            withdrawBalance(url, purchase)
        }
        catch (e: IllegalArgumentException){
            val status = Status(transactionId, "not enough money")
            updateStatus(url, status)
            return status
        }
        val status = Status(transactionId, "success")
        updateStatus(url, status)
        return status
    }
}