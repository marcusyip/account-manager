package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import com.example.repositories.AccountRepository
import com.example.repositories.Accounts
import com.example.repositories.TransferRepository
import com.example.repositories.Transfers
import com.example.services.AccountService
import com.example.services.TransferService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.math.MathContext

fun Application.main() {
    Database.connect("jdbc:h2:file:./build/db", driver = "org.h2.Driver")

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Accounts, Transfers)

        val row1 = Accounts.select { Accounts.id eq 12345678 }.firstOrNull()
        if (row1 == null) {
            Accounts.insert {
                it[id] = 12345678
                it[currency] = "HKD"
                it[balance] = BigDecimal(1000000, MathContext.DECIMAL64)
                it[lockVersion] = 1
                it[createdAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            }
        }
        val row2 = Accounts.select { Accounts.id eq 88888888 }.firstOrNull()
        if (row2 == null) {
            Accounts.insert {
                it[id] = 88888888
                it[currency] = "HKD"
                it[balance] = BigDecimal("1000000.00", MathContext.DECIMAL64)
                it[lockVersion] = 1
                it[createdAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            }
        }
    }

    install(CallLogging)

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    val accountRepository = AccountRepository()
    val accountService = AccountService(accountRepository)
    val transferRepository = TransferRepository()
    val transferService = TransferService(accountService, transferRepository)
    configureRouting(accountService, transferService)
}
