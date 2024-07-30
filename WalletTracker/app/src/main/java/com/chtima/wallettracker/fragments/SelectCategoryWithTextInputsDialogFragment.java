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
    private RecyclerView recyclerView;

    private SelectCategoryWithTextInputsDialogFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SelectCategoryWithTextInputsDialogFragment.
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

    public void setListener(OnSelectCategoryWithTextInputsListener listener) {
        this.listener = listener;
    }

    public interface OnSelectCategoryWithTextInputsListener{
        void onDone(Category category, String title, String note);
    }
}