package com.example.entities

import BigDecimalSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.*
import java.math.BigDecimal

@Serializable
data class Transfer(
    val id: Long,

    @SerialName("sender_account_id")
    val senderAccountId: Long,

    @SerialName("recipient_account_id")
    val recipientAccountId: Long,
    val currency: String,
    @Serializable(with = BigDecimalSerializer::class)
    val amount: BigDecimal,
    val status: String,

    @SerialName("lock_version")
    val lockVersion: Int,

    @SerialName("idempotency_key")
    val idempotencyKey: String,

    @SerialName("created_at")
    val createdAt: LocalDateTime,

    @SerialName("updated_at")
    val updatedAt: LocalDateTime,
)