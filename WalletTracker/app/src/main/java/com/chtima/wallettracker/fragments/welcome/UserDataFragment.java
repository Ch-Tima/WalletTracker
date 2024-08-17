package com.chtima.wallettracker.fragments.welcome;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.CurrencyAdapter;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.Arrays;
import java.util.List;

public class UserDataFragment extends Fragment {

    private UserDataFragment() {}

    public static UserDataFragment newInstance() {
        return new UserDataFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_data, container, false);

        AutoCompleteTextView currencyDropdown = view.findViewById(R.id.currency_dropdown);
        currencyDropdown.setDropDownHeight(getResources().getDimensionPixelSize(R.dimen.dropdown_height));
        currencyDropdown.setDropDownVerticalOffset(getResources().getDimensionPixelSize(R.dimen.m8));

        CurrencyAdapter adapter = new CurrencyAdapter(requireContext(), Arrays.asList(getResources().getStringArray(R.array.arr_currencies)));
        currencyDropdown.setAdapter(adapter);

        currencyDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currencyName = adapter.getItem(position);
                adapter.setSelectedItemPosition(position);
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}