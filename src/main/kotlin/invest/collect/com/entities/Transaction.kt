package invest.collect.com.entities

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val userId: Long,
    val moneyAmount: Int
)
