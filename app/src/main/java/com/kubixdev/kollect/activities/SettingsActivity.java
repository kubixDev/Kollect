package com.kubixdev.kollect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.FirebaseAuth;
import com.kubixdev.kollect.R;

public class SettingsActivity extends AppCompatActivity {
    AppCompatButton logoutButton, editProfileButton, passwordChangeButton;
    ImageView backButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logoutButton);
        editProfileButton = findViewById(R.id.editProfileButton);
        passwordChangeButton = findViewById(R.id.passwordChangeButton);
        backButton = findViewById(R.id.backButton);

        // handle user logout
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
        });

        // handle back button click
        backButton.setOnClickListener(v -> {
            finish();
        });

        // handle edit profile button click
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // handle password change button click
        passwordChangeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PasswordChangeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}