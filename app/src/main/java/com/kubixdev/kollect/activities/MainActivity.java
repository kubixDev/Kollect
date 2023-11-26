package com.kubixdev.kollect.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.fragments.CollectionFragment;
import com.kubixdev.kollect.fragments.ExploreFragment;
import com.kubixdev.kollect.fragments.HomeFragment;
import com.kubixdev.kollect.fragments.MyProfileFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    ExploreFragment exploreFragment = new ExploreFragment();
    CollectionFragment collectionFragment = new CollectionFragment();
    MyProfileFragment myProfileFragment = new MyProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, homeFragment)
                        .commit();
                return true;

            case R.id.search:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, exploreFragment)
                        .commit();
                return true;

            case R.id.collection:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, collectionFragment)
                        .commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, myProfileFragment)
                        .commit();
                return true;
        }
        return false;
    }
}