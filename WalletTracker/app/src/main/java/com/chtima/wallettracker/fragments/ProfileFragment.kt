package com.chtima.wallettracker.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.chtima.wallettracker.R
import com.chtima.wallettracker.activities.HelpActivity
import com.chtima.wallettracker.fragments.dialogs.TopUpBottomDialogFragment
import com.chtima.wallettracker.utils.CurrencyUtils
import com.chtima.wallettracker.viewModels.UserViewModel
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment() {

    //VM
    private lateinit var userViewModel: UserViewModel

    //UI - textView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userBalance: TextView

    companion object {
        fun newInstance() = ProfileFragment().apply {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_profile, container, false)

        //textView
        userName = v.findViewById(R.id.user_name);
        userEmail = v.findViewById(R.id.user_email);
        userBalance = v.findViewById(R.id.user_balance);

        v.findViewById<MaterialButton>(R.id.btn_help).setOnClickListener{
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        v.findViewById<Button>(R.id.btn_top_up).setOnClickListener{
            val dialogFragment = TopUpBottomDialogFragment.newInstance()
            dialogFragment.show(childFragmentManager, TopUpBottomDialogFragment::class.simpleName)
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class]
        userViewModel.getUser().observe(getViewLifecycleOwner()) { user ->
            // Update the UI with user data
            userName.text = StringBuilder().append(user.lastname).append(" ").append(user.firstname)
            userBalance.text = StringBuilder().append(user.balance)
                .append(CurrencyUtils.getCurrencyChar(user.currency?:"USD", requireContext()))
        };
    }

}