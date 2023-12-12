package com.example.myapplication2.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    // Declare variables for EditText values
    private String username,email, password,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Find all the necessary views
        final EditText userNameEditText = findViewById(R.id.userNameEditText);
        final EditText emailEditText = findViewById(R.id.emailEditText);
        final EditText passwordEditText = findViewById(R.id.passwordEditText);
        final EditText confirmPwdEditText = findViewById(R.id.confirmpwdEditText);

        ImageButton backBtn = findViewById(R.id.backBtn);
        Button signUpBtn = findViewById(R.id.signUpBtn);

        // Handle click event for the back button (ImageButton)
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the login activity
                finish();
            }
        });

        // Handle click event for the sign-up button
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get values from EditText fields
                username = userNameEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                confirmPassword = confirmPwdEditText.getText().toString();

                // Validate email
                if (!isValidEmail(email)) {
                    // Show an error message for invalid email
                    Toast.makeText(SignUpActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                //validate password
                if (password.equals(confirmPassword)) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userDocumentRef = db.collection("User").document(username);
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("username", username);
                    userData.put("email", email);
                    userData.put("password", password);

                    userDocumentRef.set(userData);
                    Toast.makeText(SignUpActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    // Passwords do not match, show an error message or handle it accordingly
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
