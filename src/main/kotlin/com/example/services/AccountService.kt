package com.example.services

import com.example.exceptions.InsufficentBalanceException
import com.example.entities.Account
import com.example.exceptions.InvalidCurrencyException
import com.example.repositories.AccountRepository
import java.math.BigDecimal

class AccountService(private val accountRepository: AccountRepository) {
    fun findById(id: Long): Account {
        return accountRepository.findById(id) ?: throw RuntimeException("not found");
    }

    fun debit(account: Account, currency: String, amount: BigDecimal, sourceType: String, sourceId: String) {
        if (account.balance < amount) {
            throw InsufficentBalanceException("debit")
        }
        if (account.currency != currency) {
            throw InvalidCurrencyException("debit")
        }
        // TODO: log sourceType sourceId
        accountRepository.debit(accountId = account.id, debitAmount = amount, lockVersion = account.lockVersion)
    }

    fun credit(account: Account, currency: String, amount: BigDecimal, sourceType: String, sourceId: String) {
        // TODO: log sourceType sourceId
        if (account.currency != currency) {
            throw InvalidCurrencyException("credit")
        }
        accountRepository.credit(accountId = account.id, creditAmount = amount, lockVersion = account.lockVersion)
    }
}