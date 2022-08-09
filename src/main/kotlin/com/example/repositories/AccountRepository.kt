package com.example.repositories

import com.example.entities.Account
import com.example.entities.Transfer
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

object Accounts : Table("accounts") {
    val id: Column<Long> = long("id").autoIncrement()
    val currency: Column<String> = varchar("currency", 10)
    val balance: Column<BigDecimal> = decimal("balance", 36, 16)
    val lockVersion: Column<Int> = integer("lock_version")
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(id, name = "PK_Accounts_Id")
}

class AccountRepository {
    private fun resultRowToAccount(row: ResultRow) = Account(
        id = row[Accounts.id],
        currency = row[Accounts.currency],
        balance = row[Accounts.balance],
        lockVersion = row[Accounts.lockVersion],
        createdAt = row[Accounts.createdAt],
        updatedAt = row[Accounts.updatedAt],
    )

    fun findById(id: Long): Account? = transaction {
        Accounts.select { Accounts.id eq id }
            .map(::resultRowToAccount)
            .singleOrNull()
    }

    fun debit(accountId: Long, debitAmount: BigDecimal, lockVersion: Int) = transaction {
        Accounts.update({
            Accounts.id eq accountId and
                    (Accounts.balance greaterEq debitAmount) and
                    (Accounts.lockVersion eq lockVersion)
        }) {
            with(SqlExpressionBuilder) {
                it[Accounts.balance] = Accounts.balance - debitAmount
                it[Accounts.lockVersion] = Accounts.lockVersion + 1
                it[Accounts.updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            }
        }
    }

    fun credit(accountId: Long, creditAmount: BigDecimal, lockVersion: Int) = transaction {
        Accounts.update({
            Accounts.id eq accountId and
                    (Accounts.lockVersion eq lockVersion)
        }) {
            with(SqlExpressionBuilder) {
                it[Accounts.balance] = Accounts.balance + creditAmount
                it[Accounts.lockVersion] = Accounts.lockVersion + 1
                it[Accounts.updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            }
        }
    }
}