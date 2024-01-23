package com.kubixdev.kollect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.FirebaseAuth;
import com.kubixdev.kollect.R;

public class PasswordResetActivity extends AppCompatActivity {
    AppCompatButton resetPasswordButton;
    ImageView backButton;
    EditText emailInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backButton = findViewById(R.id.backButton);
        emailInput = findViewById(R.id.emailInput);

        // handle back button click
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // handle reset password button click
        resetPasswordButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                emailInput.setError("Please enter your email");
            }
            else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordResetActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(PasswordResetActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // handle back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}