package com.chtima.wallettracker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.models.Category;

import java.util.Calendar;
import java.util.List;

public class CategorySpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<Category> list;

    public CategorySpinnerAdapter(Context context, List<Category> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.categoty_line_card, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.title_category);
        textView.setText(getItem(position).title);

        return convertView;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Category getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Category c = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.categoty_line_card, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.setTitle(c.title);

        return convertView;

    }


    class ViewHolder {
        private TextView title;

        public ViewHolder(View view) {
            this.title = view.findViewById(R.id.title_category);
            view.setTag(this);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }
    }

}
