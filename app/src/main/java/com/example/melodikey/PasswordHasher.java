package com.example.melodikey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            // Créez un objet MessageDigest pour SHA-256
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // Hash le mot de passe
            byte[] hashedBytes = messageDigest.digest(password.getBytes());

            // Convertir les bytes en une chaîne hexadécimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString(); // Retourne le mot de passe hashé
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Retourne null en cas d'erreur
        }
    }

}
