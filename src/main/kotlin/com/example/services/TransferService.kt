package com.example.services

import com.example.dtos.CreateTransferDTO
import com.example.exceptions.NotFoundException
import com.example.entities.*
import com.example.repositories.TransferRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.RuntimeException

class TransferService(
    private val accountService: AccountService,
    private val transferRepository: TransferRepository
) {
    fun findById(id: Long): Transfer {
        return transferRepository.findById(id) ?: throw NotFoundException("transfer");
    }

    fun findAll(): List<Transfer> {
        return transferRepository.findAll()
    }

    fun create(dto: CreateTransferDTO): Transfer {
        println(dto)
        // TODO: DTO validation

        // Remark: simply wrap the transfer into one single transaction
        val transfer = transaction {
            val transfer = transferRepository.create(
                senderAccountId = dto.senderAccountId,
                recipientAccountId = dto.recipientAccountId,
                currency = dto.currency,
                amount = dto.amount,
                idempotencyKey = dto.idempotencyKey,
            )

            println(transfer)
            processTransfer(transfer)
            findById(transfer.id)
        }
        return transfer
    }

    private fun processTransfer(transfer: Transfer) {
        val senderAccount = accountService.findById(transfer.senderAccountId)
        println(senderAccount)
        val recipientAccount = accountService.findById(transfer.recipientAccountId)
        println(recipientAccount)

        accountService.debit(
            account = senderAccount,
            currency = transfer.currency,
            amount = transfer.amount,
            sourceType = "transfer",
            sourceId = transfer.id.toString(),
        )
        accountService.credit(
            account = recipientAccount,
            currency = transfer.currency,
            amount = transfer.amount,
            sourceType = "transfer",
            sourceId = transfer.id.toString(),
        )
        transferRepository.updateById(id = transfer.id, status = "done", lockVersion = transfer.lockVersion)
    }
}