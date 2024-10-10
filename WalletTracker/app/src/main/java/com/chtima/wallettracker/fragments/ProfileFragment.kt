package com.chtima.wallettracker.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.chtima.wallettracker.R
import com.chtima.wallettracker.activities.HelpActivity
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.fragments.dialogs.TopUpBottomDialogFragment
import com.chtima.wallettracker.utils.CurrencyUtils
import com.chtima.wallettracker.viewModels.UserViewModel
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment() {

    private lateinit var createFileLauncher: ActivityResultLauncher<Intent>

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

        createFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result.data?.data?.let {
                AppDatabase.backupDatabase(requireContext(), it)
            }
            Log.d("RESSSS", result.resultCode.toString())
        }

        v.findViewById<MaterialButton>(R.id.btn_create_backup).setOnClickListener{
            createFile(Uri.EMPTY)
        }

        v.findViewById<MaterialButton>(R.id.btn_help).setOnClickListener{
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        v.findViewById<Button>(R.id.btn_top_up).setOnClickListener{
            val dialogFragment = TopUpBottomDialogFragment.newInstance()
            dialogFragment.show(childFragmentManager, TopUpBottomDialogFragment::class.simpleName)
        }

        return v
    }

    private fun createFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "WTBackup.db")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        createFileLauncher.launch(intent)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this)[UserViewModel::class]
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            // Update the UI with user data
            userName.text = StringBuilder().append(user.lastname).append(" ").append(user.firstname)
            userBalance.text = StringBuilder().append(user.balance)
                .append(CurrencyUtils.getCurrencyChar(user.currency?:"USD", requireContext()))
        };
    }

}