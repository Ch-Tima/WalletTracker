package com.chtima.wallettracker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.chtima.wallettracker.R
import com.chtima.wallettracker.models.Category


class CategoryRecycleAdapter (
    private val context: Context,
    private val list: ArrayList<Category>,
    private val isShowSelectedItem: Boolean) : RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder>() {

    private var selectedCategory: Category? = null
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.category_card_tile, parent, false));
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val it = list[position]
        holder.title.text = it.title
        holder.icon.setImageDrawable(getCategoryIcon(it));

        holder.itemView.setOnClickListener { _ ->
            selectedCategory = it //set new selected category
            notifyDataSetChanged() //update ui
            onClickListener?.onClick(it)
        }

        if(!isShowSelectedItem) return;

        if(it.equals(selectedCategory)){ //Set the style for a selected item
            holder.itemView.setBackgroundResource(R.drawable.rounded_blue_8dp)
            holder.icon.setImageTintList(context.getColorStateList(R.color.light_ashen_35))
            holder.title.setTextColor(context.getColorStateList(R.color.light_ashen_35))
        }
        else {
            holder.itemView.setBackgroundResource(R.drawable.rounded_8dp_ashen35)
            holder.icon.setImageTintList(context.getColorStateList(R.color.dark_midnight_blue))
            holder.title.setTextColor(context.getColorStateList(R.color.dark_midnight_blue))
        }

    }

    /**
     * Update the RecyclerView's data list with a new list of categories.
     * @param list The new list of categories to display.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Category>) {
        this.list.clear() // Clear current list
        this.list.addAll(list) // Add new list
        this.notifyDataSetChanged() // Notify RecyclerView of data change
    }

    /**
     * Get the Drawable icon for a category.
     *
     * @param category The category object containing icon information.
     * @return Drawable representing the category icon.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getCategoryIcon(category: Category): Drawable? {
        val id = context.resources.getIdentifier(category.icon, "drawable", context.packageName)
        return context.getDrawable(id)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    /**
     * ViewHolder class for caching views in each RecyclerView item.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val title: TextView = itemView.findViewById(R.id.title)
    }

    /**
     * Interface definition for a callback to be invoked when a category item is clicked.
     */
    interface OnClickListener {
        fun onClick(category: Category)
    }

    /**
     * ItemDecoration for adding spacing between grid items in RecyclerView.
     */
    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) :
        ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view) //item position
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount //Left indent
                outRect.right = (column + 1) * spacing / spanCount //Right indent

                if (position < spanCount) { //Top row
                    outRect.top = spacing
                }
                outRect.bottom = spacing //Bottom indent
            } else {
                outRect.left = column * spacing / spanCount //Left indent
                outRect.right = spacing - (column + 1) * spacing / spanCount //Right indent
                if (position >= spanCount) {
                    outRect.top = spacing // Top indent
                }
            }
        }
    }

}