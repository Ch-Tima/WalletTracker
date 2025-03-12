package com.chtima.wallettracker.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.chtima.wallettracker.R
import com.chtima.wallettracker.activities.HelpActivity
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.fragments.dialogs.TopUpBottomDialogFragment
import com.chtima.wallettracker.models.AppConstants
import com.chtima.wallettracker.models.SharedPreferencesKeys
import com.chtima.wallettracker.utils.CurrencyUtils
import com.chtima.wallettracker.viewModels.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.rpc.Code

class ProfileFragment : Fragment() {

    private lateinit var createFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var spk: SharedPreferences

    //VM
    private lateinit var userViewModel: UserViewModel

    //UI - textView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userBalance: TextView
    private lateinit var themeBtn: MaterialButton

    companion object {
        fun newInstance() = ProfileFragment().apply {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_profile, container, false)

        spk = SharedPreferencesKeys.getSharedPreferences(requireContext())
        var themeKey = spk.getInt(AppConstants.SELECTED_THEME_MODE, AppConstants.NOT_FOUND)

        //textView
        userName = v.findViewById(R.id.user_name)
        userEmail = v.findViewById(R.id.user_email)
        userBalance = v.findViewById(R.id.user_balance)

        createFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result.data?.data?.let {
                val backupResult = AppDatabase.backupDatabase(requireContext(), it)
                if(backupResult.code != Code.OK){
                    //SEND ERROR TO FIREBASE
                }
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

        themeBtn = v.findViewById(R.id.btn_theme)
        themeBtn.setOnClickListener{
            themeKey = when (themeKey) {
                AppConstants.SYSTEM_MODE_KEY -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    AppConstants.LIGHT_MODE_KEY
                }
                AppConstants.LIGHT_MODE_KEY -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    AppConstants.DARK_MODE_KEY
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    AppConstants.SYSTEM_MODE_KEY
                }
            }
            spk.edit().putInt(AppConstants.SELECTED_THEME_MODE, themeKey).apply()
            setThemeBtnText(themeKey)
        }

        setThemeBtnText(themeKey)

        return v
    }

    private fun setThemeBtnText(themeKey:Int){
        val themeName = when (themeKey) {
            AppConstants.LIGHT_MODE_KEY -> "light"
            AppConstants.DARK_MODE_KEY -> "dark"
            else -> "system"
        }
        themeBtn.text = getString(R.string.theme).plus(": $themeName")
    }

    private fun createFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "WTBackup.wt2db")

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