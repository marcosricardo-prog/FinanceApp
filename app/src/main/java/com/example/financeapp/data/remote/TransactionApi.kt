package com.example.financeapp.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Serializable
data class DeleteRequest(val id: String)

interface TransactionApi {
    @GET("transactions")
    suspend fun getTransactions(): List<TransactionDto>

    @POST("transactions")
    suspend fun insertTransaction(@Body dto: TransactionDto): TransactionDto

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: String,
        @Body dto: TransactionDto
    ): TransactionDto

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(
        @Path("id") id: String
    )
}