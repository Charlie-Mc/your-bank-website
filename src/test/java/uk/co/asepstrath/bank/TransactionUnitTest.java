package uk.co.asepstrath.bank;


import org.junit.jupiter.api.Test;

import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Transaction;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


public class TransactionUnitTest {
    @Test
    public void testTransaction() {
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
    public void test_get_transactions() {
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
    public void test_do_transaction() {
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
    public void test_failed_transaction() {
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

    @Test
    public void test_reverse_transaction() {
        Account account = new Account(
                "1",
                "Test",
                new BigDecimal("150.00"),
                "CWP",
                "Savings"
        );
        Account account2 = new Account(
                "2",
                "Test2",
                new BigDecimal("160.00"),
                "CWP",
                "Savings"
        );
        Transaction transaction = new Transaction(
                "100",
                "2",
                "1",
                new Date(),
                new BigDecimal("50.00"),
                "CWP"
        );

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(account);
        accounts.add(account2);
        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balance2Before = account2.getBalance();

        boolean doTransaction = transaction.doTransaction(accounts);
        assertNotEquals(balanceBefore, account.getBalance());
        assertNotEquals(balance2Before, account2.getBalance());

        if (doTransaction) {
            transaction.reverseTransaction(accounts);
        }
        assertEquals(balanceBefore, account.getBalance());
        assertEquals(balance2Before, account2.getBalance());
    }

    @Test
    public void test_reverse_transaction_2() {
        Account account = new Account(
                "1",
                "Test",
                new BigDecimal("100.00"),
                "CWP",
                "Savings"
        );
        Account account2 = new Account(
                "2",
                "Test2",
                new BigDecimal("200.00"),
                "CWP",
                "Savings"
        );
        Transaction transaction = new Transaction(
                "100",
                "2",
                "1",
                new Date(),
                new BigDecimal("20.00"),
                "CWP"
        );
        Transaction transaction2 = new Transaction(
                "101",
                "1",
                "2",
                new Date(),
                new BigDecimal("50.00"),
                "CWP"
        );

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(account);
        accounts.add(account2);
        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balance2Before = account2.getBalance();

        boolean doTransaction = transaction.doTransaction(accounts);
        assertNotEquals(balanceBefore, account.getBalance());
        assertNotEquals(balance2Before, account2.getBalance());

        boolean doTransaction2 = transaction2.doTransaction(accounts);
        assertNotEquals(balanceBefore, account.getBalance());
        assertNotEquals(balance2Before, account2.getBalance());

        if (doTransaction) {
            transaction.reverseTransaction(accounts);
        }
        assertNotEquals(balanceBefore, account.getBalance());
        assertNotEquals(balance2Before, account2.getBalance());

        if (doTransaction2) {
            transaction2.reverseTransaction(accounts);
        }
        assertEquals(balanceBefore, account.getBalance());
        assertEquals(balance2Before, account2.getBalance());
    }
}
