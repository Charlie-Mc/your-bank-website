package uk.co.asepstrath.bank.models;

import java.security.MessageDigest;

public class User {
    private String name;
    private String password;
    private int balance;

    public User(String name, String password, int balance) {
        this.name = name;
        this.password = password;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }
    public String getPassword() {
        // Return password encrypted in SHA-256
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(password.getBytes()).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public int getBalance() {
        return balance;
    }
}
