package com.chtima.wallettracker.fragments;

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

import com.chtima.wallettracker.MainActivity;
import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.TransactionAdapter;
import com.chtima.wallettracker.components.Swicher;
import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.models.CategoryWithTransactions;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.models.User;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
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

    private Button btnBalance;

    private Swicher swicher;

    private SliderChartFragment sliderChartFragment;

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

        sliderChartFragment =  SliderChartFragment.newInstance();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.slide_chart_fragment, sliderChartFragment, SliderChartFragment.class.getName())
                .commit();


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

    //Method to filter transactions based on the transaction type and update the UI
    private void filterTransactionWithUpdateUI(){
        if(categoryWithTransactions.isEmpty()) return;

        List<Transaction> transactionsFilterType = categoryWithTransactions.stream()
                .map(x -> x.transactions)
                .flatMap(Collection::stream)
                .filter(x -> x.type == HomeFragment.this.transactionType)
                .sorted(Comparator.comparing((Transaction s) -> s.dateTime).reversed())
                .collect(Collectors.toList());

        transactionAdapter.updateList(transactionsFilterType);

        //setPieChartToday
        List<PieEntry> pieChartToday = new ArrayList<>();
        categoryWithTransactions.stream().map(item -> {
            double sum = item.transactions.stream()
                    .filter(
                            x -> x.type == HomeFragment.this.transactionType &&
                                    x.dateTime.toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate().equals(
                                                    MainActivity.nowDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                            )
                    )
                    .mapToDouble(t -> t.sum)
                    .sum();
            return new AbstractMap.SimpleEntry<>(item, sum);
        }).filter(x -> x.getValue() > 0)
                .sorted((s1, s2) -> Double.compare(s2.getValue(), s1.getValue()))
                .limit(4)
                .forEach(x -> {
                    pieChartToday.add(new PieEntry(x.getValue().floatValue(), x.getKey().category.title));
                });
        this.sliderChartFragment.setPieChartToday(pieChartToday);

        //LAST_WEEK
        List<BarEntry> pieBarChartLastWeek = new ArrayList<>();
        getSumTransactionLastWeek().forEach(x -> pieBarChartLastWeek.add(new BarEntry(
                        (float)LocalDate.parse(x.getKey()).getDayOfWeek().getValue(),
                        x.getValue().floatValue())
                )
        );
        this.sliderChartFragment.setBarChartLastWeek(pieBarChartLastWeek);

        //date now 2024-05-23 23:34:36
        //this week 20 - 26
        //setBarChartThisWeek
        List<BarEntry> pieBarChartThisWeek = new ArrayList<>();
        getSumTransactionThisWeek()
                .forEach(x -> pieBarChartThisWeek.add(new BarEntry(
                        (float)LocalDate.parse(x.getKey()).getDayOfWeek().getValue(), x.getValue().floatValue())
                ));
        this.sliderChartFragment.setBarChartThisWeek(pieBarChartThisWeek);

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


    /**
     * @return SimpleEntry, where KEY is the date and VALUE is the sum of all completed transactions. Over the this week.
    * */
    private List<AbstractMap.SimpleEntry<String, Double>> getSumTransactionThisWeek(){
        Calendar startThisWeek = Calendar.getInstance();
        startThisWeek.setTime(MainActivity.nowDate);
        startThisWeek.add(Calendar.DAY_OF_WEEK, -startThisWeek.get(Calendar.DAY_OF_WEEK) + 1);

        Calendar endThisWeek = (Calendar) startThisWeek.clone();
        endThisWeek.add(Calendar.DATE, 7);

        //search for all transactions for the THIS week
        return getSumTransactionForPeriod(startThisWeek.getTime(), endThisWeek.getTime());

    }

    /**
     * @return SimpleEntry, where KEY is the date and VALUE is the sum of all completed transactions. Over the past week.
     * */
    private List<AbstractMap.SimpleEntry<String, Double>>  getSumTransactionLastWeek(){
        Calendar startLastWeek = Calendar.getInstance();//last week 13 - 19
        startLastWeek.setTime(MainActivity.nowDate);
        startLastWeek.add(Calendar.DAY_OF_WEEK, -startLastWeek.get(Calendar.DAY_OF_WEEK) + 1);
        startLastWeek.add(Calendar.DAY_OF_MONTH, -7);

        Calendar endLastWeek = (Calendar) startLastWeek.clone();
        endLastWeek.add(Calendar.DATE, 7);

        //search for all transactions for the LAST week
        return getSumTransactionForPeriod(startLastWeek.getTime(), endLastWeek.getTime());
    }


    /**
     * @param start filtering start date.
     * @param end filtering end date.
     * @return SimpleEntry, where KEY is the date and VALUE is the sum of all completed transactions. For the period specified in the date parameters.
     * */
    private List<AbstractMap.SimpleEntry<String, Double>> getSumTransactionForPeriod(Date start, Date end){
        return toTransactionList().stream().filter(x -> x.type == transactionType && (x.dateTime.before(end) && x.dateTime.after(start)))
                .collect(Collectors.groupingBy(Transaction::getDate))//grouping by date
                .entrySet().stream()//Calculation of the amount on a specific day of the week
                .map(x -> new AbstractMap.SimpleEntry<String, Double>(x.getKey(), x.getValue().stream().mapToDouble(y -> y.sum).sum()))
                .collect(Collectors.toList());
    }

    /**
     * @return all Transaction from List&lt;CategoryWithTransactions&gt;
     * @see HomeFragment#categoryWithTransactions */
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