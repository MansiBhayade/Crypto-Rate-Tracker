package com.example.crypto_rate

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crypto_rate.databinding.RvItemBinding
import java.text.DecimalFormat

class RvAdapter(val context: Context, var data:ArrayList<Modal>):RecyclerView.Adapter<RvAdapter.ViewHolder>(){


    inner class ViewHolder(val binding: RvItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RvItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val decimalFormat = DecimalFormat("#.######")
        val roundedPrice = decimalFormat.format(data[position].price.toDouble())

        Glide.with(context)
            .load(data[position].iconUrl)
            .into(holder.binding.image)
        holder.binding.name.text= data[position].name
        holder.binding.symbol.text= data[position].symbol
        holder.binding.price.text= "$ " + roundedPrice
    }

    fun setadapterData(newData: ArrayList<Modal>) {
        data = newData
        notifyDataSetChanged()
    }
}