package com.chtima.wallettracker.fragments;

import android.os.Binder;
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
import android.widget.Button;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.adapters.CategoryRecycleAdapter;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.vm.CategoryViewModel;

public class SelectCategoryDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private CategoryRecycleAdapter adapter;
    private CategoryViewModel categoryViewModel;
    private SelectCategoryListener selectCategoryListener;

    private SelectCategoryDialogFragment() {
    }

    public static SelectCategoryDialogFragment newInstance() {
        return new SelectCategoryDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_category_dialog, container, false);

        //init
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        adapter = new CategoryRecycleAdapter(this.requireContext());
        recyclerView = view.findViewById(R.id.list_category);
        //setup
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.requireContext(), 3));
        recyclerView.addItemDecoration(new CategoryRecycleAdapter.GridSpacingItemDecoration (3,  Math.round(16 * this.getResources().getDisplayMetrics().density), true));
        //request
        categoryViewModel.getAll().observe(this, categories -> this.adapter.updateList(categories));

        adapter.setOnClickListener(category -> {
            if(selectCategoryListener != null) selectCategoryListener.onSelected(category);
            this.getDialog().dismiss();
        });

        return view;
    }

    public void setSelectCategoryListener(SelectCategoryListener selectCategoryListener) {
        this.selectCategoryListener = selectCategoryListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setBackgroundDrawableResource(R.drawable.dark_round_layout_m16);
            window.setAttributes(params);
        }
    }

    public interface SelectCategoryListener{
        void onSelected(Category category);
    }

}