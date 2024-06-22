package com.chtima.wallettracker.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.components.Swicher;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.ErrorEmptyTextWatcher;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.TransactionType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/** BottomSheetDialogFragment for adding a new transaction. */
public class AddTransactionDialogFragment extends BottomSheetDialogFragment {

    private DialogObserver<Transaction> dialogObserver = null;
    private Category selectedCategory;

    // UI elements
    private EditText title;
    private TextInputLayout titleLayout;
    private EditText note;
    private TextInputLayout noteLayout;
    private EditText sum;
    private TextInputLayout sumLayout;
    private Swicher type;
    private Button dataBtn;
    private Button selectCategoryBtn;
    private DatePickerDialog datePickerDialog;
    private Date dateFromPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_transaction_dialog, container, false);

        //dismiss dialog on cancel
        getDialog().setOnCancelListener(dialog -> dismiss());

        //handle click on add button
        (view.findViewById(R.id.add_button)).setOnClickListener(l -> {
            if (dialogObserver == null) return;

            Transaction t = makeTransaction();

            if(t == null) return;

            dialogObserver.onSuccess(t);
            dismiss();
        });

        //initialize select category button and set click listener
        selectCategoryBtn = view.findViewById(R.id.select_category_btn);
        selectCategoryBtn.setOnClickListener(l -> selectCategory());

        //initialize date picker dialog
        final Calendar c = Calendar.getInstance();
        dateFromPicker = c.getTime();
        datePickerDialog = new DatePickerDialog(requireContext(), (DatePicker viewPicker, int year, int month, int dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateFromPicker = calendar.getTime();
            dataBtn.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        //initialize UI elements
        title = view.findViewById(R.id.title_transaction);
        titleLayout = view.findViewById(R.id.title_transaction_layout);
        title.addTextChangedListener(new ErrorEmptyTextWatcher(requireContext(), titleLayout, R.string.please_enter_title));

        note = view.findViewById(R.id.note_transaction);
        noteLayout = view.findViewById(R.id.note_transaction_layout);

        sum = view.findViewById(R.id.sum_transaction);
        sumLayout = view.findViewById(R.id.sum_transaction_layout);
        sum.addTextChangedListener(new ErrorEmptyTextWatcher(requireContext(), sumLayout, R.string.please_enter_sum));

        type = view.findViewById(R.id.type_transaction);
        dataBtn = view.findViewById(R.id.date_picker_transaction);

        //set date picker dialog listener on date button click
        dataBtn.setOnClickListener(x -> datePickerDialog.show());
        dataBtn.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis()));

        return view;
    }

    public static AddTransactionDialogFragment newInstance() {
        return new AddTransactionDialogFragment();
    }

    /**
     * Set the DialogObserver to subscribe to transaction events.
     * @param observer The observer to set.
     */
    public void setSubscribe(DialogObserver<Transaction> observer){
        if(observer == null) return;
        this.dialogObserver = observer;
    }

    @Override
    public void onStart() {
        super.onStart();
        //adjust dialog window attributes on dialog start
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }
    }

    /**
     * Create a Transaction object from user input.
     * @return The created Transaction object, or null if input is invalid.
     */
    private Transaction makeTransaction(){
        //check availability of the selected category
        if(selectedCategory == null){
            Toast.makeText(getContext(), R.string.please_select_a_category, Toast.LENGTH_SHORT).show();
            selectCategory();
            return null;
        }

        boolean err = false;
        double dSum = 0.0;

        //validate and retrieve title
        if (title.getText().toString().trim().isEmpty()){
            titleLayout.setError(getString(R.string.please_enter_title));
            err = true;
        }

        //validate and retrieve sum
        if (sum.getText().toString().trim().isEmpty()){
            sumLayout.setError(getString(R.string.please_enter_sum));
            err = true;
        }else {
            try {
                dSum = Double.parseDouble(sum.getText().toString());
            }catch (Exception e){
                sumLayout.setError(getString(R.string.wrong_format));
                err = true;
            }
        }

        if(err) {
            //handle error state (e.g., vibrate)
            return null;
        }

        //create and return Transaction object
        return new Transaction(selectedCategory.id,
                dSum,
                title.getText().toString(),
                note.getText().toString(),
                dateFromPicker,
                (type.isChecked() ? TransactionType.INCOME : TransactionType.EXPENSE));
    }

    /** Launch SelectCategoryDialogFragment to allow user to select a category.*/
    private void selectCategory(){
        SelectCategoryDialogFragment dialogFragment = SelectCategoryDialogFragment.newInstance();
        dialogFragment.setSelectCategoryListener(category -> {
            selectedCategory = category;
            selectCategoryBtn.setText(category.title);
        });
        dialogFragment.show(this.getChildFragmentManager(), SelectCategoryDialogFragment.class.getName());
    }

}