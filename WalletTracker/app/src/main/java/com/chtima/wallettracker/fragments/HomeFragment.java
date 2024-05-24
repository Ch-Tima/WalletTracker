package com.chtima.wallettracker.fragments;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.TransactionAdapter;
import com.chtima.wallettracker.components.Swicher;
import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.models.CategoryWithTransactions;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.models.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private User user;
    private AppDatabase database;

    private TransactionType transactionType; // transactionType is used in "filterTransactionWithUpdateUI"

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<CategoryWithTransactions> categoryWithTransactions = new ArrayList<>();

    private static final String USER_PARCELABLE = "USER_PARCELABLE";


    //ui
    private RecyclerView recyclerView;
    private PieChart pieChart;
    private Button btnBalance;

    private Swicher swicher;

    //Adapters
    private TransactionAdapter transactionAdapter;


    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_PARCELABLE, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getInstance(getContext());
        user = getArguments().getParcelable(USER_PARCELABLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //load_data
        loadTransactions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //ui
        ((ImageButton)view.findViewById(R.id.btn_add)).setOnClickListener(x -> {
            AddTransactionDialogFragment dialogFragment = AddTransactionDialogFragment.newInstance();
            dialogFragment.setSubscribe(new DialogObserver<Transaction>() {
                @Override
                public void onSuccess(Transaction obj) {
                    Disposable disposable = database.transactionDao().insert(obj)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(id -> {
                            }, er ->{
                                Log.e("Er", er.toString());
                            }, () -> {}, compositeDisposable);
                }

                @Override
                public void onCancel() {}
            });
            dialogFragment.show(getChildFragmentManager(), AddTransactionDialogFragment.class.getName());
        });

        //recyclerView
        transactionAdapter = new TransactionAdapter(getContext(), toTransactionList());
        recyclerView = view.findViewById(R.id.transaction_recycle);
        recyclerView.setAdapter(transactionAdapter);

        //pieChart
        pieChart = (PieChart) view.findViewById(R.id.pieChart);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(getResources().getColor(R.color.transparent, null));

        //swicher_transaction_type
        swicher = view.findViewById(R.id.swicher_transaction_type);
        swicher.setOnChangedSelectionListener(transactionType -> {
            this.transactionType = transactionType;
            this.filterTransactionWithUpdateUI();
        });

        //btns
        btnBalance = view.findViewById(R.id.balance_btn);
        btnBalance.setText(user.balance + "$");//set user balance to "balance_btn"

        return view;
    }

    private void filterTransactionWithUpdateUI(){
        if(categoryWithTransactions.isEmpty()) return;

        List<Transaction> transactionsFilterType = categoryWithTransactions.stream()
                .map(x -> x.transactions)
                .flatMap(Collection::stream)
                .filter(x -> x.type == HomeFragment.this.transactionType)
                .sorted((s1, s2) -> s1.dateTime.compareTo(s2.dateTime))
                .collect(Collectors.toList());

        transactionAdapter.updateList(transactionsFilterType);

        List<PieEntry> entries1 = new ArrayList<>();

        categoryWithTransactions.stream().map(item -> {
                    double sum = item.transactions.stream()
                            .filter(x -> x.type == HomeFragment.this.transactionType)
                            .mapToDouble(t -> t.sum)
                            .sum();
                    return new AbstractMap.SimpleEntry<>(item, sum);
                }).filter(x -> x.getValue() > 0)
                .sorted((s1, s2) -> Double.compare(s2.getValue(), s1.getValue()))
                .limit(4)
                .forEach(x -> {
                    entries1.add(new PieEntry(x.getValue().floatValue(), x.getKey().category.title));
                });


        PieDataSet ds1 = new PieDataSet(entries1, "transaction");

        ds1.setSliceSpace(3f);
        ds1.setSelectionShift(5f);
        ds1.setColors(getResources().getColor(R.color.saffron, null),
                getResources().getColor(R.color.peach, null),
                getResources().getColor(R.color.silver_sand, null),
                getResources().getColor(R.color.lilac_grey, null));

        PieData pieData = new PieData(ds1);
        pieData.setValueTypeface(getResources().getFont(R.font.outfit_medium));
        pieData.setValueTextSize(16f);
        pieData.setValueTextColor(getResources().getColor(R.color.white, null));

        pieChart.setEntryLabelTypeface(getResources().getFont(R.font.nunito_regular));

        pieChart.setData(pieData);
        pieChart.invalidate();


    }

    private void loadTransactions(){
        database.categoryDao().getCategoriesWithTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    categoryWithTransactions.clear();
                    categoryWithTransactions.addAll(list);
                    filterTransactionWithUpdateUI();
                }, er -> {
                    Log.e("er", er.toString());
                }, () ->{}, compositeDisposable);
    }

    private List<Transaction> toTransactionList() {
        return categoryWithTransactions.stream()
                .map(x -> x.transactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.compositeDisposable.clear();
    }
}