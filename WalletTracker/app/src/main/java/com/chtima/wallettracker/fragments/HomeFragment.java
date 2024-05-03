package com.chtima.wallettracker.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.chtima.wallettracker.R;

public class HomeFragment extends Fragment {


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ((ImageButton)view.findViewById(R.id.btn_add)).setOnClickListener(x -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setView(R.layout.fragment_add_transaction_dialog);
//            builder.set
//            builder.create().show();

            AddTransactionDialogFragment dialogFragment = AddTransactionDialogFragment.newInstance();
            dialogFragment.show(getChildFragmentManager(), AddTransactionDialogFragment.class.getName());
        });

        return view;
    }
}