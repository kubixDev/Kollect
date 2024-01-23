package com.kubixdev.kollect.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kubixdev.kollect.R;

public class PasswordChangeActivity extends AppCompatActivity {
    AppCompatButton changePasswordButton;
    ImageView backButton;
    EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        changePasswordButton = findViewById(R.id.changePasswordButton);
        backButton = findViewById(R.id.backButton);
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        // handle back button click
        backButton.setOnClickListener(v -> {
            finish();
        });

        // handle change password button click
        changePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // handle empty input fields
            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Password fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // handle secure passwords
            if (!newPassword.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                newPasswordInput.setError("Password must contain at least one uppercase letter, one digit, one special character and be at least 8 characters long");
                return;
            }

            // handle different password inputs
            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords don't match");
                return;
            }

            // handle password change
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                String email = user.getEmail();

                // user needs to reenter the current password
                AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

                // reauthenticate the user
                user.reauthenticate(credential)
                        .addOnCompleteListener(reauthTask -> {
                            if (reauthTask.isSuccessful()) {

                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(PasswordChangeActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(PasswordChangeActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(PasswordChangeActivity.this, "Reauthentication failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}