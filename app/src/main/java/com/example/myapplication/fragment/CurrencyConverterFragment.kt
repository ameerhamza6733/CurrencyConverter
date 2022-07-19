package com.example.myapplication.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.myapplication.R
import com.example.myapplication.Resource
import com.example.myapplication.databinding.FragmentCurrencyConverterBinding
import com.example.myapplication.isInternetError
import com.example.myapplication.model.local.CurrenciesModelLocal
import com.example.myapplication.model.local.ExchangeRateModelLocal
import com.example.myapplication.model.remote.CurrenciesResponse
import com.example.myapplication.viewmodel.CurrencyConverterFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CurrencyConverterFragment : Fragment(R.layout.fragment_currency_converter) {

    private val viewModel by viewModels<CurrencyConverterFragmentViewModel>()
    private val binding by viewBinding(FragmentCurrencyConverterBinding::bind)
    private var supportedCurrencyObserver: Observer<Resource<out CurrenciesModelLocal?>>? = null
    private var currenciesExcahngeRateObserver:Observer<Resource<out ExchangeRateModelLocal?>>?=null
    private var userSelectCurrency: Observer<String>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserves()
    }

    private fun initObserves() {

        if (supportedCurrencyObserver == null) {
            supportedCurrencyObserver = Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressCircular.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressCircular.visibility = View.INVISIBLE
                        resource.data?.currenciesRespons?.let { setDateToSpinner(it) }
                    }
                    is Resource.Error -> {
                        binding.progressCircular.visibility = View.INVISIBLE
                        resource.error?.printStackTrace()
                        if (resource.error?.isInternetError() == true) {
                            resource.data?.currenciesRespons?.let { setDateToSpinner(it) }
                            Snackbar.make(
                                binding.spinnerCurrency,
                                "Internal error",
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction("tryAgain") {
                                viewModel.getSupportedCurrency()
                            }.show()
                        } else {
                            Snackbar.make(
                                binding.spinnerCurrency,
                                "${resource.error?.message}",
                                Snackbar.LENGTH_INDEFINITE
                            ).show()
                        }
                    }
                }
            }
        }
        if (userSelectCurrency == null) {
            userSelectCurrency = Observer {
                binding.edEnterAmount.setHint("${it}")
            }
        }

        if (currenciesExcahngeRateObserver==null){
            currenciesExcahngeRateObserver= Observer {
                when(it){
                    is Resource.Error->{
                        Snackbar.make(
                            binding.spinnerCurrency,
                            "${it.error?.message}",
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }
            }
        }
        viewModel.exchangeRateLiveData.observe(viewLifecycleOwner,currenciesExcahngeRateObserver!!)
        viewModel.liveUserSelectedCurreny.observe(viewLifecycleOwner, userSelectCurrency!!)
        viewModel.supportedCurrencyLiveData.observe(viewLifecycleOwner, supportedCurrencyObserver!!)
    }

    private fun setDateToSpinner(listCurrencyModel: List<CurrenciesResponse>) {

        val spinnerArrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                listCurrencyModel?.map { it.countryCode }
                    .orEmpty()
            )
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = spinnerArrayAdapter
        binding.spinnerCurrency.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setUserCurrency(selectedItem)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


        })

    }
}