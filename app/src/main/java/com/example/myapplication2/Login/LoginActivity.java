package com.example.myapplication2.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.MainActivity;
import com.example.myapplication2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Add your login button click listener
        Button loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(v -> {
            normalLogin();
        });

        ImageButton googleSignInBtn = findViewById(R.id.googleSignInBtn);
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToGoogleSignIn();
            }
        });

        TextView signUpWord = findViewById(R.id.signUpWord);
        signUpWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToGoogleSignIn() {
        // Implement navigation to the main activity after successful login
        Intent intent = new Intent(this, GoogleSignInActivity.class);
        startActivity(intent);
        finish();
    }

    // Add this method to handle navigation to the main activity
    private void navigateToMainActivity() {
        // Implement navigation to the main activity after successful login
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void normalLogin() {
        // Inside the loginBtn click listener or a method where you handle login
        // Perform the login logic with user input
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(view -> {
            EditText usernameInput = findViewById(R.id.userNameInput);
            EditText passwordInput = findViewById(R.id.passwordInput);
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            checkLoginDataInFirestore(username, password);
        });
    }

    private void storeCurrentUserInFirestore(String userName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", userName);

        db.collection("User")
                .document("currentUser")
                .set(userData);
    }

    private void checkLoginDataInFirestore(String userName, String password) {
        // Assuming you have a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if the username exists in the "Users" collection
        db.collection("User")
                .document(userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // User exists, now check the password
                                String storedPassword = document.getString("username");
                                if (password.equals(storedPassword)) {
                                    // Password matches, login successful
                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    storeCurrentUserInFirestore(userName);
                                    navigateToMainActivity();
                                } else {
                                    // Password does not match
                                    Toast.makeText(LoginActivity.this, "Authentication failed. Incorrect password.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // User does not exist
                                Toast.makeText(LoginActivity.this, "Authentication failed. User does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle any errors that may occur
                            Toast.makeText(LoginActivity.this, "Error during authentication.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
