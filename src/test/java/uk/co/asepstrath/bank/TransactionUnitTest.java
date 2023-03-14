package uk.co.asepstrath.bank;


import org.junit.jupiter.api.Test;

import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Transaction;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TransactionUnitTest {
    @Test
    void testTransaction() {
        Transaction transaction = new Transaction("100",
                "Test",
                "Test2",
                new Date(),
                new BigDecimal("100.00"),
                "CWP"
                );

        assert(transaction.getWithdrawAccount().equals("Test"));
    }

    @Test
    void test_get_transactions() {
        Account account = new Account(
                "101",
                "Test",
                new BigDecimal("100.00"),
                "CWP",
                "Savings"
                );
        Account account2 = new Account(
                "102",
                "Test2",
                new BigDecimal("100.00"),
                "CWP",
                "Savings"
                );

        Transaction transaction = new Transaction("100",
                "102",
                "101",
                new Date(),
                new BigDecimal("100.00"),
                "CWP"
                );
        Transaction transaction2 = new Transaction("101",
                "102",
                "101",
                new Date(),
                new BigDecimal("100.00"),
                "CWP"
                );
        Transaction.transactions.add(transaction);
        Transaction.transactions.add(transaction2);
        assertEquals(2, Transaction.getTransactionsByAccount("102").size());
    }

    @Test
    void test_do_transaction() {
        Account account = new Account(
                "100",
                "Test",
                new BigDecimal("100.00"),
                "CWP",
                "Savings"
                );
        Account account2 = new Account(
                "101",
                "Test2",
                new BigDecimal("100.00"),
                "CWP",
                "Savings"
                );
        Transaction transaction = new Transaction(
                "100",
                "101",
                "100",
                new Date(),
                new BigDecimal("20.00"),
                "CWP"
                );
        Transaction transaction2 = new Transaction(
                "101",
                "101",
                "100",
                new Date(),
                new BigDecimal("20.00"),
                "CWP"
                );

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(account);
        accounts.add(account2);
        boolean done = transaction2.doTransaction(accounts);
        assertTrue(done);
    }

    @Test
    void test_failed_transaction() {
        Account account = new Account(
                "100",
                "Test",
                new BigDecimal("100.00"),
                "CWP",
                "Savings"
        );
        Account account2 = new Account(
                "101",
                "Test2",
                new BigDecimal("100.00"),
                "CWP",
                "Savings"
        );
        Transaction transaction = new Transaction(
                "100",
                "101",
                "100",
                new Date(),
                new BigDecimal("120.00"),
                "CWP"
        );

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(account);
        accounts.add(account2);
        boolean done = transaction.doTransaction(accounts);
        assertFalse(done);
    }

}
