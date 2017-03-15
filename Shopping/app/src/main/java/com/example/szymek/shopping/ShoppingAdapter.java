package com.example.szymek.shopping;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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
        String prefSize = sharedPref.getString(resources.getString(R.string.size), "normal");

        String[] colorArray = resources.getStringArray(R.array.font_color_values);
        String[] sizeArray = resources.getStringArray(R.array.font_size_values);

        for (String color: colorArray) {
            if (prefColor.equals(color))
                changeColor(holder, color);
        }

        if (prefSize.equals("small"))
            changeSize(holder, 20);
        else if (prefSize.equals("normal"))
            changeSize(holder, 32);
        else if (prefSize.equals("big"))
            changeSize(holder, 38);
        else
            changeSize(holder, 46);
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
    private void changeSize(ShoppingViewHolder holder, int size)
    {
        holder.mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        holder.mDescriptionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }
}
