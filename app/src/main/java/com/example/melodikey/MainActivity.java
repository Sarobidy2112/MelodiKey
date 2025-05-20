package com.example.melodikey;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    ImageView btnToLogin;
    ImageView btnToOrganize;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        btnToLogin = findViewById(R.id.btnToLogin);
        textView = findViewById(R.id.textView);

        // Vérifie si l'utilisateur est connecté
        boolean isLoggedIn = sharedPreferences.contains("user_email");

        if (isLoggedIn) {
            // Modifier le texte pour "Se déconnecter"
            textView.setText("Se déconnecter");
            btnToLogin.setImageResource(R.drawable.deconnexion);
        } else {
            textView.setText("Se connecter");
            btnToLogin.setImageResource(R.drawable.connexion);
        }


        btnToLogin = findViewById(R.id.btnToLogin);

        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLoggedIn = sharedPreferences.contains("user_email");

                if (isLoggedIn) {
                    // Déconnexion
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show();

                    // Recharger MainActivity pour mettre à jour le bouton
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Rediriger vers la connexion
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnToOrganize = findViewById(R.id.btnToOrganize);
        btnToOrganize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLoggedIn = sharedPreferences.contains("user_email");

                if (isLoggedIn) {
                    Intent intent = new Intent(MainActivity.this, OrganizeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Rediriger vers la connexion
                    Toast.makeText(MainActivity.this, "Connecter vous pour Organisez vos répertoire", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}