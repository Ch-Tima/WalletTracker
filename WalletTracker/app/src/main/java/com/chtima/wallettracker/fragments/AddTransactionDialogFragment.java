package com.chtima.wallettracker.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.chtima.wallettracker.MainActivity;
import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.CategorySpinnerAdapter;
import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.models.User;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddTransactionDialogFragment extends DialogFragment {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private final List<Category> categories = new ArrayList<>();
    private DialogObserver<Transaction> dialogObserver = null;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private Category selectedCategory;
    private AppDatabase database;

    //UI
    private EditText title;
    private EditText note;
    private EditText sum;
    private Switch type;
    private Spinner spinnerCategories;
    private DatePickerDialog datePickerDialog;
    private Date dateFromPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_transaction_dialog, container, false);

        database = AppDatabase.getInstance(this.getContext());
        //categories.add(new Category("mTitle", 12));
        loadCategories();

        (view.findViewById(R.id.cancel_button)).setOnClickListener(l -> {
            if (dialogObserver != null)
                dialogObserver.onCancel();
        });
        (view.findViewById(R.id.add_button)).setOnClickListener(l -> {
            if (dialogObserver == null)
                return;

            Transaction t = makeTransaction();
            if(t == null)
                return;

            dialogObserver.onSuccess(t);
        });

        //datePicker
        final Calendar c = Calendar.getInstance();
        dateFromPicker = c.getTime();
        datePickerDialog = new DatePickerDialog(requireContext(), (DatePicker viewPicker, int year, int month, int dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateFromPicker = calendar.getTime();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        //holder
        title = view.findViewById(R.id.title_transaction);
        note = view.findViewById(R.id.note_transaction);
        sum = view.findViewById(R.id.sum_transaction);
        type = view.findViewById(R.id.type_transaction);
        spinnerCategories = view.findViewById(R.id.category_transaction);
        categorySpinnerAdapter = new CategorySpinnerAdapter(this.getContext(), categories);
        spinnerCategories.setAdapter(categorySpinnerAdapter);
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (Category) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        view.findViewById(R.id.date_picker_transaction).setOnClickListener(x -> datePickerDialog.show());

        return view;
    }

    public static AddTransactionDialogFragment newInstance() {
        return new AddTransactionDialogFragment();
    }

    public void setSubscribe(DialogObserver<Transaction> observer){
        if(observer == null) return;
        this.dialogObserver = observer;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }
    }

    private Transaction makeTransaction(){
        if(selectedCategory == null){
            Toast.makeText(getContext(), R.string.please_select_a_category, Toast.LENGTH_SHORT).show();
            return null;
        }

        return new Transaction(selectedCategory.id,
                Double.parseDouble(sum.getText().toString()),
                title.getText().toString(),
                note.getText().toString(),
                dateFromPicker,
                (type.isChecked() ? TransactionType.EXPENSE : TransactionType.INCOME));
    }



    private void loadCategories(){

        Disposable subscribed = database.categoryDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryList ->
                {
                    this.categories.addAll(categoryList);
                    this.categorySpinnerAdapter.notifyDataSetChanged();


                }, e -> {
                    System.out.println("RoomWithRx: " +e.getMessage());
                });

        disposable.add(subscribed);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}