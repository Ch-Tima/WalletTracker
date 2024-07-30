package com.chtima.wallettracker.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.CategoryRecycleAdapter;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.Category.CategoryType;
import com.chtima.wallettracker.vm.CategoryViewModel;

import java.util.ArrayList;

/**
 * DialogFragment for selecting a category.
 */
public class SelectCategoryDialogFragment extends DialogFragment {

    private final static String CATEGORY_TYPE = "CATEGORY_TYPE";
    private final static String IS_SHOW_SELECTED_CATEGORY = "IS_SHOW_SELECTED_CATEGORY";

    private CategoryRecycleAdapter adapter;//Adapter for RecyclerView
    private CategoryViewModel categoryViewModel;//VM
    private SelectCategoryListener selectCategoryListener;//Listener
    private CategoryType categoryType = null;
    private boolean isShowSelectCategory;

    //UI
    private RecyclerView recyclerView;

    private SelectCategoryDialogFragment() {}

    /**
     * Static factory method to create a new instance of SelectCategoryDialogFragment.
     * @return A new instance of SelectCategoryDialogFragment.
     */
    public static SelectCategoryDialogFragment newInstance() {
        return newInstance(null, false);
    }

    /**
     * Static factory method to create a new instance of SelectCategoryDialogFragment.
     * @param categoryType - use to filter and show only a specific type category
     * @return A new instance of SelectCategoryDialogFragment.
     */
    public static SelectCategoryDialogFragment newInstance(CategoryType categoryType, boolean isShowSelectCategory) {
        SelectCategoryDialogFragment fragment = new SelectCategoryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY_TYPE, categoryType == null ? null : categoryType.name());
        bundle.putBoolean(IS_SHOW_SELECTED_CATEGORY, isShowSelectCategory);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ct;
        if (getArguments() != null && (ct = getArguments().getString(CATEGORY_TYPE)) != null) {
            categoryType = CategoryType.valueOf(ct);
            isShowSelectCategory = getArguments().getBoolean(IS_SHOW_SELECTED_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_category_dialog, container, false);

        //init
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        adapter = new CategoryRecycleAdapter(this.requireContext(), new ArrayList<>(), isShowSelectCategory);
        recyclerView = view.findViewById(R.id.list_category);
        //setup
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.requireContext(), 3));
        recyclerView.addItemDecoration(new CategoryRecycleAdapter.GridSpacingItemDecoration (3,  Math.round(16 * this.getResources().getDisplayMetrics().density), true));
        //request
        categoryViewModel.getByType(this.categoryType).observe(this, categories -> this.adapter.updateList(categories));

        //set click listener for RecyclerView items
        adapter.setOnClickListener(category -> {
            if(selectCategoryListener != null) selectCategoryListener.onSelected(category);
            if(this.getDialog() != null) this.getDialog().dismiss();//dismiss the dialog after category selection
        });

        return view;
    }

    /**
     * Setter method for setting the SelectCategoryListener.
     * @param selectCategoryListener The listener to be set.
     */
    public void setSelectCategoryListener(SelectCategoryListener selectCategoryListener) {
        this.selectCategoryListener = selectCategoryListener;
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

    /** Interface definition for a callback to be invoked when a category is selected. */
    public interface SelectCategoryListener{
        void onSelected(Category category);
    }

}