package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.flow.*

private val TAG="networkBoundResource"
fun <ResultType, RequestType> networkBoundResource(
    query: () -> Flow<ResultType?>,
    getFromNetwork: suspend () -> RequestType,
    saveToLocalDb: suspend (ResultType) -> Unit,
    shouldUpdateCach: (ResultType?) -> Boolean = { true }

) = flow {
    emit(Resource.Loading("Loading from disk",null))
    val data=query().firstOrNull()
   val flow= if (shouldUpdateCach(data)){
        try {
            emit(Resource.Loading("Loading from network",null))
           val list= getFromNetwork()
            saveToLocalDb(list as ResultType)
            query().map { Resource.Success(it) }
        }catch (throwable : Throwable){
            throwable.printStackTrace()
            query().map { Resource.Error(throwable,data) }

        }
    }else{
        query().map { Resource.Success(it) }
    }
    emitAll(flow)

}