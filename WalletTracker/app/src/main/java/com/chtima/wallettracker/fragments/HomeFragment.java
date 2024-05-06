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
import com.chtima.wallettracker.models.User;

public class HomeFragment extends Fragment {

    private User user;

    private static final String USER_PARCELABLE = "USER_PARCELABLE";


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ((ImageButton)view.findViewById(R.id.btn_add)).setOnClickListener(x -> {
            AddTransactionDialogFragment dialogFragment = AddTransactionDialogFragment.newInstance();
            dialogFragment.show(getChildFragmentManager(), AddTransactionDialogFragment.class.getName());
        });

        user = getArguments().getParcelable(USER_PARCELABLE);

        return view;
    }
}