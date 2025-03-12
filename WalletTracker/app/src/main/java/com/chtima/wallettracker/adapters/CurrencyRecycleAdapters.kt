package com.chtima.wallettracker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R

class CurrencyRecycleAdapters(
    private val context : Context,
    private val originalList: List<String>) : RecyclerView.Adapter<CurrencyRecycleAdapters.ViewHolder>(){

    private var onItemClickListener:((String) -> Unit)? = null
    private var filteredList: MutableList<String> = originalList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_code, parent, false))
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]
        holder.textView.text = item
        // Set click listener for the item
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item) // Notify the listener
        }
    }

    fun setOnListener(listener: (String) -> Unit){
        this.onItemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(q: String){
        if(q.isBlank()) {
            filteredList = originalList.toMutableList()
            return
        }
        filteredList = originalList.toMutableList().filter { it.lowercase().contains(q.lowercase()) }.toMutableList()
        this.notifyDataSetChanged() // Refresh the RecyclerView
    }

    /**
     * ViewHolder class for caching views in each RecyclerView item.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.text1)
    }

}