package uk.co.asepstrath.bank.models;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("unused")
public class Account implements Serializable {

    private BigDecimal balance;
    private String name;
    private String currency;
    private String accountType;

    private String id;

    public Account() {
    }

    public Account(String id, String name, BigDecimal balance, String currency, String accountType) {
        this.balance = balance;
        this.name = name;
        this.currency = currency;
        this.accountType = accountType;
        this.id = id;
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
        return balance.setScale(2, BigDecimal.ROUND_HALF_UP);
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

    public String getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Name: " + this.name + " Balance: " + this.balance;
    }


}