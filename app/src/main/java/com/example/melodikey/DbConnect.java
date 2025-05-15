package com.example.melodikey;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.Cursor;


import androidx.annotation.Nullable;

public class DbConnect extends SQLiteOpenHelper {

    private static String dbName = "melodikeydb";
    private static String tableUsers = "users";
    private static int version = 1;

    private static String id = "id";
    private static String username = "username";
    private static String email = "email";
    private static String mdp = "mdp";
    private static String verified = "verified";
    private static String verification_code = "verification_code";

    public DbConnect(@Nullable Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + tableUsers + " (" +
                id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                username + " TEXT NOT NULL, " +
                email + " TEXT NOT NULL UNIQUE, " +
                mdp + " TEXT NOT NULL, " +
                verification_code + " INTEGER ," +
                verified + " INTEGER DEFAULT 0 " +
                ");";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableUsers);
        onCreate(sqLiteDatabase);
    }

    public boolean checkUserExists(String email, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + tableUsers + " WHERE " + this.email + " = ? OR " + this.username + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void addUser(Users user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(username, user.getUsername());
        values.put(email, user.getEmail());
        values.put(mdp, user.getMdp());
        db.insert(tableUsers, null, values);
    }

    public boolean checkUser(String email, String password){
        String hashedPassword = PasswordHasher.hashPassword(password);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableUsers + " WHERE " + this.email + " = ? AND " + this.mdp + " = ? AND verified = 1", new String[]{email, hashedPassword});

        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    public Users getUserInfo(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, username, email FROM " + tableUsers + " WHERE " + this.email + " = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String username = cursor.getString(1);
            String userEmail = cursor.getString(2);
            cursor.close();
            return new Users(id, username, userEmail, null, 0, 0); // Retourne un objet Users avec les informations
        }
        cursor.close();
        return null; // Retourne null si l'utilisateur n'est pas trouvé
    }

    public void storeVerificationCode(String email, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("verified", 0); // L'utilisateur n'est pas encore vérifié
        values.put("verification_code", code); // Stocker le code dans la colonne dédiée

        db.update(tableUsers, values, "email = ?", new String[]{email});
        System.out.println("Code de vérification stocké pour l'e-mail : " + email);
    }

    public String getVerificationCode(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT verification_code FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String code = cursor.getString(0);
            cursor.close();
            System.out.println("Code de vérification récupéré pour l'e-mail : " + email + " -> " + code);
            return code;
        }
        return null;
    }

    public void markUserAsVerified(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("verified", 1); // L'utilisateur est maintenant vérifié
        values.put("verification_code", (String) null); // Supprimer le code après validation

        db.update(tableUsers, values, "email = ?", new String[]{email});
        System.out.println("Utilisateur vérifié et code supprimé pour l'e-mail : " + email);
    }
}