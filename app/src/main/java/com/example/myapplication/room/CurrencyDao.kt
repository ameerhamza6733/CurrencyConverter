package com.example.myapplication.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.model.local.CurrenciesModelLocal
import com.example.myapplication.model.local.ExchangeRateModelLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSupportedCurrency(currenciesModelLocal: CurrenciesModelLocal)

    //this table have only one row, so it will will retrun only one recode even we query to whole table
    @Query("SELECT * FROM currenciesmodellocal")
    fun getSupportedCurrency(): Flow<CurrenciesModelLocal?>

    @Query("SELECT * FROM exchangeratemodellocal WHERE baseCurrencie == :curreny")
    fun getExchangeRateByCurrencie(curreny: String):Flow<ExchangeRateModelLocal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExchangeRatesByCurreny(exchangeRateModelLocal:ExchangeRateModelLocal)


}