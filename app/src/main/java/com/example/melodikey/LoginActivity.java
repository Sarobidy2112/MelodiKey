package com.example.melodikey;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;


import android.content.SharedPreferences;


public class LoginActivity extends AppCompatActivity {

    DbConnect dbConnect;
    ImageView btnBack;
    EditText email;
    EditText password;
    Button loginButton;
    TextView signupText;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbConnect = new DbConnect(this);
        signupText = findViewById(R.id.signupText);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPwd);
        loginButton = findViewById(R.id.loginButton);
        btnBack = findViewById(R.id.btnBack);

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        // Vérifier si l'utilisateur est déjà connecté
        if (isUserLoggedIn()) {
            System.out.println("Utilisateur déjà connecté !");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Fermer LoginActivity pour empêcher le retour
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Vers MainActivity!");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = email.getText().toString();
                String strPassword = password.getText().toString();

                if (strEmail.isEmpty() || strPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Login Failed !", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isLogged = dbConnect.checkUser(strEmail, strPassword);

                if (isLogged) {
                    // Récupérer les informations de l'utilisateur
                    Users user = dbConnect.getUserInfo(strEmail);

                    if (user != null) {
                        // Stocker les informations dans SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("user_id", user.getId());
                        editor.putString("user_username", user.getUsername());
                        editor.putString("user_email", user.getEmail());
                        editor.apply();

                        System.out.println("Login successful!");
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Fermer LoginActivity
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to retrieve user info.", Toast.LENGTH_SHORT).show();
                        System.out.println("Failed to retrieve user info.");
                    }
                } else {
                    System.out.println("Login Failed! Wrong Email or Password.");
                    Toast.makeText(LoginActivity.this, "Login Failed! Wrong Email or Password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Vers Register!");
                Intent intent = new Intent(LoginActivity.this, Register.class);
                startActivity(intent);
            }
        });
    }

    // Méthode pour vérifier si l'utilisateur est connecté
    private boolean isUserLoggedIn() {
        return sharedPreferences.contains("user_email");
    }

}