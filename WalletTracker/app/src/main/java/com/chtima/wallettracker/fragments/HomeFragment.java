package com.chtima.wallettracker.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.TransactionAdapter;
import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.User;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private User user;
    private AppDatabase database;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<Transaction> transactions = new ArrayList<>();

    private static final String USER_PARCELABLE = "USER_PARCELABLE";


    //ui
    private RecyclerView recyclerView;
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //load_data
        loadTransactions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        transactionAdapter = new TransactionAdapter(getContext(), this.transactions);
        recyclerView = view.findViewById(R.id.transaction_recycle);
        recyclerView.setAdapter(transactionAdapter);

        //user
        user = getArguments().getParcelable(USER_PARCELABLE);

        return view;
    }

    private void loadTransactions(){
        database.transactionDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    transactions.clear();
                    transactions.addAll(list);
                    transactionAdapter.notifyDataSetChanged();
                }, er -> {
                    Log.e("er", er.toString());
                }, () ->{}, compositeDisposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.compositeDisposable.clear();
    }
}