package invest.collect.com.entities

import java.time.temporal.TemporalAmount

data class Purchase(
    val userId: Long,
    val collectibleId: Long,
    val amount: Int
)
