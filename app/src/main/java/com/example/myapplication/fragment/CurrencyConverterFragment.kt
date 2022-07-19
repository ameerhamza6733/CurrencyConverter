package com.example.myapplication.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adupter.CurrenciesListAdupter
import com.example.myapplication.databinding.FragmentCurrencyConverterBinding
import com.example.myapplication.model.local.CurrenciesModelLocal
import com.example.myapplication.model.remote.CurrenciesResponse
import com.example.myapplication.model.ui.CurrenciesListModelUI
import com.example.myapplication.util.Resource
import com.example.myapplication.util.Utils
import com.example.myapplication.viewmodel.CurrencyConverterFragmentViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CurrencyConverterFragment : Fragment(R.layout.fragment_currency_converter) {

    private val viewModel by viewModels<CurrencyConverterFragmentViewModel>()
    private val binding by viewBinding(FragmentCurrencyConverterBinding::bind)
    private var supportedCurrencyObserver: Observer<Resource<CurrenciesModelLocal?>>? = null
    private var currenciesExchangeRateObserver: Observer<Resource<List<CurrenciesListModelUI>>>? =
        null
    private var userSelectCurrency: Observer<String>? = null
    private var currenciesListAdupter: CurrenciesListAdupter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserves()

        binding.edEnterAmount.doAfterTextChanged {
            it?.let {
                if (TextUtils.isDigitsOnly(it.toString()) && it.isNotEmpty()) {
                    viewModel.setNewAmount(it.toString().toDouble())
                }
            }
        }

        binding.btConverte.setOnClickListener {
            if (TextUtils.isDigitsOnly(binding.edEnterAmount.text.toString()) && binding.edEnterAmount.text.isNotEmpty()) {
                viewModel.setNewAmount(binding.edEnterAmount.text.toString().toDouble())
                viewModel.getExchangeRates()
            }
        }
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
                        setDateToSpinner(resource.data?.currenciesRespons)
                        Utils.showError(resource.error, binding.btConverte) {
                            viewModel.getSupportedCurrency()
                        }

                    }
                }
            }
        }
        if (userSelectCurrency == null) {
            userSelectCurrency = Observer {
                binding.edEnterAmount.hint = "${it}"
                binding.listCurrences.adapter = null
            }
        }

        if (currenciesExchangeRateObserver == null) {
            currenciesExchangeRateObserver = Observer {
                when (it) {
                    is Resource.Error -> {
                        binding.progressCircular.visibility = View.INVISIBLE
                        binding.edEnterAmount.isEnabled = true
                        binding.btConverte.isEnabled = true
                        setDateToCurrencyRecylerView(it.data)
                        Utils.showError(it.error, binding.btConverte) {
                            binding.btConverte.callOnClick()
                        }
                    }
                    is Resource.Success -> {

                        binding.progressCircular.visibility = View.INVISIBLE
                        binding.edEnterAmount.isEnabled = true
                        binding.btConverte.isEnabled = true
                        setDateToCurrencyRecylerView(it.data)
                    }
                    is Resource.Loading -> {
                        binding.progressCircular.visibility = View.VISIBLE
                        binding.btConverte.isEnabled = false
                        binding.edEnterAmount.isEnabled = false
                        binding.listCurrences.adapter = null
                    }
                }
            }
        }
        viewModel.exchangeRateLiveData.observe(viewLifecycleOwner, currenciesExchangeRateObserver!!)
        viewModel.liveUserSelectedCurrency.observe(viewLifecycleOwner, userSelectCurrency!!)
        viewModel.supportedCurrencyLiveData.observe(viewLifecycleOwner, supportedCurrencyObserver!!)
    }

    private fun setDateToCurrencyRecylerView(list: List<CurrenciesListModelUI>?) {
        list?.let {
            currenciesListAdupter = CurrenciesListAdupter(it)
            binding.listCurrences.adapter = currenciesListAdupter
            binding.listCurrences.layoutManager = GridLayoutManager(requireActivity(), 3)
        }

    }

    private fun setDateToSpinner(listCurrencyModel: List<CurrenciesResponse>?) {

     listCurrencyModel?.let {
         val spinnerArrayAdapter: ArrayAdapter<String> =
             ArrayAdapter<String>(
                 requireActivity(),
                 android.R.layout.simple_spinner_item,
                 listCurrencyModel.map { it.countryCode }
                     .orEmpty()
             )
         spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
         binding.spinnerCurrency.adapter = spinnerArrayAdapter
         binding.spinnerCurrency.onItemSelectedListener = object :
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

         }
     }

    }
}