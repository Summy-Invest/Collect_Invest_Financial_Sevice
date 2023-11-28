package invest.collect.com.entities

import kotlinx.serialization.Serializable

@Serializable
data class Wallet(
    val id: Long,
    val userId: Long,
    val balance: Int,
    val status: String,
)
