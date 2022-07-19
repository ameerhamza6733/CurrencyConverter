package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.util.Utils
import com.example.myapplication.dateasource.CurrencieRemoteDataSource
import com.example.myapplication.di.RetrofitProviderModule
import com.example.myapplication.model.local.CurrenciesModelLocal
import com.example.myapplication.model.local.ExchangeRateModelLocal
import com.example.myapplication.model.request.ExchangeRateRequest
import com.example.myapplication.networkBoundResource
import com.example.myapplication.room.CurrencyDataBase
import javax.inject.Inject


class CurrencyRepository @Inject constructor(
    private val db: CurrencyDataBase,
    private val currencieRemoteDataSource: CurrencieRemoteDataSource
) {
    private val TAG = "CurrencyRepository"

    fun getSupportedCurrency() = networkBoundResource(query = {
        db.currencyDao().getSupportedCurrency()
    }, getFromNetwork = {
        CurrenciesModelLocal(currenciesRespons = currencieRemoteDataSource.refreshCurrencyListCrash())
    }, saveToLocalDb = {
        db.currencyDao().insertSupportedCurrency(it!!)
    }, shouldUpdateCach = {
        if (it == null) {
            true
        } else {
            Utils.getTimeDifferenceInMints(it?.lastRefreshTime) > RetrofitProviderModule.THRESHOLD_API_REFRESH_TIME_MINTS
        }

    })

    fun getExchangeRate(exchangeRateRequest: ExchangeRateRequest) = networkBoundResource(query = {
        Log.d(TAG, "load from dao ${exchangeRateRequest.baseCurrencie}")
        db.currencyDao().getExchangeRateByCurrencies(exchangeRateRequest.baseCurrencie)
    }, getFromNetwork = {
        if (exchangeRateRequest.equals("USD")) {
            ExchangeRateModelLocal(
                baseCurrencie = exchangeRateRequest.baseCurrencie,
                exchangeRateReponse = currencieRemoteDataSource.getCurrencyEchangeRate(
                    exchangeRateRequest
                )
            )

        } else {
            val response = currencieRemoteDataSource.getCurrencyEchangeRate(
                ExchangeRateRequest("USD")
            )
            val baseCurrencyInUsdRate = response?.rates?.get(exchangeRateRequest.baseCurrencie)
            val map: HashMap<String, Double> = HashMap()
            response?.rates?.forEach { (currency, rate) ->
                map.put(currency, rate / baseCurrencyInUsdRate!!)
            }
            response?.rates = map
            response?.base = exchangeRateRequest.baseCurrencie
            ExchangeRateModelLocal(
                baseCurrencie = exchangeRateRequest.baseCurrencie,
                exchangeRateReponse = response
            )
        }
    }, saveToLocalDb = {
        db.currencyDao().insertExchangeRatesByCurrency(it!!)
    }, shouldUpdateCach = {
        if (it == null) {
            true
        } else {
            Utils.getTimeDifferenceInMints(
                it.lastRefreshTime
            ) >= RetrofitProviderModule.THRESHOLD_API_REFRESH_TIME_MINTS

        }

    })

}