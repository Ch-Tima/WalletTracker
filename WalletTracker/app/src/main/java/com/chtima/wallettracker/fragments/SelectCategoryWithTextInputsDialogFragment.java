package com.chtima.wallettracker.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.ErrorEmptyTextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
 * @see SelectCategoryWithTextInputsDialogFragment#newInstance
 * @see SelectCategoryDialogFragment
 * @see TextInputEditText
 * @see DialogFragment
 * @author <a href="https://github.com/Ch-Tima/">chizh</a>
 */
public class SelectCategoryWithTextInputsDialogFragment extends DialogFragment {

    private Category category;
    private OnSelectCategoryWithTextInputsListener listener;

    //UI
    private EditText title;
    private TextInputLayout titleLayout;
    private EditText note;
    private TextInputLayout noteLayout;

    // Private constructor to prevent instantiation from outside
    private SelectCategoryWithTextInputsDialogFragment() {}

    /**
     * Factory method to create a new instance of this fragment.
     * <p>
     * This method creates a new instance of {@link SelectCategoryWithTextInputsDialogFragment}
     * with no arguments.
     * </p>
     * @return A new instance of {@link SelectCategoryWithTextInputsDialogFragment}.
     */
    public static SelectCategoryWithTextInputsDialogFragment newInstance() {
        SelectCategoryWithTextInputsDialogFragment fragment = new SelectCategoryWithTextInputsDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_category_with_text_inputs_dialog, container, false);

        //initialize UI elements
        title = view.findViewById(R.id.title_transaction);//*required
        title.setText(requireContext().getString(R.string.replenishment_message));//Replenishment on 12.12.2000 for the amount of 200
        titleLayout = view.findViewById(R.id.title_transaction_layout);
        title.addTextChangedListener(new ErrorEmptyTextWatcher(requireContext(), titleLayout, R.string.please_enter_title));

        note = view.findViewById(R.id.note_transaction);//*not required
        noteLayout = view.findViewById(R.id.note_transaction_layout);

        SelectCategoryDialogFragment fragment = SelectCategoryDialogFragment.newInstance(Category.CategoryType.INCOME, true);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.select_category_fragment, fragment)
                .commit();

        fragment.setSelectCategoryListener(category -> this.category = category);

        view.findViewById(R.id.btn_done).setOnClickListener(y -> {
            //validate and retrieve title
            if (title.getText().toString().trim().isEmpty()){
                titleLayout.setError(getString(R.string.please_enter_title));
                return;
            }

            //check the selected category
            if(category == null){
                Toast.makeText(requireContext(), R.string.please_select_a_category, Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null)
                listener.onDone(category, title.getText().toString(), note.getText().toString());
            dismiss();//close
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //adjust dialog window attributes on dialog start
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setBackgroundDrawableResource(R.drawable.round_ashen_8dp_m16);
            window.setAttributes(params);
        }
    }

    /**
     * Set the listener to be notified when the user completes their input.
     * @param listener An instance of {@link OnSelectCategoryWithTextInputsListener} to handle the callback.
     */
    public void setListener(OnSelectCategoryWithTextInputsListener listener) {
        this.listener = listener;
    }

    /**
     * Listener interface to be implemented by the parent activity or fragment.
     * <p>
     * This interface defines a callback method that is invoked when the user
     * completes their selection and input within the {@link SelectCategoryWithTextInputsDialogFragment}.
     * </p>
     */
    public interface OnSelectCategoryWithTextInputsListener{
        /**
         * Called when the user has completed selecting a category and entering title and note.
         *
         * @param category The selected {@link Category}.
         * @param title The title entered by the user.
         * @param note The note entered by the user.
         */
        void onDone(Category category, String title, String note);
    }
}