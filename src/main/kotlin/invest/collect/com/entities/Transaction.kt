package invest.collect.com.entities

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val amount: Double,
    val walletId: Long
)
