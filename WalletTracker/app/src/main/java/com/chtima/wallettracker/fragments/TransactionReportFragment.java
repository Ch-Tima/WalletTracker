package com.chtima.wallettracker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.TransactionAdapter;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.CategoryWithTransactions;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.vm.CategoryViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionReportFragment extends Fragment {




    private TransactionReportFragment() {
    }

    public static TransactionReportFragment newInstance() {
        return new TransactionReportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_report, container, false);

        return view;
    }


}