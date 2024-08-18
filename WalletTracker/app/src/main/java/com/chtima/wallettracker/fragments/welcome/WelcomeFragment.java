package com.chtima.wallettracker.fragments.welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chtima.wallettracker.R;


/**
 * <p>Welcome window with login selection.</p>
 * Use the {@link FirstTopUpFragment#newInstance} factory method to create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {


    private WelcomeFragment() {}

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        view.findViewById(R.id.btn_get_started).setOnClickListener(x -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, UserDataFragment.newInstance(), UserDataFragment.class.getName())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

}