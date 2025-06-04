package com.example.melodikey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrganizeActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 100;
    private DbConnect dbConnect;
    private Spinner spinnerEvenements;
    private Button confirmBtn;
    private String selectedDate;
    private String selectedLocation;
    private int selectedEvenementId = -1;

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

        // Initialisation de la base de données
        dbConnect = new DbConnect(this);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        // Initialisation des vues
        textView = findViewById(R.id.textView);
        btnToLogin = findViewById(R.id.btnToLogin);
        textDate = findViewById(R.id.textDate);
        textLocation = findViewById(R.id.textLocation);
        datePicker = findViewById(R.id.datePicker);
        locationPicker = findViewById(R.id.locationPicker);
        spinnerEvenements = findViewById(R.id.spinner_evenements);
        confirmBtn = findViewById(R.id.ConfirmBtn);

        // Charger les événements depuis la base de données
        loadEvenementsFromDatabase();

        // Vérifier si l'utilisateur est connecté
        checkUserLoginStatus();

        // Gestion des clics
        setupClickListeners();
    }

    private void loadEvenementsFromDatabase() {
        System.out.println("Chargement des événements depuis la base de données...");

        // Récupérer tous les événements depuis la base de données
        List<Evenements> evenementsList = dbConnect.getAllEvenements();

        if (evenementsList.isEmpty()) {
            System.out.println("Aucun événement trouvé dans la base de données");
            Toast.makeText(this, "Aucun événement disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("Nombre d'événements chargés: " + evenementsList.size());

        // Créer un tableau des noms d'événements
        String[] evenementsNames = new String[evenementsList.size()];
        for (int i = 0; i < evenementsList.size(); i++) {
            evenementsNames[i] = evenementsList.get(i).getNomEvenement();
        }

        // Créer et configurer l'adapter
        ArrayAdapter<String> adapterEvenement = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                evenementsNames
        );
        adapterEvenement.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerEvenements.setAdapter(adapterEvenement);

        // Listener pour la sélection d'événement
        spinnerEvenements.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEvenementId = position + 1; // Les IDs commencent généralement à 1
                String evenement = parent.getItemAtPosition(position).toString();
                System.out.println("Événement sélectionné: " + evenement + " (ID: " + selectedEvenementId + ")");
                Toast.makeText(OrganizeActivity.this, "Événement sélectionné: " + evenement, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("Aucun événement sélectionné");
            }
        });
    }

    private void checkUserLoginStatus() {
        boolean isLoggedIn = sharedPreferences.contains("user_email");

        if (isLoggedIn) {
            textView.setText("Se déconnecter");
            btnToLogin.setImageResource(R.drawable.deconnexion);
            System.out.println("Utilisateur connecté");
        } else {
            textView.setText("Se connecter");
            btnToLogin.setImageResource(R.drawable.connexion);
            System.out.println("Utilisateur non connecté");
        }
    }

    private void setupClickListeners() {
        btnToLogin.setOnClickListener(v -> {
            boolean loggedIn = sharedPreferences.contains("user_email");

            if (loggedIn) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(OrganizeActivity.this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show();
                System.out.println("Utilisateur déconnecté");
                startActivity(new Intent(OrganizeActivity.this, LoginActivity.class));
                finish();
            } else {
                System.out.println("Redirection vers l'écran de connexion");
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
                selectedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection));
                textDate.setHint("      Date sélectionnée : " + selectedDate);
                System.out.println("Date sélectionnée: " + selectedDate);
                Toast.makeText(OrganizeActivity.this, "Date: " + selectedDate, Toast.LENGTH_SHORT).show();
            });

            materialDatePicker.show(getSupportFragmentManager(), "tag");
        });

        // Picker pour la localisation
        locationPicker.setOnClickListener(v -> {
            System.out.println("Ouverture de la carte pour sélectionner la localisation");
            Intent intent = new Intent(OrganizeActivity.this, MapLocationActivity.class);
            startActivityForResult(intent, LOCATION_REQUEST_CODE);
        });

        // Bouton de confirmation
        confirmBtn.setOnClickListener(v -> {
            System.out.println("Bouton de confirmation cliqué");

            // Vérifier que tous les champs sont remplis
            if (selectedDate == null || selectedDate.isEmpty()) {
                System.out.println("Erreur: Date non sélectionnée");
                Toast.makeText(this, "Veuillez sélectionner une date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedLocation == null || selectedLocation.isEmpty()) {
                System.out.println("Erreur: Lieu non sélectionné");
                Toast.makeText(this, "Veuillez sélectionner un lieu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedEvenementId == -1) {
                System.out.println("Erreur: Événement non sélectionné");
                Toast.makeText(this, "Veuillez sélectionner un événement", Toast.LENGTH_SHORT).show();
                return;
            }

            System.out.println("Tous les champs sont valides - Envoi du formulaire");
            System.out.println("Date: " + selectedDate);
            System.out.println("Lieu: " + selectedLocation);
            System.out.println("ID Événement: " + selectedEvenementId);

            // Créer un nouveau répertoire
            Repertoires nouveauRepertoire = new Repertoires(selectedDate, selectedLocation, selectedEvenementId);
            long result = dbConnect.addRepertoire(nouveauRepertoire);

            if (result != -1) {
                System.out.println("Répertoire ajouté avec succès, ID: " + result);
                Toast.makeText(this, "Événement organisé avec succès!", Toast.LENGTH_SHORT).show();

                // Réinitialiser le formulaire
                resetForm();
            } else {
                System.out.println("Erreur lors de l'ajout du répertoire");
                Toast.makeText(this, "Erreur lors de l'organisation de l'événement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        selectedDate = null;
        selectedLocation = null;
        selectedEvenementId = -1;
        textDate.setHint(R.string.select_date);
        textLocation.setText("");
        spinnerEvenements.setSelection(0);
        System.out.println("Formulaire réinitialisé");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedLocation = data.getStringExtra("selected_location");
            if (selectedLocation != null) {
                textLocation.setText("      Lieu : " + selectedLocation);
                System.out.println("Lieu sélectionné: " + selectedLocation);
                Toast.makeText(this, "Lieu: " + selectedLocation, Toast.LENGTH_SHORT).show();
            }
        } else {
            System.out.println("Aucun lieu sélectionné ou erreur lors de la sélection");
        }
    }
}