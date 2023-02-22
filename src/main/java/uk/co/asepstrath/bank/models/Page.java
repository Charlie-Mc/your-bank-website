package uk.co.asepstrath.bank.models;

import java.util.ArrayList;

public class Page {
    public ArrayList<Account> accounts;
    public int number;
    public int count;

    public boolean isCurrent;
    public Page(ArrayList<Account> accounts, int number) {
        this.accounts = accounts;
        this.number = number;
        this.count = accounts.size();
    }
    public Page(int number) {
        this.accounts = new ArrayList<>();
        this.number = number;
        this.count = 0;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
        this.count = accounts.size();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean getCurrent() {
        return this.isCurrent;
    }

    public void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    @Override
    public String toString() {
        return "Page{" +
                "accounts=" + accounts +
                ", number=" + number +
                '}';
    }
}
