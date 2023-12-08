package invest.collect.com.entities

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: Long? = null,
    val amount: Int? = null,
    val status: String? = null,
    val walletId: Long? = null
)
