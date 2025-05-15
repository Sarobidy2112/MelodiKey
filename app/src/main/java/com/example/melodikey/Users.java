package com.example.melodikey;

public class Users {

    private int id;
    private String username;
    private String email;
    private String mdp;
    private int verification_code;
    private int verified;

    public Users(int id, String username, String email, String mdp, int verification_code, int verified) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.mdp = mdp;
        this.verified = verified;
    }

    public int getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(int verification_code) {
        this.verification_code = verification_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }
}
