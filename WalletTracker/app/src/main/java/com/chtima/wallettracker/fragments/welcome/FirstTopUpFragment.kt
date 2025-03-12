package com.chtima.wallettracker.fragments.welcome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.chtima.wallettracker.MainActivity
import com.chtima.wallettracker.R

class FirstTopUpFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_first_top_up, container, false);

        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()

            //TODO
//        requireActivity().onBackPressedDispatcher.addCallback {
//
//        }

        return v
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FirstTopUpFragment().apply {}
    }
}