package com.chtima.wallettracker.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chtima.wallettracker.R
import com.chtima.wallettracker.fragments.simples.SelectionCategoryFragment
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.watchers.ErrorEmptyTextWatcher
import com.google.android.material.textfield.TextInputLayout


/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link SelectCategoryWithTextInputsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p>
 * This fragment displays:
 * <ul>
 *     <li>{@link TextInputEditText} fields for entering a title and a note.</li>
 *     <li>{@link SelectCategoryDialogFragment} for selecting a category.</li>
 * </ul>
 * </p>
 *
 * @see CategoryAndDetailsDialogFragment#newInstance
 * @see SelectCategoryDialogFragment
 * @see TextInputEditText
 * @see DialogFragment
 * @author <a href="https://github.com/Ch-Tima/">chizh</a>
 */
class CategoryAndDetailsDialogFragment private constructor(): DialogFragment() {

    private var category: Category? = null
    private var listener: OnCategoryAndDetailsDialogListener? = null

    //UI
    private lateinit var title: EditText
    private lateinit var titleLayout: TextInputLayout
    private lateinit var note: EditText
    private lateinit var noteLayout: TextInputLayout


    companion object {
        /**
         * Factory method to create a new instance of this fragment.
         * <p>
         * This method creates a new instance of {@link SelectCategoryWithTextInputsDialogFragment}
         * with no arguments.
         * </p>
         * @return A new instance of {@link SelectCategoryWithTextInputsDialogFragment}.
         */
        fun newInstance(): CategoryAndDetailsDialogFragment {
            return CategoryAndDetailsDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_category_and_details_dialog, container, false);

        //initialize UI elements
        title = v.findViewById(R.id.title_transaction)//*required
        title.setText(requireContext().getString(R.string.replenishment_message))//Replenishment on 12.12.2000 for the amount of 200
        titleLayout = v.findViewById(R.id.title_transaction_layout)
        title.addTextChangedListener(ErrorEmptyTextWatcher(requireContext(), titleLayout, R.string.please_enter_title))

        note = v.findViewById(R.id.note_transaction)//*not required
        noteLayout = v.findViewById(R.id.note_transaction_layout)

        val fragment = SelectionCategoryFragment.newInstance(Category.CategoryType.INCOME, true)
        getChildFragmentManager()
            .beginTransaction()
            .replace(R.id.select_category_fragment, fragment)
            .commit()

        fragment.setSelectCategoryListener(object : DialogObserver<Category> {
            override fun onSuccess(result: Category) {
                category = result
            }
        })

        v.findViewById<Button>(R.id.btn_done).setOnClickListener{
            //validate and retrieve title
            if (title.getText().toString().trim().isEmpty()){
                titleLayout.setError(getString(R.string.please_enter_title))
                return@setOnClickListener
            }

            //check the selected category
            if(category == null){
                Toast.makeText(requireContext(), R.string.please_select_a_category, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (listener != null)
                listener!!.onDone(category!!, title.getText().toString(), note.getText().toString())

            dismiss()//close
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null && dialog?.window != null) {
            val window:Window = dialog!!.window!!
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.setBackgroundDrawableResource(R.drawable.rounded_8dp_m16_ashen)
            window.attributes = params
        }
    }

    /**
     * Set the listener to be notified when the user completes their input.
     * @param listener An instance of [OnCategoryAndDetailsDialogListener] to handle the callback.
     */
    fun setListener(listener: OnCategoryAndDetailsDialogListener) {
        this.listener = listener
    }

    /**
     * Listener interface to be implemented by the parent activity or fragment.
     *
     *
     * This interface defines a callback method that is invoked when the user
     * completes their selection and input within the [SelectCategoryWithTextInputsDialogFragment].
     *
     */
    interface OnCategoryAndDetailsDialogListener {
        /**
         * Called when the user has completed selecting a category and entering title and note.
         *
         * @param category The selected [Category].
         * @param title The title entered by the user.
         * @param note The note entered by the user.
         */
        fun onDone(category: Category, title: String, note: String)
    }

}