package com.chtima.wallettracker.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

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

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.CategoryRecycleAdapter;
import com.chtima.wallettracker.adapters.MultipleCategoriesRecycleAdapter;
import com.chtima.wallettracker.components.Swicher;
import com.chtima.wallettracker.models.Category;
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

    //UI
    private RecyclerView recyclerView;
    private Button btnDone;
    private Button btnClear;
    private Button btnData;
    private Swicher swicherTransationType;
    private TextView dateText;

    private FilterDailogFragment() {}

    public static FilterDailogFragment newInstance() {
        FilterDailogFragment fragment = new FilterDailogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parameters = new FilterParameters();
        categoryRecycleAdapter = new MultipleCategoriesRecycleAdapter(
                this.requireContext(),
                (List<Category> categories) -> parameters.categories = categories
        );
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getAll().observeForever(list -> {
            categoryRecycleAdapter.updateList(list);
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

        //events
        swicherTransationType.setOnChangedSelectionListener(x -> parameters.transactionType = x);
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
                parameters.startData = new Date(longLongPair.first);
                parameters.endData = new Date(longLongPair.second);
                dateText.setText(getDateStr(parameters.startData) + ((!longLongPair.first.equals(longLongPair.second)) ?(" - " + getDateStr(parameters.endData)) : ""));
            }
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
        this.dateText.setText("");
        this.categoryRecycleAdapter.clearSelectedCategories(); 
    }

    public static class FilterParameters{
        List<Category> categories;
        Date startData;
        Date endData;
        TransactionType transactionType;

        FilterParameters() {
            categories = null;
            startData = null;
            endData = null;
            transactionType = null;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public Date getStartData() {
            return startData;
        }

        public Date getEndData() {
            return endData;
        }

        public TransactionType getTransactionType() {
            return transactionType;
        }
    }

}