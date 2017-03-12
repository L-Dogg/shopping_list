package com.example.szymek.shopping;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainFragment extends Fragment {
    private List<ShoppingItem> list;
    private ShoppingAdapter shoppingAdapter;
    private FeedReaderDbHelper mDbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shopping_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        list = new ArrayList<>();
        shoppingAdapter = new ShoppingAdapter(list, getActivity());

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ShoppingItem item = list.get(position);
                editItem(item);
            }

            @Override
            public void onLongClick(View view, int position) {
                removeItem(position);
            }
        }));

        /*CheckBox checkBox = (CheckBox) recyclerView.findViewById();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });*/

        recyclerView.setAdapter(shoppingAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addItem();
            }
        });

        mDbHelper = new FeedReaderDbHelper(getContext());
        prepareData();
        return  view;
    }

    @Override
    public void onStop() {
        mDbHelper.close();
        super.onStop();
    }

    private void prepareData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_QUANTITY

        };
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                null, null, null, null, null, null
        );

        while(cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
            byte[] itemImage = cursor.getBlob(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE));
            Double itemQuantity = cursor.getDouble(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_QUANTITY));
            //DbBitmapUtility.getImage(itemImage);
            list.add(new ShoppingItem(itemName, itemQuantity));
        }
        cursor.close();

        shoppingAdapter.notifyDataSetChanged();
    }

    private void addItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add new item");

        Context context = getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameInput = new EditText(getActivity());
        nameInput.setHint("Name");
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nameInput);

        final EditText quantityInput = new EditText(getContext());
        quantityInput.setHint("Quantity");
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(quantityInput);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nameInput.length() == 0 || quantityInput.length() == 0) {
                    Toast.makeText(getContext(), "Invalid data", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = nameInput.getText().toString();
                Double quantity =  Double.parseDouble(quantityInput.getText().toString());
                list.add(new ShoppingItem(name, quantity));

                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_QUANTITY, quantity);

                // TODO:
                // save image

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

                shoppingAdapter.notifyDataSetChanged();
                dialog.dismiss();
                Toast.makeText(getContext(), nameInput.getText() + " added!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void editItem(final ShoppingItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit item");

        Context context = getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameInput = new EditText(getContext());
        nameInput.setText(item.getTitle());
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nameInput);

        final EditText quantityInput = new EditText(getContext());
        quantityInput.setText(String.valueOf(item.getQuantity()));
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(quantityInput);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nameInput.length() == 0 || quantityInput.length() == 0) {
                    Toast.makeText(getContext(), "Invalid data", Toast.LENGTH_SHORT).show();
                }
                else {
                    item.setTitle(nameInput.getText().toString());
                    item.setQuantity(Double.parseDouble(quantityInput.getText().toString()));
                    shoppingAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    Toast.makeText(getContext(), nameInput.getText() + " edited!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void removeItem(int id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ShoppingItem item = list.get(id);
        Toast.makeText(getContext(), item.getTitle() + " removed!", Toast.LENGTH_SHORT).show();
        list.remove(id);

        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id + 1)};
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs);
        shoppingAdapter.notifyDataSetChanged();
    }
}
