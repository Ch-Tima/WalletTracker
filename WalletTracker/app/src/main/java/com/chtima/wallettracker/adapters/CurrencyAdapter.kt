package com.chtima.wallettracker.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter;
import android.widget.TextView
import com.chtima.wallettracker.R

class CurrencyAdapter(private val context: Context, private val list: List<String>) :
    ArrayAdapter<String>(context, R.layout.item_currency_code, list) {

    private var selectedItemPosition = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(getContext()).inflate(R.layout.item_currency_code, parent, false)


        val text = getItem(position)

        val textView: TextView = view.findViewById(R.id.text1)
        textView.text = text

        if(selectedItemPosition == position){
            textView.setBackgroundResource(R.drawable.rounded_rectangle_8dp_blue)
            textView.setTextColor(getContext().getColor(R.color.light_ashen_35))
        }else{
            textView.setBackgroundResource(R.color.transparent)
            textView.setTextColor(getContext().getColor(R.color.darkened_slate_blue))
        }

        return view;
    }

    fun setSelectedItemPosition(position: Int){
        this.selectedItemPosition = position;
        notifyDataSetChanged();
        Log.d("H", "setSelectedItemPosition:$position")
    }
}