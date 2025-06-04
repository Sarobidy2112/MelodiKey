package com.example.melodikey;

public class Evenements {
    private int id;
    private String nomEvenement;

    public Evenements(int id, String nomEvenement) {
        this.id = id;
        this.nomEvenement = nomEvenement;
    }

    // Getters et Setters
    public int getId() { return id; }
    public String getNomEvenement() { return nomEvenement; }
    public void setId(int id) { this.id = id; }
    public void setNomEvenement(String nomEvenement) { this.nomEvenement = nomEvenement; }
}