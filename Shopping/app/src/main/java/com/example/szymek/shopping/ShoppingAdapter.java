package com.example.szymek.shopping;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Szymek on 10-Mar-17.
 */

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingViewHolder> {
    private List<ShoppingItem> list;
    private Context mContext;

    public ShoppingAdapter(List<ShoppingItem> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public ShoppingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_shopping_item, null);
        ShoppingViewHolder shoppingViewHolder = new ShoppingViewHolder(view);
        return shoppingViewHolder;
    }

    @Override
    public void onBindViewHolder(ShoppingViewHolder holder, int position) {
        ShoppingItem shoppingItem = list.get(position);
        holder.mTitleView.setText(shoppingItem.getTitle());
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        holder.mDescriptionView.setText(format.format(shoppingItem.getQuantity()));

        Resources resources = mContext.getResources();
        SharedPreferences sharedPref = ((Activity)mContext).getPreferences(Context.MODE_PRIVATE);
        String prefColor = sharedPref.getString(resources.getString(R.string.color), "black");

        String[] colorArray = resources.getStringArray(R.array.font_color_values);

        for (String color: colorArray) {
            if (prefColor.equals(color))
                changeColor(holder, color);
        }
        // TODO:
        // change font size (and maybe whole row size)
    }
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private void changeColor(ShoppingViewHolder holder, String color) {
        int c = Color.parseColor(color);
        holder.mTitleView.setTextColor(c);
        holder.mDescriptionView.setTextColor(c);

    }
}
