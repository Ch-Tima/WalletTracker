package com.chtima.wallettracker.fragments.welcome;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.chtima.wallettracker.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

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

        MaterialAutoCompleteTextView currencyDropdown = view.findViewById(R.id.currency_dropdown);
        currencyDropdown.setDropDownHeight(getResources().getDimensionPixelSize(R.dimen.dropdown_height));
        currencyDropdown.setDropDownVerticalOffset(getResources().getDimensionPixelSize(R.dimen.m8));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.arr_currencies, android.R.layout.simple_dropdown_item_1line);
        currencyDropdown.setAdapter(adapter);


        return view;
    }
}