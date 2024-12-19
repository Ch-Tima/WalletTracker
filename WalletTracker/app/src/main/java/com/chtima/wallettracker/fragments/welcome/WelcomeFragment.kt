package com.chtima.wallettracker.fragments.welcome

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import autodispose2.androidx.lifecycle.autoDispose
import com.chtima.wallettracker.MainActivity
import com.chtima.wallettracker.R
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.db.repositories.UserRepository
import com.chtima.wallettracker.models.AppConstants
import com.chtima.wallettracker.models.SharedPreferencesKeys
import com.google.android.material.button.MaterialButton
import com.google.rpc.Code

class WelcomeFragment : Fragment() {

    // ActivityResultLauncher to handle the result of the file chooser activity
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

        // Initialize file launcher for handling the backup restoration process
        openFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result.data?.data?.let {
                // Attempt to restore the database from the chosen backup file
                val res = AppDatabase.restoreDatabase(requireContext(), it)
                if(res.code == Code.OK){
                    // If restoration was successful, fetch users from the database
                    val ur = UserRepository(requireActivity().application)
                    ur.getUsers()
                        .autoDispose(this)
                        .subscribe({ list ->//!In the future, the ability to select a user if there is more than one
                            if (list.isEmpty()) {// If the restored database is empty, show an error dialog
                                deleteDbAndAlertError()
                            } else {
                                // Save the first user's ID to shared preferences and proceed to the main activity
                                SharedPreferencesKeys.getSharedPreferences(requireContext()).edit()
                                    .putLong(AppConstants.SELECTED_USER_ID, list[0].id).apply()
                                // Start the main activity and finish the current one
                                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
                                requireActivity().finish()
                            }
                        }, { ex ->// If any error occurs while fetching users, show an error dialog
                            deleteDbAndAlertError(R.string.this_backup_is_corrupted_or_isnt_part_of_this_app)
                        })
                }else{// If restoration failed, show a general error dialog
                    deleteDbAndAlertError()
                }
            }
            Log.d("RESSSS", result.resultCode.toString())
        }

        // Set a click listener for the "Get Started" button to transition to the CreateUserFragment
        view.findViewById<MaterialButton>(R.id.btn_get_started).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, CreateUserFragment.newInstance(), CreateUserFragment::class.simpleName)
                .addToBackStack(null)
                .commit()
        }

        // Set a click listener for the "Get Started From Backup" button to open a file chooser for backup restoration
        view.findViewById<MaterialButton>(R.id.btn_get_started_from_backup).setOnClickListener{
            // Intent to open the document picker to select a backup file
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
            }
            openFileLauncher.launch(intent)// Launch the file picker
        }

        if((requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES){
            view.findViewById<TextView>(R.id.tv_greeting_message)?.setTextColor(requireContext().getColor(R.color.silver_sand))
        }

        return view
    }

    /**
     * Deletes the current database and shows an error dialog to inform the user of an issue.
     *
     * @param rId The resource ID of the error message to be displayed in the dialog (default: unable_to_recover_data_due_to_incorrect_data).
     */
    private fun deleteDbAndAlertError(rId:Int = R.string.unable_to_recover_data_due_to_incorrect_data){
        AppDatabase.delete(requireContext())// Delete the existing database
        AlertDialog.Builder(requireContext()) // Show an alert dialog with the error message
            .setTitle(R.string.error)
            .setMessage(rId)
            .setPositiveButton(R.string.ok) { x, _ -> x.dismiss() }
            .create().show()
    }

    /**
     * Companion object to create a new instance of WelcomeFragment.
     * This method can be used for fragment creation with any necessary initialization parameters in the future.
     */
    companion object {
        @JvmStatic
        fun newInstance() =
            WelcomeFragment().apply {
                // No initialization parameters at the moment
            }
    }
}