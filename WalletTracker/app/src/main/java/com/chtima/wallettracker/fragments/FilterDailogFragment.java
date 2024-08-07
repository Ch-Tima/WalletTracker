package com.chtima.wallettracker.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    //Static method to create a new instance of FilterDailogFragment with default parameters
    public static FilterDailogFragment newInstance(DialogObserver<FilterParameters> dialogObserver) {
        return newInstance(dialogObserver, new FilterParameters());
    }

    //Static method to create a new instance of FilterDailogFragment with specified parameters
    public static FilterDailogFragment newInstance(DialogObserver<FilterParameters> dialogObserver, FilterDailogFragment.FilterParameters parameters) {
        return new FilterDailogFragment(dialogObserver, parameters);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize category adapter with an empty list and set a click listener to update the parameters
        categoryRecycleAdapter = new MultipleCategoriesRecycleAdapter(
                this.requireContext(),
                (List<Category> categories) -> parameters.categories = categories
        );
        //Initialize the ViewModel and observe changes to update the adapter
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

        //Initialize UI components
        btnData = view.findViewById(R.id.btn_date);
        btnDone = view.findViewById(R.id.btn_done);
        btnClear = view.findViewById(R.id.btn_clear);
        dateText = view.findViewById(R.id.date_text);


        if(parameters != null && parameters.getStartData() > 0 && parameters.getEndData() > 0)
            dateText.setText(getDateStr(new Date(parameters.getStartData()), new Date(parameters.getEndData())));

        swicherTransationType = (Swicher) view.findViewById(R.id.swicher_transaction_type);

        //Setup transaction type switcher
        if(parameters.getTransactionType() != null && swicherTransationType.getTransactionType() != parameters.getTransactionType())
            swicherTransationType.setChecked(parameters.getTransactionType());

        swicherTransationType.setOnChangedSelectionListener(x -> parameters.transactionType = x);

        //Clear event listener
        btnClear.setOnClickListener(x -> clearFilterParameters());

        //Initialize date picker dialog
        materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Date")
                .setTheme(R.style.CustomMaterialDatePicker)
                .setSelection(
                        new Pair<>(
                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()
                        )
                ).build();

        //Show date picker dialog on button click
        btnData.setOnClickListener(x -> materialDatePicker.show(this.getChildFragmentManager(), materialDatePicker.getClass().getName()));

        //Set date range picker positive button click listener
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> longLongPair) {
                parameters.startData = longLongPair.first;
                parameters.endData = longLongPair.second;
                dateText.setText(getDateStr(new Date(parameters.startData)) + ((!longLongPair.first.equals(longLongPair.second)) ?(" - " + getDateStr(new Date(parameters.endData))) : ""));
            }
        });

        //Set done button click listener
        btnDone.setOnClickListener(x -> {
            dialogObserver.onSuccess(this.parameters);
            dismiss();
        });

        return view;
    }

    //Helper method to format a date to a string
    public String getDateStr(Date dateTime){
        if(dateTime == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(dateTime.getTime());
    }

    //Helper method to format a date range to a string
    public String getDateStr(Date start, Date end){
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        builder.append(dateFormat.format(start.getTime()));

        if(end.getTime() > start.getTime())
            builder.append(" - ").append(dateFormat.format(end.getTime()));

        return builder.toString();
    }

    //Method to clear filter parameters and reset UI components
    private void clearFilterParameters(){
        this.swicherTransationType.setChecked(TransactionType.EXPENSE);
        this.dateText.setText("");
        this.categoryRecycleAdapter.clearSelectedCategories();
        this.parameters = new FilterParameters();
        this.dialogObserver.onSuccess(new FilterParameters());
    }

    //Class to hold filter parameters
    public static class FilterParameters{
        private List<Category> categories;
        private long startData;
        private long endData;
        private TransactionType transactionType;

        //Private constructor to initialize default values
        private FilterParameters() {
            categories = new ArrayList<>();
            startData = -1;
            endData = -1;
            transactionType = null;
        }


        //Getters for filter parameters
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

        //Method to check if filter parameters are empty
        public boolean isEmpty(){
            return (categories == null || categories.isEmpty()) && startData == -1 && endData == -1 && transactionType == null;
        }
    }

}