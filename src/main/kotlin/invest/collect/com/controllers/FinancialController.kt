package invest.collect.com.controllers

import invest.collect.com.entities.*
import invest.collect.com.utils.HttpClientFactory
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.lang.Exception

/**
 * The FinancialController class provides methods for managing financial transactions and wallet balances.
 */
class FinancialController {
    /**
     * Retrieves the wallet of a user.
     *
     * @param userId The ID of the user.
     * @param url The base URL of the financial service.
     * @return The user's wallet.
     * @throws Exception if there is an error while receiving the wallet.
     */
    suspend fun getWallet(userId: Long, url: String): Wallet {
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

    /**
     * Withdraws a specified amount of balance from a user's wallet.
     *
     * @param url The base URL of the financial service.
     * @param userId The ID of the user.
     * @param amount The amount to withdraw.
     * @throws IllegalArgumentException if there is not enough money in the wallet.
     * @throws Exception if there is an error while updating the balance.
     */
    private suspend fun withdrawBalance(url: String, userId: Long, amount: Double){
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

    /**
     * Method to top up the balance of a user's wallet.
     *
     * @param url The base URL of the financial service.
     * @param userId The ID of the user.
     * @param amount The amount to top up.
     */
    suspend fun topUp(url: String, userId: Long, amount: Double) {
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

    /**
     * Creates a new transaction.
     *
     * @param url The base URL of the financial service.
     * @param userId The ID of the user.
     * @param amount The amount of the transaction.
     * @return The ID of the created transaction.
     * @throws Exception if there is an error while creating the transaction.
     */
    private suspend fun createTransaction(url: String, userId: Long, amount: Double): Long{
        HttpClientFactory.createHttpClient().use { client ->
            val walletId = getWallet(userId, "http://localhost:8080").id
            val newTransaction = Transaction(amount = amount, walletId = walletId)
            val response: HttpResponse = client.patch("$url/financialService/transaction/createTransaction"){
                contentType(ContentType.Application.Json)
                setBody(newTransaction)
            }
            when (response.status){
                HttpStatusCode.OK -> {
                    return response.body<TransactionId>().id
                }

                else -> {
                    throw Exception("Error while creating transaction")
                }
            }
        }
    }

    /**
     * Updates the status of a transaction.
     *
     * @param url The base URL of the financial service.
     * @param status The updated status of the transaction.
     * @throws Exception if there is an error while updating the status.
     */
    private suspend fun updateStatus(url: String, status: Status){
        HttpClientFactory.createHttpClient().use { client ->
            val response: HttpResponse = client.patch("$url/financialService/transaction/updateStatus"){
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

    /**
     * Buys a collectible by performing the following steps:
     * 1. Creates a new transaction with a negative amount to represent the purchase.
     * 2. Attempts to withdraw the specified amount from the user's balance. If the balance
     *    is insufficient, a "not enough money" status is returned and the transaction status is updated.
     * 3. If the withdrawal is successful, a "success" status is returned and the transaction status is updated.
     *
     * @param url The base URL of the financial service.
     * @param userId The ID of the user.
     * @param amount The amount to buy.
     * @return The status of the buying operation.
     */
    suspend fun buyCollectible(url: String, userId: Long, amount: Double): Status{
        val transactionId = createTransaction(url, userId, 0 - amount)
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

    /**
     * Sells a collectible by performing the following steps:
     * 1. Creates a new transaction with a positive amount to represent the sale.
     * 2. Attempts to top up the specified amount into the user's balance. If the top-up fails,
     *    an "operation dropped" status is returned and the transaction status is updated.
     * 3. If the top-up is successful, a "success" status is returned and the transaction status is updated.
     *
     * @param url The base URL of the financial service.
     * @param userId The ID of the user.
     * @param amount The amount to sell.
     * @return The status of the selling operation.
     */
    suspend fun sellCollectible(url: String, userId: Long, amount: Double): Status {
        val transactionId = createTransaction(url, userId, amount)
        try {
            topUp(url, userId, amount)
        }
        catch (e: Throwable){
            val status = Status(id = transactionId, status = "operation dropped")
            updateStatus(url, status)
            return status
        }
        val status = Status(id = transactionId, status = "success")
        updateStatus(url, status)
        return status
    }
}
