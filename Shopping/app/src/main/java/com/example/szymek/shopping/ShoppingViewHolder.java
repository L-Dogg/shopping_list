package com.example.szymek.shopping;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShoppingViewHolder extends RecyclerView.ViewHolder{
    TextView mTitleView;
    TextView mDescriptionView;
    ImageView mImageView;

    public ShoppingViewHolder(View itemView) {
        super(itemView);
        this.mTitleView = (TextView) itemView.findViewById(R.id.item_title);
        this.mDescriptionView = (TextView) itemView.findViewById(R.id.item_quantity);
        this.mImageView = (ImageView) itemView.findViewById(R.id.item_type_image);

        CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
    }
}
