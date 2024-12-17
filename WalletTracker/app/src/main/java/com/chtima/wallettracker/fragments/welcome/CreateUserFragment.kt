package com.chtima.wallettracker.fragments.welcome

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider;
import autodispose2.AutoDispose
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.chtima.wallettracker.R
import com.chtima.wallettracker.adapters.CurrencyAdapter
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.fragments.dialogs.SelectCurrencyDialogFragment
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.SharedPreferencesKeys
import com.chtima.wallettracker.models.User
import com.chtima.wallettracker.viewModels.CategoryViewModel
import com.chtima.wallettracker.viewModels.UserViewModel
import com.chtima.wallettracker.watchers.ErrorEmptyTextWatcher
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.core.Completable

class CreateUserFragment : Fragment() {

    private var currencyName : String = "";
    private lateinit var userVM : UserViewModel;
    private lateinit var categoryVM : CategoryViewModel;

    //UI
    private lateinit var nameEditText : TextInputEditText
    private lateinit var nameEditTextLayout : TextInputLayout
    private lateinit var surnameEditText : TextInputEditText
    private lateinit var currencyInputLayout: TextInputLayout
    private lateinit var selectCurrencyBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_create_user, container, false);

        nameEditText = v.findViewById(R.id.name_input)
        nameEditTextLayout = v.findViewById(R.id.name_input_layout)
        nameEditText.addTextChangedListener(ErrorEmptyTextWatcher(requireContext(), nameEditTextLayout, R.string.please_enter_first_name))
        surnameEditText = v.findViewById(R.id.lastname_input)

        selectCurrencyBtn = v.findViewById(R.id.select_currency_btn)
        selectCurrencyBtn.setOnClickListener{
                val d = SelectCurrencyDialogFragment.newInstance()
                d.show(parentFragmentManager, SelectCurrencyDialogFragment::class.java.name)
                d.setOnListener {
                    //TODO
                }
            }

        // Handle the "Next" button click event
        v.findViewById<Button>(R.id.btn_next).setOnClickListener{
            val user = makeUser() // Create a User object based on input data
                    ?: return@setOnClickListener // If user creation failed, do nothing

            userVM.insert(user)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(requireActivity())))
                .subscribe(
                    {id ->
                        user.id = id;// Set the user ID after insertion
                        SharedPreferencesKeys.getSharedPreferences(requireContext()).edit().putLong(SharedPreferencesKeys.SELECTED_USER_ID, id).apply()

                        categoryVM.insertAll(*AppDatabase.defaultCategories(requireContext()))
                            .to(AutoDispose.autoDisposable<Completable>(AndroidLifecycleScopeProvider.from(requireActivity())))
                            .subscribe({}, {
                                Toast.makeText(requireContext(), R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                                Log.e("ERR", it.toString());
                            })

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container, FirstTopUpFragment.newInstance(), FirstTopUpFragment::class.simpleName)
                            .commit();
                    },
                    // Handle errors during insertion
                    {throwable  ->
                        Toast.makeText(requireContext(), R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                        Log.e("ERR", throwable.toString());
                    }
                )

            userVM.insert(user);
        }

        if((requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES){
            v.findViewById<TextView>(R.id.tv_greeting_message)?.setTextColor(requireContext().getColor(R.color.silver_sand))
        }

        return v;
    }

    /**
     * Creates a User object based on the input fields. If any required field is empty,
     * sets an error message and returns null.
     *
     * @return A new User object or null if validation fails.
     */
    private fun makeUser(): User? {
        var err = false// Flag to track validation errors
        val name = nameEditText.getText().toString().trim()
        val surname = surnameEditText.getText().toString().trim()

        if(name.isEmpty()){// Validate name input
            nameEditTextLayout.setError(getString(R.string.please_enter_first_name));
            err = true;
        }

        if(currencyName.isEmpty()){// Validate currency selection
            currencyInputLayout.setError(getText(R.string.please_select_a_currency));
            err = true;
        }

        if(err){// If any validation error occurred, return null
            return null;
        }

        // Return a new User object with the input values
        return User(name, surname.ifEmpty { "" }, 0.0, currencyName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize the ViewModel after the view is created
        userVM = ViewModelProvider(requireActivity())[UserViewModel::class]
        categoryVM = ViewModelProvider(requireActivity())[CategoryViewModel::class]

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateUserFragment().apply {

            }
    }
}