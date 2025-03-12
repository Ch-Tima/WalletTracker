package com.chtima.wallettracker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.models.Transaction.TransactionType

class TransactionAdapter constructor (private val context: Context, private val list: MutableList<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.transaction_card_line, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Transaction = list[position];

        holder.title.text = item.title
        if(item.type == TransactionType.EXPENSE){
            "-${item.sum}".also { holder.sum.text = it }
            holder.sum.setTextColor(context.getColor(R.color.coral));
        }else {
            "+${item.sum}".also { holder.sum.text = it }
            holder.sum.setTextColor(context.getColor(R.color.lime));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Transaction>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title);
        var sum: TextView = itemView.findViewById(R.id.sum);
    }

}
