package com.chtima.wallettracker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
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

public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder> {

    private Context context;
    private List<Category> list;
    private OnClickListener onClickListener;

    public CategoryRecycleAdapter(Context context) {
        this(context, new ArrayList<Category>());
    }

    public CategoryRecycleAdapter(Context context, List<Category> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.category_card_tile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category item = this.list.get(position);
        holder.title.setText(item.title);
        holder.icon.setImageDrawable(getCategoryIcon(item));
        holder.itemView.setOnClickListener(y -> onClickListener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Category> list){
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

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

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView icon;
        private final TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
        }
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration{

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            int position = parent.getChildAdapterPosition(view);//item position
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; //Left indent
                outRect.right = (column + 1) * spacing / spanCount; //Right indent

                if (position < spanCount) { //Top row
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; //Bottom indent
            } else {
                outRect.left = column * spacing / spanCount; //Left indent
                outRect.right = spacing - (column + 1) * spacing / spanCount; //Right indent
                if (position >= spanCount) {
                    outRect.top = spacing; // Top indent
                }
            }

        }
    }

    public interface OnClickListener{
        void onClick(Category category);
    }

}
