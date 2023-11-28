package invest.collect.com.controllers

import invest.collect.com.entities.Purchase
import invest.collect.com.utils.HttpClientFactory
import io.ktor.server.engine.*

class PurchaseController {
    suspend fun buy(purchase: Purchase, url: String){
        HttpClientFactory.createHttpClient().use { client ->
            //TODO
        }
    }
}