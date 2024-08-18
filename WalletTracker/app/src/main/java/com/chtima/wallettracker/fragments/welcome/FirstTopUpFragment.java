package com.chtima.wallettracker.fragments.welcome;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chtima.wallettracker.MainActivity;
import com.chtima.wallettracker.R;
import com.chtima.wallettracker.fragments.TopUpDialogFragment;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.Transaction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstTopUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstTopUpFragment extends Fragment {

    private TopUpDialogFragment topUpFragment;

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

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }
        });

        topUpFragment = TopUpDialogFragment.newInstance();
        topUpFragment.setVisibilityTitle(View.GONE);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.numpad_fragment, topUpFragment, TopUpDialogFragment.class.getName())
                .commit();

        topUpFragment.setOnDoneListener(new DialogObserver<Transaction>() {
            @Override
            public void onSuccess(Transaction obj) {
                requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(Throwable e) {
                DialogObserver.super.onError(e);
            }
        });

        return view;
    }



}