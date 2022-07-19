package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Log
import com.example.myapplication.Resource
import com.example.myapplication.model.local.CurrenciesModelLocal
import com.example.myapplication.model.local.ExchangeRateModelLocal
import com.example.myapplication.model.request.ExchangeRateRequest
import com.example.myapplication.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterFragmentViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) :
    ViewModel() {

    init {
        getSupportedCurrency()
    }

    private val _supportedCurrencyMutableLiveData: MutableLiveData<Resource<out CurrenciesModelLocal?>> =
        MutableLiveData()
    var supportedCurrencyLiveData: LiveData<Resource<out CurrenciesModelLocal?>> =
        _supportedCurrencyMutableLiveData

    private val _userSelectedCurrencyMutableLive: MutableLiveData<String> = MutableLiveData()
    val liveUserSelectedCurreny: LiveData<String> = _userSelectedCurrencyMutableLive

    private val _currencieExchangeRateMutableLiveData: MutableLiveData<Resource<out ExchangeRateModelLocal?>> =
        MutableLiveData()
    val exchangeRateLiveData: LiveData<Resource<out ExchangeRateModelLocal?>> =
        _currencieExchangeRateMutableLiveData

    private val _amountMutableLiveData: MutableLiveData<Double?> = MutableLiveData()
    val amountLiveData: LiveData<Double?> = _amountMutableLiveData


    fun getSupportedCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            currencyRepository.getSupportedCurrency().collect {
                _supportedCurrencyMutableLiveData.postValue(it)
            }
        }
    }


    fun setUserCurrency(currencyCode: String) {
        _userSelectedCurrencyMutableLive.value = currencyCode
        viewModelScope.launch(Dispatchers.IO) {
            currencyRepository.getExchangeRate(ExchangeRateRequest(currencyCode)).onEach {

            }.collect {
                when(it){
                    is Resource.Success->{
                        it.data?.exchangeRateReponse?.rates?.forEach { (s, d) ->
                            Log("${currencyCode} -> $s ${2*d}")
                        }
                    }
                }
                    _currencieExchangeRateMutableLiveData.postValue(it)
                }
        }
    }

    fun setNewAmount() {
        viewModelScope.launch(Dispatchers.Default) {

        }
    }
}