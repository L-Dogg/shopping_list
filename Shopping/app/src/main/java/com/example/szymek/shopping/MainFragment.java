package com.example.szymek.shopping;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {
    private List<ShoppingItem> list;
    private ShoppingAdapter shoppingAdapter;
    private FeedReaderDbHelper mDbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shopping_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        list = new ArrayList<>();
        shoppingAdapter = new ShoppingAdapter(list, getActivity(), this);
        recyclerView.setAdapter(shoppingAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addItem();
            }
        });

        mDbHelper = new FeedReaderDbHelper(getActivity().getApplicationContext());
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
                FeedReaderContract.FeedEntry.COLUMN_NAME_TYPE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_QUANTITY

        };
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                null, null, null, null, null, null
        );

        while(cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
            String itemType = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TYPE));
            Double itemQuantity = cursor.getDouble(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_QUANTITY));

            list.add(new ShoppingItem(itemName, itemQuantity, itemType));
        }
        cursor.close();

        shoppingAdapter.notifyDataSetChanged();
    }

    private void addItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add new item");

        Context context = getActivity().getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameInput = new EditText(getActivity());
        nameInput.setHint("Name");
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nameInput);

        final EditText quantityInput = new EditText(getActivity());
        quantityInput.setHint("Quantity");
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(quantityInput);

        final TextView spinnerLabel = new TextView(getActivity());
        spinnerLabel.setText(R.string.spinner_label);

        layout.addView(spinnerLabel);

        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(ItemTypes.DRINK);
        spinnerArray.add(ItemTypes.FOOD);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = new Spinner(getActivity());
        spinner.setAdapter(arrayAdapter);
        layout.addView(spinner);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nameInput.length() == 0 || quantityInput.length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid data", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = nameInput.getText().toString();
                Double quantity =  Double.parseDouble(quantityInput.getText().toString());
                String type = spinner.getSelectedItem().toString();
                list.add(new ShoppingItem(name, quantity, type));

                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TYPE, type);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_QUANTITY, quantity);

                long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

                shoppingAdapter.notifyDataSetChanged();
                dialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), nameInput.getText() + " added!", Toast.LENGTH_SHORT).show();
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

    public void editItem(final ShoppingItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit item");

        Context context = getActivity().getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameInput = new EditText(getActivity());
        nameInput.setText(item.getTitle());
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nameInput);

        final EditText quantityInput = new EditText(getActivity());
        quantityInput.setText(String.valueOf(item.getQuantity()));
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(quantityInput);

        final TextView spinnerLabel = new TextView(getActivity());
        spinnerLabel.setText(R.string.spinner_label);

        layout.addView(spinnerLabel);

        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(ItemTypes.DRINK);
        spinnerArray.add(ItemTypes.FOOD);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = new Spinner(getActivity());
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(item.getType().equals(ItemTypes.DRINK) ? 0 : 1);
        layout.addView(spinner);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nameInput.length() == 0 || quantityInput.length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid data", Toast.LENGTH_SHORT).show();
                }
                else {
                    item.setTitle(nameInput.getText().toString());
                    item.setQuantity(Double.parseDouble(quantityInput.getText().toString()));
                    item.setType(spinner.getSelectedItem().toString());
                    shoppingAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), nameInput.getText() + " edited!", Toast.LENGTH_SHORT).show();
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

    public void removeItem(int id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ShoppingItem item = list.get(id);
        Toast.makeText(getActivity().getApplicationContext(), item.getTitle() + " removed!", Toast.LENGTH_SHORT).show();
        list.remove(id);

        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id + 1)};
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs);
        shoppingAdapter.notifyDataSetChanged();
    }
}
