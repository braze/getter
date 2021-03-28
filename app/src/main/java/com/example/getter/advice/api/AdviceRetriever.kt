package com.example.getter.advice.api

import com.example.getter.advice.data.AdviceResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdviceRetriever {

    private val service: AdviceService

    companion object {
        private const val BASE_URL = "https://api.adviceslip.com"
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(AdviceService::class.java)
    }

    suspend fun getAdvice(): AdviceResult {
        return service.retrieveAdvice()
    }

    suspend fun getParticularAdvice(number: Int): AdviceResult {
        return service.getParticularAdvice(number)
    }
}