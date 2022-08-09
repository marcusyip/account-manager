package com.example.dtos

import BigDecimalSerializer
import io.konform.validation.Validation
import io.konform.validation.jsonschema.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.Exception
import java.math.BigDecimal

@Serializable
data class ErrorDTO(
    @SerialName("message")
    val message: String
) {
    constructor(e: Exception): this(e.message!!) {}
}
