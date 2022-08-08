package com.example

import com.example.dtos.CreateTransferDTO
import com.example.entities.Account
import com.example.entities.Transfer
import com.example.services.AccountService
import com.example.services.TransferService
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.util.*

fun Application.configureRouting(
    accountService: AccountService,
    transferService: TransferService,
) {

    // Starting point for a Ktor app:
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }

        route("/v1") {
            get("/accounts/{id}") {
                val id = call.parameters.getOrFail<Long>("id").toLong()
                val account: Account = accountService.findById(id)

                call.respond(account)
            }

            get("/transfers") {
                val transfers: List<Transfer> = transferService.findAll()
                call.respond(transfers)
            }

            post("/transfers") {
                val createTransferDTO = call.receive<CreateTransferDTO>()
                val transfer: Transfer = transferService.create(createTransferDTO)

                call.respond(HttpStatusCode.Created, transfer)
            }
        }

    }
}
