package com.chtima.wallettracker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.models.Category;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleCategoriesRecycleAdapter extends RecyclerView.Adapter<MultipleCategoriesRecycleAdapter.ViewHolder>{

    private Context context;
    private List<Category> list;
    private List<Category> selectedCategories;
    private OnClickListener onClickListener;

    //Constructor with context and OnClickListener, initializing an empty list
    public MultipleCategoriesRecycleAdapter(Context context, OnClickListener onClickListener) {
        this(context, new ArrayList<>(), onClickListener);
    }

    //Constructor with context, list of categories, and OnClickListener
    public MultipleCategoriesRecycleAdapter(Context context, List<Category> list, OnClickListener onClickListener) {
        this.context = context;
        this.list = list;
        this.onClickListener = onClickListener;
        this.selectedCategories = new ArrayList<>();
    }

    @NonNull
    @Override
    public MultipleCategoriesRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout for each item of the RecyclerView
        return new MultipleCategoriesRecycleAdapter.ViewHolder(
                LayoutInflater.from(this.context)
                        .inflate(R.layout.category_card_tile, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MultipleCategoriesRecycleAdapter.ViewHolder holder, int position) {
        Category item = this.list.get(position);//Bind data to the ViewHolder
        holder.title.setText(item.title);
        holder.icon.setImageDrawable(getCategoryIcon(item));

        //Check if the item is selected and set the style accordingly
        if(selectedCategories.stream().map(x -> x.id).collect(Collectors.toList()).contains(item.id))
            setSelectedStyle(holder);
        else
            setNotSelectedStyle(holder);
        //Set click listener for the item
        holder.itemView.setOnClickListener(y -> {
            if(selectedCategories.remove(item))
                setNotSelectedStyle(holder);
            else {
                selectedCategories.add(item);
                setSelectedStyle(holder);
            }
            //Call the onClick method with the updated selected categories list
            onClickListener.onClick(selectedCategories);//always
        });
    }

    //Set the style for a selected item
    private void setSelectedStyle(ViewHolder holder){
        holder.itemView.setBackgroundResource(R.drawable.round_blue_layout_8dp);
        holder.icon.setImageTintList(context.getColorStateList(R.color.light_ashen_35));
        holder.title.setTextColor(context.getColorStateList(R.color.light_ashen_35));
    }

    private void setNotSelectedStyle(ViewHolder holder){
        holder.itemView.setBackgroundResource(R.drawable.round_ashen35_8dp);
        holder.icon.setImageTintList(context.getColorStateList(R.color.dark_midnight_blue));
        holder.title.setTextColor(context.getColorStateList(R.color.dark_midnight_blue));
    }

    /**
     * Get the Drawable icon for a category.
     *
     * @param category The category object containing icon information.
     * @return Drawable representing the category icon.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable getCategoryIcon(Category category) {
        String uri = category.icon;

        if (uri.startsWith("res://")) {
            int resourceId = Integer.parseInt(uri.substring("res://".length()));
            return context.getDrawable(resourceId);
        } else {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(uri));
                return Drawable.createFromStream(inputStream, uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    /**
     * Update the RecyclerView's data list with a new list of categories.
     * @param list The new list of categories to display.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Category> list, List<Category> selectedCategories){
        this.list.clear(); // Clear current list
        this.selectedCategories.clear();
        this.list.addAll(list); // Add new list
        this.selectedCategories.addAll(selectedCategories);
        this.notifyDataSetChanged(); // Notify RecyclerView of data change
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelectedCategories(){
        selectedCategories.clear();
        notifyDataSetChanged();
        onClickListener.onClick(selectedCategories);
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }

    public interface OnClickListener {
        void onClick(List<Category> categories);
    }

    /**
     * ViewHolder class for holding item views */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
