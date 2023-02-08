package uk.co.asepstrath.bank.models;

import java.math.BigDecimal;

public class Account {

    private BigDecimal balance;
    private String name;

    public Account() {
        this(new BigDecimal("0.00"), "Default");
    }

    public Account(BigDecimal balance, String name) {
        this.balance = balance;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) throws Exception {
        if (balance.compareTo(amount) < 0) {
            throw new Exception("Balance to low");
        }
        balance = balance.subtract(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Name: " + this.name + " Balance: " + this.balance;
    }
}