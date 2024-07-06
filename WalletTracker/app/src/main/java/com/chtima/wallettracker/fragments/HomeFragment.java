package com.chtima.wallettracker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.chtima.wallettracker.models.CategoryWithTransactions;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.models.User;
import com.chtima.wallettracker.vm.CategoryViewModel;
import com.chtima.wallettracker.vm.TransactionViewModel;
import com.chtima.wallettracker.vm.UserViewModel;
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

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class HomeFragment extends Fragment {

    private User user = null;

    private TransactionType transactionType; // transactionType is used in "filterTransactionWithUpdateUI"

    private final List<CategoryWithTransactions> categoryWithTransactions = new ArrayList<>();


    //ViewModels
    private TransactionViewModel transactionVM;
    private CategoryViewModel categoryVM;
    private UserViewModel userVM;

    //ui
    private RecyclerView recyclerView;

    private Button btnBalance;

    private Swicher swicher;

    private SliderChartFragment sliderChartFragment;

    //Adapters
    private TransactionAdapter transactionAdapter;
    private AddTransactionDialogFragment addTransactionDialogFragment;

    private HomeFragment(){}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryVM = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionVM = new ViewModelProvider(this).get(TransactionViewModel.class);
        userVM = new ViewModelProvider(this).get(UserViewModel.class);

        updateCategoriesWithTransactions(); //only once!
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //ui
        ((ImageButton)view.findViewById(R.id.btn_add)).setOnClickListener(x -> {

            addTransactionDialogFragment = AddTransactionDialogFragment.newInstance();
            addTransactionDialogFragment.setSubscribe(new DialogObserver<Transaction>() {
                @Override
                public void onSuccess(Transaction obj) {
                   transactionVM.insert(obj)
                           .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(HomeFragment.this)))
                           .subscribe(id -> {
                               obj.id = id;
                               if (TransactionType.EXPENSE == obj.type)
                                   user.deductFromBalance(obj.sum);
                               else
                                   user.addToBalance(obj.sum);

                               btnBalance.setText(String.valueOf(user.balance));

                               userVM.update(HomeFragment.this.user)
                                       .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(HomeFragment.this)))
                                       .subscribe(
                                               () -> Log.i("WW-INFO-C", "OK"),
                                               e -> Log.e("WW-INFO-E", e.getMessage())
                                       );

                               //updateCategoriesWithTransactions();
                           }, ex -> Log.e("ERR", ex.getMessage()));
                }

                @Override
                public void onCancel() {}
            });
            addTransactionDialogFragment.show(getChildFragmentManager(), AddTransactionDialogFragment.class.getName());
        });

        //recyclerView
        transactionAdapter = new TransactionAdapter(getContext(), toTransactionList());
        recyclerView = view.findViewById(R.id.transaction_recycle);
        recyclerView.setAdapter(transactionAdapter);

        sliderChartFragment = SliderChartFragment.newInstance();
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

    private void updateCategoriesWithTransactions() {
        categoryVM.getCategoriesWithTransactions().observe(this, list -> {
            categoryWithTransactions.clear();
            categoryWithTransactions.addAll(list);
            filterTransactionWithUpdateUI();
        });
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

    public void setUser(User user) {
        this.user = user;
    }
}