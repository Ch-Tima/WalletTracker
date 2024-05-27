package com.chtima.wallettracker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.TransactionType;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final Context context;
    private final List<Transaction> list;

    public TransactionAdapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.transaction_card_line, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction item = list.get(position);

        holder.title.setText(item.getDate());

        if(item.type == TransactionType.EXPENSE){
            holder.sum.setText("-" + item.sum);
            holder.sum.setTextColor(context.getResources().getColor(R.color.coral, null));
        }else {
            holder.sum.setText("+" + item.sum);
            holder.sum.setTextColor(context.getResources().getColor(R.color.lime, null));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Transaction> list){
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView title;
        final TextView sum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.sum = itemView.findViewById(R.id.sum);
        }
    }

}
