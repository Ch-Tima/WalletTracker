package com.chtima.wallettracker.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.chtima.wallettracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopUpDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopUpDialogFragment extends BottomSheetDialogFragment {

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

        for (int i = 0; i < numpad.getChildCount(); i++){
            View v = numpad.getChildAt(i);
            if(v.getTag() != null && v.getTag().toString().equals(getString(R.string.number_btn))){
                Button button = (Button)v;
                int number = Integer.parseInt(button.getText().toString());
                button.setOnClickListener(x -> Toast.makeText(requireContext(), String.valueOf(number), Toast.LENGTH_SHORT).show());
            }
        }

        return view;
    }


}