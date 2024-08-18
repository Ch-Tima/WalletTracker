package com.chtima.wallettracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chtima.wallettracker.R;

import java.util.List;

public class CurrencyAdapter extends ArrayAdapter<String> {

    private int selectedItemPosition = -1;

    public CurrencyAdapter(@NonNull Context context, List<String> list) {
        super(context, R.layout.simple_dropdown_item_1line, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String str = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.text1);
        textView.setText(str);

        if(selectedItemPosition != position){
            textView.setBackgroundResource(R.color.transparent);
            textView.setTextColor(getContext().getColor(R.color.darkened_slate_blue));
        }else {
            textView.setBackgroundResource(R.drawable.round_blue_layout_8dp);
            textView.setTextColor(getContext().getColor(R.color.white));
        }

        return convertView;
    }

    public void setSelectedItemPosition(int position){
        this.selectedItemPosition = position;
    }
}
