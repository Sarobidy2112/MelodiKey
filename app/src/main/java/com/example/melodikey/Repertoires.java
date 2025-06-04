package com.example.melodikey;

public class Repertoires {
    private int id;
    private String date;
    private String lieu;
    private int evenementId; // Clé étrangère vers la table Evenements
    private String nomEvenement; // Optionnel: pour stocker le nom de l'événement si besoin

    // Constructeur complet
    public Repertoires(int id, String date, String lieu, int evenementId) {
        this.id = id;
        this.date = date;
        this.lieu = lieu;
        this.evenementId = evenementId;
    }

    // Constructeur sans l'id (utile pour les insertions)
    public Repertoires(String date, String lieu, int evenementId) {
        this.date = date;
        this.lieu = lieu;
        this.evenementId = evenementId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }

    // Optionnel: si vous voulez aussi stocker le nom de l'événement
    public String getNomEvenement() {
        return nomEvenement;
    }

    public void setNomEvenement(String nomEvenement) {
        this.nomEvenement = nomEvenement;
    }
}