package uk.co.asepstrath.bank.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Transaction {
    private String transactionID;
    private String withdrawAccount;
    private String depositAccount;
    private BigDecimal priorBalance;
    private BigDecimal postBalance;
    private Date date;
    private BigDecimal amount;
    private String currency = null;

    // Static Variables
    public static ArrayList<Transaction> transactions = new ArrayList<Transaction>();


    /**
     * Constructor for Transaction
     * @param transactionID String
     * @param withdrawAccount String
     * @param depositAccount String
     * @param date Date
     * @param amount BigDecimal
     * @param currency String
     */
    public Transaction(String transactionID, String withdrawAccount, String depositAccount, Date date, BigDecimal amount, String currency) {
        this.transactionID = transactionID;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Constructor for Transaction
     * @param transactionID String
     * @param withdrawAccount String
     * @param depositAccount String
     * @param date Date
     * @param amount BigDecimal
     */
    public Transaction(String transactionID, String withdrawAccount, String depositAccount, Date date, BigDecimal amount) {
        this.transactionID = transactionID;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.date = date;
        this.amount = amount;
    }

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
     * Get the transaction ID
     * @return String
     */
    public String getTransactionID() {
        return transactionID;
    }

    public BigDecimal getPriorBalance() {
        return priorBalance;
    }

    public BigDecimal getPostBalance() {
        return postBalance;
    }

    // Static Methods
    /**
     * Get all transactions
     * @return ArrayList<Transaction>
     */
    public static ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Get a transaction by ID
     * @param transactionID String
     * @return Transaction
     */
    public static Transaction getTransaction(String transactionID) {
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionID().equals(transactionID)) {
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
         ArrayList<Transaction> transactions = new ArrayList<Transaction>();
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
         ArrayList<Transaction> transactions = new ArrayList<Transaction>();
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
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            for (Transaction transaction : Transaction.getTransactions()) {
                if (transaction.getDepositAccount().equals(accountID) || transaction.getWithdrawAccount().equals(accountID)) {
                    transactions.add(transaction);
                }
            }
            return transactions;
        }

        public boolean doTransaction(ArrayList<Account> accounts) {
            Account withdrawAcc = null;
            Account depositAcc = null;
            for (Account account : accounts) {
                if (account.getId().equals(this.getWithdrawAccount())) {
                    withdrawAcc = account;
                }
                if (account.getId().equals(this.getDepositAccount())) {
                    depositAcc = account;
                }
            }
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
}
