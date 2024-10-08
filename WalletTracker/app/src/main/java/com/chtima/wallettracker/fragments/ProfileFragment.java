package com.chtima.wallettracker.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.activities.HelpActivity;
import com.chtima.wallettracker.utils.CurrencyUtils;
import com.chtima.wallettracker.vm.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;

    //UI - textView
    private TextView userName;
    private TextView userEmail;
    private TextView userBalance;

    //UI - buttons
    private Button btnTotUp;
    private Button btnTheme;
    private Button btnReport;
    private Button btnCreateBackup;
    private Button btnImportBackup;


    private ProfileFragment() {}

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //textView
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        userBalance = view.findViewById(R.id.user_balance);

        //Buttons (MaterialButton)
        btnTotUp = view.findViewById(R.id.btn_tot_up);//Default Button
        btnTheme = view.findViewById(R.id.btn_theme);
        btnReport = view.findViewById(R.id.btn_generate_report);
        btnCreateBackup = view.findViewById(R.id.btn_create_backup);
        btnImportBackup = view.findViewById(R.id.btn_import_backup);
        view.findViewById(R.id.btn_help).setOnClickListener(x -> startActivity(new Intent(requireContext(), HelpActivity.class)));

        btnTotUp.setOnClickListener(i -> {
            TopUpDialogFragment fragment = TopUpDialogFragment.newInstance();
            fragment.show(this.getChildFragmentManager(), TopUpDialogFragment.class.getName());
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Update the UI with user data
                userName.setText(new StringBuilder().append(user.lastname).append(" ").append(user.firstname));
                userBalance.setText(new StringBuilder().append(user.balance).append(CurrencyUtils.getCurrencyChar(user.currency, requireContext())));
            }
        });
    }
}