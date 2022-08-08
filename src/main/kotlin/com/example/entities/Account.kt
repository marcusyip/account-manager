package com.example.entities

import BigDecimalSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.*
import java.math.BigDecimal

@Serializable
data class Account(
    val id: Long,
    val currency: String,

    @Serializable(with = BigDecimalSerializer::class)
    val balance: BigDecimal = BigDecimal.ZERO,

    @Transient
    val lockVersion: Int = 0,

    @SerialName("created_at")
    var createdAt: LocalDateTime? = null,

    @SerialName("updated_at")
    var updatedAt: LocalDateTime? = null,
)