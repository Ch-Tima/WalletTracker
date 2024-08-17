package com.chtima.wallettracker.fragments.welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chtima.wallettracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstTopUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstTopUpFragment extends Fragment {

    private FirstTopUpFragment(){}

    public static FirstTopUpFragment newInstance() {
        return new FirstTopUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_top_up, container, false);


        return view;
    }
}