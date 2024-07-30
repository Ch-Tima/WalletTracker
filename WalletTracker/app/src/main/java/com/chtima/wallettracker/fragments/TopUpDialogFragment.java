package com.chtima.wallettracker.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.models.User;
import com.chtima.wallettracker.vm.TransactionViewModel;
import com.chtima.wallettracker.vm.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;

/**
 * A dialog fragment for handling user top-up transactions.
 * <p>
 * This fragment allows users to enter an amount to top-up their balance, select a category,
 * and provide a title and note for the transaction.
 * </p>
 *
 * @see TopUpDialogFragment#newInstance()
 * @see BottomSheetDialogFragment
 * @see SelectCategoryWithTextInputsDialogFragment
 */
public class TopUpDialogFragment extends BottomSheetDialogFragment {

    private User user;

    //VM
    private UserViewModel userVM;
    private TransactionViewModel transactionVM;

    //UI
    private TextView textView;//input fields
    private Button btnDot;
    private Button btnDone;

    //Private constructor to prevent instantiation without arguments.
    private TopUpDialogFragment() {}

    /**
     * Factory method to create a new instance of this fragment.
     * @return A new instance of {@link TopUpDialogFragment}.
     */
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

        // Initialize numpad buttons and set click listeners
        View v = null;
        for (int i = 0; i < numpad.getChildCount(); i++) {
            v = numpad.getChildAt(i);
            if (v.getTag() != null && v.getTag().toString().equals(getString(R.string.number_btn))) {
                Button button = (Button) v;
                button.setOnClickListener(x -> putNumber(button.getText().toString()));
            }
        }

        // Initialize input fields
        textView = view.findViewById(R.id.number_text);//input fields
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {// Enable or disable the 'Done' button based on input
                btnDone.setEnabled(!s.toString().isEmpty() && s.charAt(s.length() - 1) != '.' && toDouble(s.toString()) > 0.009);
            }
        });

        // Initialize 'Dot' button for adding decimal point
        btnDot = view.findViewById(R.id.btn_dot);
        btnDot.setOnClickListener(x -> {
            if(!textView.getText().toString().contains(".") && !textView.getText().toString().isEmpty())
                textView.setText(textView.getText() + ".");
        });

        // Initialize 'Clear' button for removing the last character
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

        // Initialize 'Done' button for finalizing the transaction
        btnDone = view.findViewById(R.id.btn_done);
        btnDone.setEnabled(false);
        btnDone.setOnClickListener(x -> {// Show category selection dialog
            SelectCategoryWithTextInputsDialogFragment fragment = SelectCategoryWithTextInputsDialogFragment.newInstance();
            fragment.show(this.getChildFragmentManager(), fragment.getClass().getName());
            fragment.setListener(new SelectCategoryWithTextInputsDialogFragment.OnSelectCategoryWithTextInputsListener() {
                @Override
                public void onDone(Category category, String title, String note) {
                    Transaction transaction = new Transaction(
                            category.id, user.id,
                            toDouble(textView.getText().toString()),
                            title, note,
                            Calendar.getInstance().getTime(),
                            TransactionType.INCOME);

                    // Insert transaction and update user balance
                    TransactionViewModel transactionVM = new ViewModelProvider(TopUpDialogFragment.this).get(TransactionViewModel.class);
                    transactionVM.insert(transaction)
                            .flatMapCompletable(aLong -> {
                                user.balance = user.balance + transaction.sum;
                                return userVM.update(TopUpDialogFragment.this.user);
                            })
                            .subscribe();

                    dismiss(); // Close
                }
            });

        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userVM.getUser().observe(this, user -> this.user = user);
    }

    /**
     * Appends the clicked number to the input field, handling formatting and decimal points.
     * @param i The number to append.
     */
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

    /**
     * Formats the input string to include thousand separators.
     * @param s The input string.
     * @return A formatted string with thousand separators.
     */
    private String toFormat(String s){
        if(s.isBlank()) return "";

        StringBuilder text = new StringBuilder(s);
        DecimalFormat f = new DecimalFormat("#,###");

        int indexForDelete;
        while ((indexForDelete = text.indexOf(",")) != -1)
            text.deleteCharAt(indexForDelete);

        return f.format(Integer.parseInt(text.toString()));
    }

    /**
     * Converts the input string to a double, removing thousand separators.
     * @param s The input string.
     * @return The double representation of the input string.
     */
    private double toDouble(String s){
        if(s.isBlank()) return 0.0;

        StringBuilder text = new StringBuilder(s);

        int indexForDelete;
        while ((indexForDelete = text.indexOf(",")) != -1)
            text.deleteCharAt(indexForDelete);

        return Double.parseDouble(text.toString());
    }

}