package com.chtima.wallettracker.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.TransactionAdapter;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.CategoryWithTransactions;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.vm.CategoryViewModel;
import com.google.android.material.textfield.TextInputEditText;

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

    private static final String ARG_USERID = "arg_user_id";

    private long userId;

    private CategoryViewModel viewModel;
    private List<CategoryWithTransactions> categoryWithTransactions;
    private TransactionAdapter transactionAdapter;
    private FilterDailogFragment.FilterParameters filterParameters;

    //UI
    private RecyclerView recyclerView;
    private TextInputEditText textInputEditText;
    private ImageView filter;


    private TransactionReportFragment() {
    }

    public static TransactionReportFragment newInstance(long userId) {
        TransactionReportFragment fragment = new TransactionReportFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) userId = getArguments().getLong(ARG_USERID);
        categoryWithTransactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.getCategoriesWithTransactionsByUserId(userId).observeForever(list -> {
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

        textInputEditText = (TextInputEditText)view.findViewById(R.id.search_input);

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                transactionAdapter.updateList(filter(String.valueOf(textInputEditText.getText()), filterParameters));
            }
        });

        filter = (ImageView) view.findViewById(R.id.filter);

        filter.setOnClickListener(v -> {
            DialogObserver<FilterDailogFragment.FilterParameters> dialogObserver = new DialogObserver<FilterDailogFragment.FilterParameters>() {
                @Override
                public void onSuccess(FilterDailogFragment.FilterParameters parameters) {
                    filterParameters = parameters;

                    if(parameters.isEmpty())
                        filter.setImageTintList(requireContext().getColorStateList(R.color.dark_midnight_blue));
                    else
                        filter.setImageTintList(requireContext().getColorStateList(R.color.light_slate_blue));

                    transactionAdapter.updateList(filter(String.valueOf(textInputEditText.getText()), filterParameters));

                }

                @Override
                public void onCancel() {

                }
            };

            FilterDailogFragment filterDailogFragment = filterParameters == null ? FilterDailogFragment.newInstance(dialogObserver) : FilterDailogFragment.newInstance(dialogObserver, filterParameters);
            filterDailogFragment.show(getChildFragmentManager(), AddTransactionDialogFragment.class.getName());
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setAdapter(transactionAdapter);

        return view;
    }

    private List<Transaction> filter(){
        return filter("", new ArrayList<>(), -1, -1, null);
    }

    private List<Transaction> filter(String text, FilterDailogFragment.FilterParameters parameters){
        if(parameters != null && !parameters.isEmpty()){
            return filter(text, parameters.getCategories().stream().map(x -> x.id).collect(Collectors.toList()),
                    parameters.getStartData(), parameters.getEndData(), parameters.getTransactionType());
        }
        return filter(text, new ArrayList<>(), -1, -1, null);
    }

    private List<Transaction> filter(String text, List<Long> categoryIds, long startTime, long endTime, TransactionType type){

        Stream<CategoryWithTransactions> transactionsStream = categoryWithTransactions.stream();

        if(categoryIds != null && !categoryIds.isEmpty()){
            transactionsStream = transactionsStream.filter(categoryWithTransactions -> categoryIds.contains(categoryWithTransactions.category.id));
        }

        if(startTime > endTime) endTime = startTime + endTime - (startTime = endTime);

        long finalStartTime = startTime;
        long finalEndTime = endTime;

        return transactionsStream.map(x -> x.transactions)
                .flatMap(Collection::stream)
                .filter(x -> {

                    if(!text.isEmpty() && !(x.title + x.note + (x.type == TransactionType.EXPENSE ? "-" : "+") + x.sum).toLowerCase().contains(text))
                        return false;

                    if(type != null && x.type != type)
                        return false;

                    if(finalEndTime > 0 && finalStartTime > 0)
                        if(x.dateTime.before(new Date(finalStartTime)) || x.dateTime.after(new Date(finalEndTime)))
                            return false;

                    return true;
                }).collect(Collectors.toList());

    }

}