package com.chtima.wallettracker.fragments.dialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.adapters.CurrencyRecycleAdapters
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SelectCurrencyDialogFragment : DialogFragment() {

    private var onItemClickListener:((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_select_currency_dialog, container, false)

        val textInput = v.findViewById<TextInputEditText>(R.id.text_input)

        val listView = v.findViewById<RecyclerView>(R.id.list)
        val adapter = CurrencyRecycleAdapters(requireContext(), resources.getStringArray(R.array.currency_codes).toList())

        adapter.setOnListener{
            onItemClickListener?.invoke(it)
            dismiss()
        }

        listView.setAdapter(adapter)

        textInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
            }

        })

        return v
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SelectCurrencyDialogFragment().apply {

            }
    }

    override fun onStart() {
        super.onStart()
        //adjust dialog window attributes on dialog start
        val window = dialog?.window?:return
        val params: WindowManager.LayoutParams = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        window.setAttributes(params);
    }

    fun setOnListener(listener: (String) -> Unit){
        this.onItemClickListener = listener
    }

}