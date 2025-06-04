package com.example.melodikey;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.Cursor;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbConnect extends SQLiteOpenHelper {
    
        private static String dbName = "melodikeydb";
        private static String tableUsers = "users";
        private static String tableEvenements = "Evenements";
        private static String tableRepertoires = "repertoires";
        private static int version = 3;  // Incrémenter la version car on modifie la structure
    
        private static String id = "id";
        private static String username = "username";
        private static String email = "email";
        private static String mdp = "mdp";
        private static String verified = "verified";
        private static String verification_code = "verification_code";
        private static String nomEvenement = "nomEvenement";
        private static String date = "date";
        private static String lieu = "lieu";
        private static String evenementId = "evenement_id";
    
        public DbConnect(@Nullable Context context) {
            super(context, dbName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            // Création de la table users (existant)
            String queryUsers = "CREATE TABLE " + tableUsers + " (" +
                    id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    username + " TEXT NOT NULL, " +
                    email + " TEXT NOT NULL UNIQUE, " +
                    mdp + " TEXT NOT NULL, " +
                    verification_code + " INTEGER ," +
                    verified + " INTEGER DEFAULT 0 " +
                    ");";
            sqLiteDatabase.execSQL(queryUsers);

            // Création de la table Evenements (existant)
            String queryEvenements = "CREATE TABLE " + tableEvenements + " (" +
                    id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    nomEvenement + " TEXT NOT NULL UNIQUE" +
                    ");";
            sqLiteDatabase.execSQL(queryEvenements);

            // Insertion des données par défaut (existant)
            insererEvenementsParDefaut(sqLiteDatabase);

            // Création de la NOUVELLE table repertoires
            String queryRepertoires = "CREATE TABLE " + tableRepertoires + " (" +
                    id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    date + " TEXT NOT NULL, " +
                    lieu + " TEXT NOT NULL, " +
                    evenementId + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + evenementId + ") REFERENCES " +
                    tableEvenements + "(" + id + ")" +
                    ");";
            sqLiteDatabase.execSQL(queryRepertoires);
        }

        // Modifiez onUpgrade() pour gérer la nouvelle table
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            if (oldVersion < 2) {
                // Création de la table Evenements (existant)
                String queryEvenements = "CREATE TABLE " + tableEvenements + " (" +
                        id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        nomEvenement + " TEXT NOT NULL UNIQUE" +
                        ");";
                sqLiteDatabase.execSQL(queryEvenements);
                insererEvenementsParDefaut(sqLiteDatabase);
            }

            if (oldVersion < 3) {  // Nouvelle version pour la table repertoires
                String queryRepertoires = "CREATE TABLE " + tableRepertoires + " (" +
                        id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        date + " TEXT NOT NULL, " +
                        lieu + " TEXT NOT NULL, " +
                        evenementId + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + evenementId + ") REFERENCES " +
                        tableEvenements + "(" + id + ")" +
                        ");";
                sqLiteDatabase.execSQL(queryRepertoires);
            }
        }
    
        // Méthode pour insérer les événements par défaut
        private void insererEvenementsParDefaut(SQLiteDatabase db) {
            String[] evenements = {"Mariages", "Eglise", "Concerts", "Animations", "Autres"};
    
            for (String evenement : evenements) {
                ContentValues values = new ContentValues();
                values.put(nomEvenement, evenement);
    
                try {
                    db.insertOrThrow(tableEvenements, null, values);
                } catch (SQLiteConstraintException e) {
                    // Ignorer les doublons (grace à UNIQUE)
                }
            }
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
    
            System.out.println("-------- Dans Add User : ");
            System.out.println("Username = " + user.getUsername());
            System.out.println("Email = " + user.getEmail());
            System.out.println("Password = " + user.getMdp());
    
            db.insert(tableUsers, null, values);
        }
    
        public boolean checkUser(String email, String password){
            String hashedPassword = PasswordHasher.hashPassword(password);
    
            System.out.println("------- Check User : ");
            System.out.println("Email : " + email);
            System.out.println("Mdp : " + hashedPassword);
    
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

        // Ajouter un nouveau répertoire
        // Méthode existante à modifier
        public long addRepertoire(Repertoires repertoire) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(date, repertoire.getDate());
            values.put(lieu, repertoire.getLieu());
            values.put(evenementId, repertoire.getEvenementId());

            System.out.println("Insertion dans la base de données:");
            System.out.println("Date: " + repertoire.getDate());
            System.out.println("Lieu: " + repertoire.getLieu());
            System.out.println("ID Événement: " + repertoire.getEvenementId());

            long result = db.insert(tableRepertoires, null, values);

            if(result == -1) {
                System.out.println("Erreur lors de l'insertion");
            } else {
                System.out.println("Insertion réussie, ID: " + result);
            }

            return result;
        }


        // Récupérer tous les répertoires
        public Cursor getAllRepertoires() {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery("SELECT r.*, e." + nomEvenement + " FROM " + tableRepertoires +
                    " r INNER JOIN " + tableEvenements + " e ON r." + evenementId + " = e." + id, null);
        }


        // Mettre à jour un répertoire
        public int updateRepertoire(long id, String date, String lieu, long evenementId) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(this.date, date);
            values.put(this.lieu, lieu);
            values.put(this.evenementId, evenementId);

            return db.update(tableRepertoires, values, this.id + "=?",
                    new String[]{String.valueOf(id)});
        }

        // Supprimer un répertoire
        public int deleteRepertoire(long id) {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(tableRepertoires, this.id + "=?",
                    new String[]{String.valueOf(id)});
        }

        public List<Evenements> getAllEvenements() {
            List<Evenements> evenementsList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableEvenements, null);

            if (cursor.moveToFirst()) {
                do {
                    Evenements evenement = new Evenements(
                            cursor.getInt(0), // id
                            cursor.getString(1) // nomEvenement
                    );
                    evenementsList.add(evenement);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return evenementsList;
        }
    }