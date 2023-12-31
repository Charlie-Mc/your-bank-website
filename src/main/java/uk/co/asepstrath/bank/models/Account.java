package uk.co.asepstrath.bank.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Account implements Serializable {
    private String id, name, currency, accountType;
    private BigDecimal balance;

    public Account(String id, String name, BigDecimal balance, String currency, String accountType) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        this.accountType = accountType;
    }

    public Account() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }


    public void withdraw(BigDecimal amount) throws ArithmeticException {
        if (balance.compareTo(amount) < 0) {
            throw new ArithmeticException("Insufficient funds");
        }

        balance = balance.subtract(amount);
    }

    // getters and setters
    public BigDecimal getBalance() {
        if (balance == null) {
            return new BigDecimal("0.00");
        }
        return balance.setScale(2, RoundingMode.HALF_UP);
    }
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "Name: " + this.name + " Balance: " + this.balance;
    }

}