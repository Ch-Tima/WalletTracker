package com.chtima.wallettracker.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.User;
import com.chtima.wallettracker.vm.TransactionViewModel;
import com.chtima.wallettracker.vm.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopUpDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopUpDialogFragment extends BottomSheetDialogFragment {

    private User user;

    //VM
    private UserViewModel userVM;
    private TransactionViewModel transactionVM;

    //UI
    private TextView textView;//input fields


    private TopUpDialogFragment() {}

    public static TopUpDialogFragment newInstance() {
        TopUpDialogFragment fragment = new TopUpDialogFragment();
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
        View view = inflater.inflate(R.layout.fragment_top_up_dialog, container, false);

        GridLayout numpad = view.findViewById(R.id.numpad_grid);

        //numpad btns
        View v = null;
        for (int i = 0; i < numpad.getChildCount(); i++) {
            v = numpad.getChildAt(i);
            if (v.getTag() != null && v.getTag().toString().equals(getString(R.string.number_btn))) {
                Button button = (Button) v;
                button.setOnClickListener(x -> putNumber(button.getText().toString()));
            }
        }

        textView = view.findViewById(R.id.number_text);//input fields

        view.findViewById(R.id.btn_dot).setOnClickListener(x -> {
            if(!textView.getText().toString().contains(".") && !textView.getText().toString().isEmpty())
                textView.setText(textView.getText() + ".");
        });

        view.findViewById(R.id.btn_clear).setOnClickListener(x -> {
            StringBuilder text = new StringBuilder(textView.getText().toString());
            if (text.toString().isEmpty()) return;

            text.deleteCharAt(text.length()-1);

            if(text.indexOf(".") != -1){
                textView.setText(text);
                return;
            }

            textView.setText(toFormat(text.toString()));
        });

        view.findViewById(R.id.btn_done).setOnClickListener(x -> {

            SelectCategoryDialogFragment categoryDialog = SelectCategoryDialogFragment.newInstance(Category.CategoryType.INCOME);
            categoryDialog.show(this.getChildFragmentManager(), SelectCategoryDialogFragment.class.getName());
            categoryDialog.setSelectCategoryListener(category -> {

            });

            user.balance = user.balance + toDouble(textView.getText().toString());
            //update user and add Transaction

        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userVM = new ViewModelProvider(this).get(UserViewModel.class);
        userVM.getUser().observe(this, user -> this.user = user);
    }

    private void putNumber(String i){
        StringBuilder text = new StringBuilder(textView.getText().toString());

        if(text.length() < 1 && i.equals("0")){
            text.append("0.");
            textView.setText(text);
            return;
        }

        int indexDot = text.indexOf(".");
        if(indexDot != -1 && text.length() < 14 && text.length() - indexDot < 3){
            textView.setText(text.append(i).toString());
            return;
        }

        if(text.length() < 12 && indexDot == -1){
            textView.setText(toFormat(text.append(i).toString()));
        }

    }

    private String toFormat(String s){
        if(s.isBlank()) return "";

        StringBuilder text = new StringBuilder(s);
        DecimalFormat f = new DecimalFormat("#,###");

        int indexForDelete;
        while ((indexForDelete = text.indexOf(",")) != -1)
            text.deleteCharAt(indexForDelete);

        return f.format(Integer.parseInt(text.toString()));
    }

    private double toDouble(String s){
        if(s.isBlank()) return 0.0;

        StringBuilder text = new StringBuilder(s);

        int indexForDelete;
        while ((indexForDelete = text.indexOf(",")) != -1)
            text.deleteCharAt(indexForDelete);

        return Double.parseDouble(text.toString());
    }

}