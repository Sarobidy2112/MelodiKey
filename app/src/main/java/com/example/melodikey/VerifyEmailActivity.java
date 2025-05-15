package com.example.melodikey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VerifyEmailActivity extends AppCompatActivity {

    EditText editTextCode;
    Button btnVerify;
    DbConnect dbConnect;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        editTextCode = findViewById(R.id.editTextCode);
        btnVerify = findViewById(R.id.btnVerify);
        dbConnect = new DbConnect(this);

        // Récupérer l'email depuis l'intent
        userEmail = getIntent().getStringExtra("email");

        btnVerify.setOnClickListener(view -> {
            String enteredCode = editTextCode.getText().toString();
            String storedCode = dbConnect.getVerificationCode(userEmail);

            if (enteredCode.equals(storedCode)) {
                dbConnect.markUserAsVerified(userEmail);
                Toast.makeText(VerifyEmailActivity.this, "E-mail vérifié avec succès !", Toast.LENGTH_SHORT).show();

                // Redirection vers la page de connexion
                Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(VerifyEmailActivity.this, "Code incorrect. Vérifiez votre e-mail.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}