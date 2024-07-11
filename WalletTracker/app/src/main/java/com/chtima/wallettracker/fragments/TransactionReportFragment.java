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
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.vm.CategoryViewModel;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionReportFragment extends Fragment {


    private CategoryViewModel viewModel;
    private List<CategoryWithTransactions> categoryWithTransactions;
    private TransactionAdapter transactionAdapter;
    private RecyclerView recyclerView;


    private TransactionReportFragment() {
    }

    public static TransactionReportFragment newInstance() {
        return new TransactionReportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryWithTransactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.getCategoriesWithTransactions().observeForever(list -> {
            categoryWithTransactions.clear();
            categoryWithTransactions.addAll(list);
            transactionAdapter.updateList(filter());
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_report, container, false);

        view.findViewById(R.id.filter).setOnClickListener(v -> {
            FilterDailogFragment filterDailogFragment = FilterDailogFragment.newInstance();
            filterDailogFragment.show(getChildFragmentManager(), AddTransactionDialogFragment.class.getName());
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setAdapter(transactionAdapter);

        return view;
    }

    private List<Transaction> filter(){
        return filter("", -1, -1, -1, null);
    }

    private List<Transaction> filter(String text, long categoryId, long startTime, long endTime, TransactionType type){


        Stream<CategoryWithTransactions> transactionsStream = categoryWithTransactions.stream();

        if(categoryId > 0){
            transactionsStream = transactionsStream.filter(categoryWithTransactions -> categoryWithTransactions.category.id == categoryId);
        }

        if(startTime > endTime) endTime = startTime + endTime - (startTime = endTime);

        long finalStartTime = startTime;
        long finalEndTime = endTime;

        List<Transaction> list = transactionsStream.map(x -> x.transactions)
                .flatMap(Collection::stream)
                .filter(x -> {

                    if(!text.isEmpty() && !(x.title + x.note).toLowerCase().contains(text))
                        return false;

                    if(type != null && x.type != type)
                        return false;

                    if(x.dateTime.before(new Date(finalStartTime)) || x.dateTime.after(new Date(finalEndTime)))
                        return false;

                    return true;
                }).collect(Collectors.toList());

        return list;

    }

}