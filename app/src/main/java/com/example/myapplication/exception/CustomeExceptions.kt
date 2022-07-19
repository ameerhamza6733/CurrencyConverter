package com.example.myapplication.exception

import com.example.myapplication.model.remote.ErrorResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody


class UnauthenticatedExeption(override val message: String? = null) : Exception()
class RestrictedAccessException(override val message: String? = null) : Exception()
class OverUseEXception(override val message: String?) : Exception()
class AccessRestrictedException(override val message: String?) : Exception()
class InvalideAppIDException(override val message: String?) : Exception()
class NotAllowedException(override val message: String?) : Exception()
class GetRatesByCurrencyException(override val message: String? = null) : Exception()

fun getGetCustomExceptionFromErrorCode(error: Int, errorBody: ResponseBody?): Throwable {
    val errorMessage = try {
        val gson = Gson()
        val type = object : TypeToken<ErrorResponse>() {}.type
        var errorResponse: ErrorResponse? = gson.fromJson(errorBody!!.charStream(), type)
        errorResponse
    } catch (e: java.lang.Exception) {
        ErrorResponse("Unknown Error",-1)
    }
    when (error) {
        429 -> {
            return NotAllowedException(errorMessage?.description.toString())
        }
        403 -> {
            return if (errorMessage?.status==429){
                OverUseEXception(errorMessage?.description.toString())
            }else{
                AccessRestrictedException(errorMessage?.description.toString())
            }
        }
        401 -> {
            return InvalideAppIDException(errorMessage?.description.toString())
        }
        else -> {
            return Exception("Unknown error with error code ${error}")
        }


    }
}