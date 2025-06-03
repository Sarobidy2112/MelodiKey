package com.example.melodikey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrganizeActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 100;

    TextView textDate;
    EditText textLocation;
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

        textView = findViewById(R.id.textView);
        btnToLogin = findViewById(R.id.btnToLogin);
        textDate = findViewById(R.id.textDate);
        textLocation = findViewById(R.id.textLocation);
        datePicker = findViewById(R.id.datePicker);
        locationPicker = findViewById(R.id.locationPicker);

        // Vérifie si l'utilisateur est connecté
        boolean isLoggedIn = sharedPreferences.contains("user_email");

        if (isLoggedIn) {
            textView.setText("Se déconnecter");
            btnToLogin.setImageResource(R.drawable.deconnexion);
        } else {
            textView.setText("Se connecter");
            btnToLogin.setImageResource(R.drawable.connexion);
        }

        btnToLogin.setOnClickListener(v -> {
            boolean loggedIn = sharedPreferences.contains("user_email");

            if (loggedIn) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(OrganizeActivity.this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OrganizeActivity.this, LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(OrganizeActivity.this, LoginActivity.class));
            }
        });

        // Picker pour la date
        datePicker.setOnClickListener(v -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Sélectionner la date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection));
                textDate.setHint("      Date sélectionnée : " + date);
            });

            materialDatePicker.show(getSupportFragmentManager(), "tag");
        });

        // Picker pour la localisation
        locationPicker.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizeActivity.this, MapLocationActivity.class);
            startActivityForResult(intent, LOCATION_REQUEST_CODE);
        });
    }

    // Récupère la localisation sélectionnée depuis MapLocationActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedLocation = data.getStringExtra("selected_location");
            if (selectedLocation != null) {
                textLocation.setHint("      Lieu : " + selectedLocation);
            }
        }
    }
}
