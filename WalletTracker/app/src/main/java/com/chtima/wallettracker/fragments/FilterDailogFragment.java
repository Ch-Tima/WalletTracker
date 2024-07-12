package com.chtima.wallettracker.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.CategoryRecycleAdapter;
import com.chtima.wallettracker.adapters.MultipleCategoriesRecycleAdapter;
import com.chtima.wallettracker.components.Swicher;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.DialogObserver;
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.vm.CategoryViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FilterDailogFragment extends BottomSheetDialogFragment {

    private CategoryViewModel categoryViewModel;
    private MultipleCategoriesRecycleAdapter categoryRecycleAdapter;
    private FilterParameters parameters;
    private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;
    private DialogObserver<FilterParameters> dialogObserver;

    //UI
    private RecyclerView recyclerView;
    private Button btnDone;
    private Button btnClear;
    private Button btnData;
    private Swicher swicherTransationType;
    private TextView dateText;


    private FilterDailogFragment(DialogObserver<FilterParameters> dialogObserver, FilterDailogFragment.FilterParameters parameters) {
        this.dialogObserver = dialogObserver;
        this.parameters = parameters;
    }

    public static FilterDailogFragment newInstance(DialogObserver<FilterParameters> dialogObserver) {
        return newInstance(dialogObserver, new FilterParameters());
    }

    public static FilterDailogFragment newInstance(DialogObserver<FilterParameters> dialogObserver, FilterDailogFragment.FilterParameters parameters) {
        return new FilterDailogFragment(dialogObserver, parameters);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryRecycleAdapter = new MultipleCategoriesRecycleAdapter(
                this.requireContext(),
                (List<Category> categories) -> parameters.categories = categories
        );
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getAll().observeForever(list -> {
            categoryRecycleAdapter.updateList(list, parameters.getCategories());
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_dailog, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.list_category);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.addItemDecoration(new CategoryRecycleAdapter.GridSpacingItemDecoration (3,  Math.round(16 * this.getResources().getDisplayMetrics().density), true));
        recyclerView.setAdapter(categoryRecycleAdapter);

        btnData = view.findViewById(R.id.btn_date);

        btnDone = view.findViewById(R.id.btn_done);
        btnClear = view.findViewById(R.id.btn_clear);

        dateText = view.findViewById(R.id.date_text);

        swicherTransationType = (Swicher) view.findViewById(R.id.swicher_transaction_type);

        //setup swicherTransationType
        if(parameters.getTransactionType() != null && swicherTransationType.getTransactionType() != parameters.getTransactionType())
            swicherTransationType.setChecked(parameters.getTransactionType());

        swicherTransationType.setOnChangedSelectionListener(x -> parameters.transactionType = x);

        //clear event
        btnClear.setOnClickListener(x -> clearFilterParameters());

        //initialize date picker dialog
        materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("mDate")
                .setTheme(R.style.CustomMaterialDatePicker)
                .setSelection(
                        new Pair<>(
                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()
                        )
                ).build();

        btnData.setOnClickListener(x -> materialDatePicker.show(this.getChildFragmentManager(), materialDatePicker.getClass().getName()));

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> longLongPair) {
                parameters.startData = longLongPair.first;
                parameters.endData = longLongPair.second;
                dateText.setText(getDateStr(new Date(parameters.startData)) + ((!longLongPair.first.equals(longLongPair.second)) ?(" - " + getDateStr(new Date(parameters.endData))) : ""));
            }
        });

        btnDone.setOnClickListener(x -> {
            dialogObserver.onSuccess(this.parameters);
            dismiss();
        });

        return view;
    }

    public String getDateStr(Date dateTime){
        if(dateTime == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(dateTime.getTime());
    }

    private void clearFilterParameters(){
        this.parameters = new FilterParameters();
        this.swicherTransationType.setChecked(TransactionType.EXPENSE);
        this.dateText.setText("");
        this.categoryRecycleAdapter.clearSelectedCategories();
        this.dialogObserver.onSuccess(this.parameters);
    }

    public static class FilterParameters{
        private List<Category> categories;
        private long startData;
        private long endData;
        private TransactionType transactionType;

        private FilterParameters() {
            categories = new ArrayList<>();
            startData = -1;
            endData = -1;
            transactionType = null;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public long getStartData() {
            return startData;
        }

        public long getEndData() {
            return endData;
        }

        public TransactionType getTransactionType() {
            return transactionType;
        }
    }

}