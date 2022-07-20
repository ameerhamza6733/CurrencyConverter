package com.example.myapplication.repository

import com.example.myapplication.dateasource.CurrencieRemoteDataSource
import com.example.myapplication.exception.AccessRestrictedException
import com.example.myapplication.exception.GetSupportedCurrencyListExcetion
import com.example.myapplication.exception.InvalideAppIDException
import com.example.myapplication.model.remote.ErrorResponse
import com.example.myapplication.webservices.Api
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
internal class CurrencyRepositoryTest {

    @Mock
    lateinit var service: Api
    @InjectMocks
    lateinit var dataSource: CurrencieRemoteDataSource
    private val TAG = "CurrencyRepositoryTest"


    @Test(expected = GetSupportedCurrencyListExcetion::class)
    fun giveErrorWhenResponseNull() {
       runBlocking {
           BDDMockito.given(service.getCurrencyList()).willReturn(null)
           dataSource.refreshCurrencyListCrash()
       }
    }

    @Test(expected = InvalideAppIDException::class)
    fun giveErrorWhenResponse401() {
        val errorResponseJson = Gson().toJson(ErrorResponse("invalid api key", status = 401))
        val errorResponseBody = errorResponseJson.toResponseBody("application/json".toMediaTypeOrNull())
        val mockResponse = Response.error<ResponseBody>(401, errorResponseBody)
       runBlocking {
           BDDMockito.given(service.getCurrencyList()).willReturn(mockResponse)
           dataSource.refreshCurrencyListCrash()
       }
    }

    @Test(expected = AccessRestrictedException::class)
    fun giveErrorWhenErrorResponse403(){
        val errorResponseJson = Gson().toJson(ErrorResponse("not_allowed", status = 403))
        val errorResponseBody = errorResponseJson.toResponseBody("application/json".toMediaTypeOrNull())
        val mockResponse = Response.error<ResponseBody>(403, errorResponseBody)
        runBlocking {
            BDDMockito.given(service.getCurrencyList()).willReturn(mockResponse)
            dataSource.refreshCurrencyListCrash()
        }
    }

}