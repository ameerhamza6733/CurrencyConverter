package com.example.myapplication.dateasource

import com.example.myapplication.exception.getGetCustomExceptionFromErrorCode
import com.example.myapplication.model.remote.CurrenciesResponse
import com.example.myapplication.model.remote.ExchangeRateReponse
import com.example.myapplication.model.request.ExchangeRateRequest
import com.example.myapplication.webservices.Api
import javax.inject.Inject
import kotlin.jvm.Throws

class CurrencieRemoteDataSource @Inject constructor(private val api: Api) {

    @Throws(Exception::class)
    suspend fun refreshCurrencyListCrash(): List<CurrenciesResponse> {

        return kotlin.runCatching {
            val reponseBody = api.getCurrencyList()
           if (reponseBody.isSuccessful){
               val currencyMap = reponseBody.body()
               val supportedCurrencyList = ArrayList<CurrenciesResponse>()
               currencyMap?.forEach { (key, value) ->
                   supportedCurrencyList.add(CurrenciesResponse(key, value))
               }
               supportedCurrencyList
           }else{
               throw getGetCustomExceptionFromErrorCode(reponseBody.code(),reponseBody.errorBody())
           }
        }.onFailure {
            throw it
        }.getOrThrow()

    }

    @Throws(Exception::class)
    suspend fun getCurrencyEchangeRate(exchangeRateRequest: ExchangeRateRequest): ExchangeRateReponse? {
        return kotlin.runCatching {
            val reponse= api.getExchangeRateByCurrency(exchangeRateRequest.baseCurrencie)
            if (reponse.isSuccessful){
                reponse.body()
            }else{
                throw getGetCustomExceptionFromErrorCode(reponse.code(),reponse.errorBody())
            }
         }.onFailure {
            it.printStackTrace()
            throw it
         }.getOrThrow()
    }


}