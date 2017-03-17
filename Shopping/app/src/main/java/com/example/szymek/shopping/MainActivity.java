package com.example.szymek.shopping;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by Szymek on 10-Mar-17.
 */

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout viewById = (FrameLayout) findViewById(R.id.main_container);

        if (savedInstanceState == null)
            getFragmentManager().beginTransaction()
                .replace(R.id.main_container, new MainFragment())
                .commit();

        //Listen for changes in the back stack
        getFragmentManager().addOnBackStackChangedListener(new android.app.FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                shouldDisplayHomeUp();
            }
        });
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, new SettingsFragment())
                .addToBackStack("settingsFragment")
                .commit();

        shouldDisplayHomeUp();

        return super.onOptionsItemSelected(item);
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getFragmentManager().popBackStack("settingsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        return true;
    }
}
