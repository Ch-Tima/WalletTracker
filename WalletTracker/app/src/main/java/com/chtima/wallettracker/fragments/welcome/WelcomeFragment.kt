package com.chtima.wallettracker.fragments.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chtima.wallettracker.R
import com.google.android.material.button.MaterialButton

class WelcomeFragment : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_welcome, container, false);

        view.findViewById<MaterialButton>(R.id.btn_get_started).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, CreateUserFragment.newInstance(), CreateUserFragment::class.simpleName)
                .addToBackStack(null)
                .commit();
        }

        return view;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WelcomeFragment().apply {

            }
    }
}