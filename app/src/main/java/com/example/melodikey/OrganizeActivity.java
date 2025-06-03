package com.example.melodikey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.SharedPreferences;
import android.widget.Toast;

public class OrganizeActivity extends AppCompatActivity {


    TextView textDate;
    TextView textView;
    SharedPreferences sharedPreferences;
    ImageView btnToLogin;
    ImageView datePicker;
    ImageView locationPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organize);

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

        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLoggedIn = sharedPreferences.contains("user_email");

                if (isLoggedIn) {
                    // Déconnexion
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(OrganizeActivity.this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show();

                    // Recharger MainActivity pour mettre à jour le bouton
                    Intent intent = new Intent(OrganizeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Rediriger vers la connexion
                    Intent intent = new Intent(OrganizeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        datePicker = findViewById(R.id.datePicker);
        textDate = findViewById(R.id.textDate);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long>materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Selectioner la date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection));
                        textDate.setHint("      Date selectioner : " + date);
                    }
                });

                materialDatePicker.show(getSupportFragmentManager(),"tag");
            }
        });

        locationPicker = findViewById(R.id.locationPicker);

        locationPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizeActivity.this,MapLocationActivity.class);
                startActivity(intent);
            }
        });
    }
}