package com.kubixdev.kollect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.fragments.CollectionFragment;
import com.kubixdev.kollect.fragments.ExploreFragment;
import com.kubixdev.kollect.fragments.HomeFragment;
import com.kubixdev.kollect.fragments.MyProfileFragment;
import com.kubixdev.kollect.model.User;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    ExploreFragment exploreFragment = new ExploreFragment();
    CollectionFragment collectionFragment = new CollectionFragment();
    MyProfileFragment myProfileFragment = new MyProfileFragment();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        // used to initialize the firestore database with data (only for development purposes)
        //FirestoreInitializer.initializeFirestore();

        Log.d("USERINFO", "ID: " + currentUser.getUid());

        // making sure the user is logged in to use the app
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // loads current user data from database and passes it to fragments that need it
        User.loadUserData(currentUser.getUid(), new User.UserDataLoadListener() {
            @Override
            public void onUserDataLoaded(User user) {
                myProfileFragment.passUserData(user);
            }

            @Override
            public void onUserDataLoadFailed(String error) {
                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // handles bottom navigation between fragments
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