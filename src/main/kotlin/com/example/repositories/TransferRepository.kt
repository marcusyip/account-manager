package com.example.repositories

import com.example.entities.Transfer
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

object Transfers : Table("transfers") {
    val id: Column<Long> = long("id").autoIncrement()
    val senderAccountId: Column<Long> = long("sender_account_id")
    val recipientAccountId: Column<Long> = long("recipient_account_id")
    val currency: Column<String> = varchar("currency", 10)
    val amount: Column<BigDecimal> = decimal("amount", 36, 16)
    val status: Column<String> = varchar("status", 20)
    val lockVersion: Column<Int> = integer("lock_version")
    val idempotencyKey: Column<String> = varchar("idempotency_key", 36).uniqueIndex()
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(id, name = "PK_Transfers_Id")
}

class TransferRepository {
    private fun resultRowToTransfer(row: ResultRow) = Transfer(
        id = row[Transfers.id],
        senderAccountId = row[Transfers.senderAccountId],
        recipientAccountId = row[Transfers.recipientAccountId],
        currency = row[Transfers.currency],
        amount = row[Transfers.amount],
        status = row[Transfers.status],
        lockVersion = row[Transfers.lockVersion],
        idempotencyKey = row[Transfers.idempotencyKey],
        createdAt = row[Transfers.createdAt],
        updatedAt = row[Transfers.updatedAt],
    )

    fun findById(id: Long): Transfer? = transaction {
        Transfers.select { Transfers.id eq id }
            .map(::resultRowToTransfer)
            .singleOrNull()
    }

    fun findAll(): List<Transfer> = transaction {
        Transfers.selectAll().map(::resultRowToTransfer)
    }

    fun create(
        senderAccountId: Long,
        recipientAccountId: Long,
        currency: String,
        amount: BigDecimal,
        idempotencyKey: String
    ): Transfer {
        val id = transaction {
            Transfers.insert {
                it[Transfers.senderAccountId] = senderAccountId
                it[Transfers.recipientAccountId] = recipientAccountId
                it[Transfers.currency] = currency
                it[Transfers.amount] = amount
                it[Transfers.status] = "pending"
                it[Transfers.idempotencyKey] = idempotencyKey
                it[Transfers.lockVersion] = 1
                val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                it[Transfers.createdAt] = now
                it[Transfers.updatedAt] = now
            } get Transfers.id
        }
        return findById(id)!!
    }

    fun updateById(id: Long, status: String, lockVersion: Int) = transaction {
        Transfers.update({
            Transfers.id eq id and
                    (Transfers.lockVersion eq lockVersion)
        }) {
            with(SqlExpressionBuilder) {
                it[Transfers.status] = status
                it[Transfers.lockVersion] = Transfers.lockVersion + 1
                it[Transfers.updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            }
        }
    }
}