package com.kubixdev.kollect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kubixdev.kollect.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText usernameInput, emailInput, passwordInput, repeatPasswordInput;
    AppCompatButton signupButton, switchToLoginButton;
    FirebaseAuth mAuth;
    FirebaseFirestore database;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // redirects to the app content if the user is already logged in
        if (currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // returns the current date and time
    private String getCurrentDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(currentDate);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        repeatPasswordInput = findViewById(R.id.repeatPasswordInput);
        signupButton = findViewById(R.id.signupButton);
        switchToLoginButton = findViewById(R.id.switchToLoginButton);


        // handle action after signup button click
        signupButton.setOnClickListener(v -> {
            String email, username, password, repeatPassword;
            email = emailInput.getText().toString();
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();
            repeatPassword = repeatPasswordInput.getText().toString();

            // handle empty input fields
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repeatPassword)) {
                Toast.makeText(SignupActivity.this, "Account details cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // handle incorrect email address
            if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                emailInput.setError("Invalid email address");
                return;
            }

            // handle secure passwords
            if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                passwordInput.setError("Password must contain at least one uppercase letter, one digit, one special character and be at least 8 characters long");
                return;
            }

            // handle different password inputs
            if (!password.equals(repeatPassword)) {
                repeatPasswordInput.setError("Passwords don't match");
                return;
            }

            // handle registering a new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        // user registration successful
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null) {
                                final String userId = firebaseUser.getUid();

                                Map<String, Object> user = new HashMap<>();
                                user.put("username", username);
                                user.put("accountType", "basic");
                                user.put("accountCreationDate", getCurrentDate());
                                user.put("profileImage", "default.jpg");
                                user.put("ownedPhotocardIds", new ArrayList<String>());
                                user.put("wishlistPhotocardIds", new ArrayList<String>());
                                user.put("friendListIds", new ArrayList<String>());

                                // add user data to firestore under the user's UID
                                database.collection("users").document(userId).set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignupActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(SignupActivity.this, "Firestore data write failed", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }

                        else {
                            Toast.makeText(SignupActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // handle action after alternative log in button click
        switchToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}