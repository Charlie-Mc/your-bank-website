package uk.co.asepstrath.bank.models;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction {
    private String id;
    private String withdrawAccount;
    private String depositAccount;
    private BigDecimal priorBalance;
    private BigDecimal postBalance;
    private Date date;
    private BigDecimal amount;
    private String currency;

    // Static Variables
    public static final ArrayList<Transaction> transactions = new ArrayList<>();


    /**
     * Constructor for Transaction
     * @param id String
     * @param withdrawAccount String
     * @param depositAccount String
     * @param date Date
     * @param amount BigDecimal
     * @param currency String
     */
    public Transaction(String id, String withdrawAccount, String depositAccount, Date date, BigDecimal amount, String currency) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Constructor for Transaction
     * @param id String
     * @param withdrawAccount String
     * @param depositAccount String
     * @param date Date
     * @param amount BigDecimal
     */
    public Transaction(String id, String withdrawAccount, String depositAccount, Date date, BigDecimal amount) {
        this(id, withdrawAccount, depositAccount, date, amount, "GBP");
    }

    /**
     * Get the transaction ID
     * @return String
     */
    public String getId() {
        return id;
    }

    public Transaction() {}

    /**
     * Get the withdrawal account
     * @return String
     */
    public String getWithdrawAccount() {
        return withdrawAccount;
    }

    /**
     * Get the deposit account
     * @return String
     */
    public String getDepositAccount() {
        return depositAccount;
    }

    /**
     * Get the date
     * @return Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Get the amount
     * @return BigDecimal
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Get the currency
     * @return String
     */
    public String getCurrency() {
        return currency;
    }

    /*
     * Get the prior balance
     * @return BigDecimal
     */
    public BigDecimal getPriorBalance() {
        return priorBalance;
    }

    /*
     * Get the post balance
     * @return BigDecimal
     */
    public BigDecimal getPostBalance() {
        return postBalance;
    }

    // Static Methods
    /**
     * Get all transactions
     * @return ArrayList<Transaction>
     */
    public static List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Get a transaction by ID
     * @param transactionID String
     * @return Transaction
     */
    public static Transaction getTransaction(String transactionID) {
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(transactionID)) {
                return transaction;
            }
        }
        return null;
    }

    /**
     * Get all withdrawal transactions by account
     * @param accountID String
     * @return ArrayList<Transaction>
     */
    public static ArrayList<Transaction> getWithdrawalsByAccount(String accountID) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : Transaction.getTransactions()) {
            if (transaction.getWithdrawAccount().equals(accountID) || transaction.getDepositAccount().equals(accountID)) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    /**
     * Get all deposit transactions by account
     * @param accountID String
     * @return ArrayList<Transaction>
     */
    public static ArrayList<Transaction> getDepositsByAccount(String accountID) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : Transaction.getTransactions()) {
            if (transaction.getDepositAccount().equals(accountID) || transaction.getWithdrawAccount().equals(accountID)) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    /**
     * Get all transactions by account
     * @param accountID String
     * @return ArrayList<Transaction>
     */
    public static ArrayList<Transaction> getTransactionsByAccount(String accountID) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : Transaction.getTransactions()) {
            if (transaction.getDepositAccount().equals(accountID) || transaction.getWithdrawAccount().equals(accountID)) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public Account findWithdrawAcc(ArrayList<Account> accounts) {
        Account withdrawAcc = null;
        for (Account account : accounts) {
            if (account.getId().equals(this.getWithdrawAccount())) {
                withdrawAcc = account;
            }
        }
        return withdrawAcc;
    }

    public Account findDepositAcc(ArrayList<Account> accounts) {
        Account depositAcc = null;
        for (Account account : accounts) {
            if (account.getId().equals(this.getDepositAccount())) {
                depositAcc = account;
            }
        }
        return depositAcc;
    }
    public boolean doTransaction(ArrayList<Account> accounts) {
        Account withdrawAcc = findWithdrawAcc(accounts);
        Account depositAcc = findDepositAcc(accounts);

        if (withdrawAcc == null || depositAcc == null) {
            return false;
        }
        this.priorBalance = withdrawAcc.getBalance();
        this.postBalance = withdrawAcc.getBalance().subtract(this.amount);
        try {
            withdrawAcc.withdraw(this.amount);
            depositAcc.deposit(this.amount);
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    public boolean reverseTransaction(ArrayList<Account> accounts) {
        Account withdrawAcc = findWithdrawAcc(accounts);
        Account depositAcc = findDepositAcc(accounts);

        if (withdrawAcc == null || depositAcc == null) {
            return false;
        }
        this.priorBalance = depositAcc.getBalance();
        this.postBalance = depositAcc.getBalance().subtract(this.amount);
        try {
            depositAcc.withdraw(this.amount);
            withdrawAcc.deposit(this.amount);
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    public boolean requestReversal(ArrayList<Account> accounts) throws IOException {
        URL url = new URL("http://api.asep-strath.co.uk/api/bank2/reversal");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        JsonObject payload = new JsonObject();
        payload.addProperty("id", this.getId());

        OutputStream os = con.getOutputStream();
        os.write(payload.toString().getBytes());
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            reverseTransaction(accounts);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Transaction{amount=" + amount + ", currency=" + currency + ", date=" + date + ", depositAccount="
                + depositAccount + ", id=" + id + ", priorBalance=" + priorBalance + ", postBalance=" + postBalance
                + ", withdrawAccount=" + withdrawAccount + "}";
    }
}
