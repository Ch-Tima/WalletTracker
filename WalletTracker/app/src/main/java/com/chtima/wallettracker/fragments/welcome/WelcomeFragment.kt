package com.chtima.wallettracker.fragments.welcome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import autodispose2.AutoDispose
import autodispose2.androidx.lifecycle.autoDispose
import autodispose2.autoDispose
import com.chtima.wallettracker.MainActivity
import com.chtima.wallettracker.R
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.db.repositories.UserRepository
import com.chtima.wallettracker.models.SharedPreferencesKeys
import com.chtima.wallettracker.viewModels.UserViewModel
import com.google.android.material.button.MaterialButton

class WelcomeFragment : Fragment() {

    private lateinit var openFileLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_welcome, container, false);

        openFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result.data?.data?.let {
                val res = AppDatabase.getInstance(requireContext(), it)

                if(res){
                    val ur = UserRepository(requireActivity().application)
                    ur.getUsers()
                        .autoDispose(this)
                        .subscribe { list ->
                            if (list.isEmpty()) {
                                deleteDbAndAlertError()
                            } else {
                                SharedPreferencesKeys.getSharedPreferences(requireContext()).edit()
                                    .putLong(SharedPreferencesKeys.SELECTED_USER_ID, list[0].id).apply()
                                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
                                requireActivity().finish()
                            }
                        }
                }else{
                    deleteDbAndAlertError()
                }

            }
            Log.d("RESSSS", result.resultCode.toString())
        }

        view.findViewById<MaterialButton>(R.id.btn_get_started).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, CreateUserFragment.newInstance(), CreateUserFragment::class.simpleName)
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<MaterialButton>(R.id.btn_get_started_from_backup).setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
            }
            openFileLauncher.launch(intent)
        }

        return view
    }

    private fun deleteDbAndAlertError(){
        AppDatabase.delete(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error)
            .setMessage(R.string.unable_to_recover_data_due_to_incorrect_data)
            .setPositiveButton(R.string.ok) { x, _ -> x.dismiss() }
            .create().show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WelcomeFragment().apply {

            }
    }
}