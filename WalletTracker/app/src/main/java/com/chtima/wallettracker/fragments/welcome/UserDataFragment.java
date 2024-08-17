package com.chtima.wallettracker.fragments.welcome;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.CurrencyAdapter;
import com.chtima.wallettracker.models.ErrorEmptyTextWatcher;
import com.chtima.wallettracker.models.User;
import com.chtima.wallettracker.vm.UserViewModel;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class UserDataFragment extends Fragment {

    private String currencyName;
    private UserViewModel userViewModel;

    //UI
    private EditText nameEditText;//TextInputEditText
    private TextInputLayout nameEditTextLayout;
    private EditText surnameEditText;//TextInputEditText
    private TextInputLayout currencyInputLayout;
    private AutoCompleteTextView currencyDropdown;

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

        nameEditText = view.findViewById(R.id.name_input);
        nameEditTextLayout = view.findViewById(R.id.name_input_layout);
        nameEditText.addTextChangedListener(new ErrorEmptyTextWatcher(requireContext(), nameEditTextLayout, R.string.please_enter_first_name));
        surnameEditText = view.findViewById(R.id.lastname_input);

        currencyDropdown = view.findViewById(R.id.currency_dropdown);
        currencyDropdown.setDropDownHeight(getResources().getDimensionPixelSize(R.dimen.dropdown_height));
        currencyDropdown.setDropDownVerticalOffset(getResources().getDimensionPixelSize(R.dimen.m8));

        currencyInputLayout = view.findViewById(R.id.currency_input_layout);

        CurrencyAdapter adapter = new CurrencyAdapter(requireContext(), Arrays.asList(getResources().getStringArray(R.array.arr_currencies)));
        currencyDropdown.setAdapter(adapter);

        currencyDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currencyInputLayout.setError(null);
                currencyName = adapter.getItem(position);
                adapter.setSelectedItemPosition(position);
                adapter.notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.btn_next).setOnClickListener(x -> {
            User user = makeUser();
            if(user == null) return;

            userViewModel.insert(user)
                    .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(aLong -> {
                        user.id = aLong;
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_fragment_container, FirstTopUpFragment.newInstance(), FirstTopUpFragment.class.getName())
                                .commit();
                    }, throwable -> {
                        Toast.makeText(requireContext(), R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                        Log.e("ERR", throwable.toString());
                    });

        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    private User makeUser(){
        boolean err = false;

        String name = nameEditText.getText().toString().trim();
        String surname = surnameEditText.getText().toString().trim();

        if(name.isEmpty()){
            nameEditTextLayout.setError(getString(R.string.please_enter_first_name));
            err = true;
        }

        if(currencyName == null || currencyName.isEmpty()){
            currencyInputLayout.setError(getText(R.string.please_select_a_currency));
            err = true;
        }

        if(err){
            return null;
        }

        return new User(name, surname.isEmpty() ? "" : surname, 0.0, currencyName);

    }

}