package invest.collect.com.entities

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val id: Long,
    val status: String
)
