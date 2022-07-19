package com.example.myapplication.adupter

import android.R.attr.data
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.CurrenciesRowBinding
import com.example.myapplication.model.ui.CurrenciesListModelUI


class CurrenciesListAdupter(val list: List<CurrenciesListModelUI>) : RecyclerView.Adapter<CurrenciesListAdupter.ViewHolder>() {
   inner class ViewHolder(val binding:CurrenciesRowBinding):RecyclerView.ViewHolder(binding.root){
        fun bind()=binding.apply {
            binding.currenciName.text=list[adapterPosition].baseCurreniesInto
            binding.currenciConvertedAmout.text=list[adapterPosition].amountConversion
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CurrenciesRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return list.size
    }

}