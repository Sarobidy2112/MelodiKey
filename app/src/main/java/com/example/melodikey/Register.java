package com.example.melodikey;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;

public class Register extends AppCompatActivity {

    EditText email, username, password, passwordConfirm;
    Button registerButton;
    TextView signinText;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        signinText = findViewById(R.id.signinText);
        username = findViewById(R.id.editTextUsername);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPwd);
        passwordConfirm = findViewById(R.id.editTextPwdConfirme);
        registerButton = findViewById(R.id.registerButton);
        btnBack = findViewById(R.id.btnBack);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strUsername = username.getText().toString();
                String strEmail = email.getText().toString();
                String strPassword = password.getText().toString();
                String strPasswordConfirm = passwordConfirm.getText().toString();

                DbConnect dbConnect = new DbConnect(Register.this);

                if (strUsername.isEmpty() || strEmail.isEmpty() || strPassword.isEmpty() || strPasswordConfirm.isEmpty()) {
                    Toast.makeText(Register.this, "Register Failed !", Toast.LENGTH_SHORT).show();
                } else if (strPassword.equals(strPasswordConfirm)) {
                    if (dbConnect.checkUserExists(strEmail, strUsername)) {
                        Toast.makeText(Register.this, "Register Failed! Email or Username already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        String hashedPassword = PasswordHasher.hashPassword(strPassword);
                        Users user = new Users(0, strUsername, strEmail, hashedPassword,0, 0);
                        dbConnect.addUser(user);

                        String verificationCode = generateVerificationCode();
                        System.out.println("Code de verification Generer : " + verificationCode);
                        dbConnect.storeVerificationCode(strEmail, verificationCode);

                        MailSender mailSender = new MailSender();
                        mailSender.sendVerificationEmail(strEmail, verificationCode);
                        System.out.println("Mail envoyer");



                        Toast.makeText(Register.this, "Register Successful !", Toast.LENGTH_SHORT).show();

                        // Rediriger vers la page de vérification
                        Intent intent = new Intent(Register.this, VerifyEmailActivity.class);
                        intent.putExtra("email", strEmail);
                        startActivity(intent);
                        System.out.println("Redirection vers VerifyEmailActivity"); // Log pour déboguer
                    }
                } else {
                    Toast.makeText(Register.this, "Register Failed! Password doesn't match.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public String generateVerificationCode() {
        int randomCode = (int) (Math.random() * 9000) + 1000; // Génère un code à 4 chiffres
        return String.valueOf(randomCode);
    }
}