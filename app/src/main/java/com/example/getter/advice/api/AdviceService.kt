package com.example.getter.advice.api

import com.example.getter.advice.data.AdviceResult
import retrofit2.http.GET
import retrofit2.http.Path

interface AdviceService {

    @GET("/advice")
    suspend fun retrieveAdvice(): AdviceResult

    //API service provides not valid JSON response
    @GET("/advice/{id}")
    suspend fun getParticularAdvice(@Path("id") id: Int): AdviceResult
}