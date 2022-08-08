package com.example.dtos

import BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class CreateTransferDTO(
    @SerialName("sender_account_id")
    val senderAccountId: Long,

    @SerialName("recipient_account_id")
    val recipientAccountId: Long,

    @SerialName("currency")
    val currency: String,

    @SerialName("amount")
    @Serializable(with = BigDecimalSerializer::class)
    val amount: BigDecimal,

    @SerialName("idempotency_key")
    val idempotencyKey: String,
)