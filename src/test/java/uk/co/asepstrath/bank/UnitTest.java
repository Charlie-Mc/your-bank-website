package uk.co.asepstrath.bank;

import io.jooby.MockRouter;
import io.jooby.StatusCode;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UnitTest {
    /**
     * Test the welcome route.
     * @since 1.0.0
     */
    @Test
    void test_home_200() {
        MockRouter router = new MockRouter(new App());
        router.get("/", rsp -> assertEquals(StatusCode.OK, rsp.getStatusCode()));
    }

    @Test
    void createAccount() {
        Account a = new Account();
        assertNotNull(a);
    }

    @Test
    void newAccountValue() {
        Account a = new Account();
        assertEquals(new BigDecimal(0), a.getBalance());
    }

    @Test
    void addFunds() {
        Account a = new Account("12h","Derek",new BigDecimal("20.00"), "GDP", "Savings");
        a.deposit(new BigDecimal("50.00"));
        assertEquals(a.getBalance(), new BigDecimal("70.00"));
    }

    @Test
    void withdrawFunds() {
        Account a = new Account("13h","Derek",new BigDecimal("20.00"), "GDP", "Savings");
        assertThrows(Exception.class,() -> a.withdraw(new BigDecimal("100")));
    }

    @Test
    void superSaving() {
        Account a = new Account("12q","Derek",new BigDecimal("20.00"), "GDP", "Savings");
        for (int x = 0; x<=4; x++) {
            a.deposit(new BigDecimal("10.00"));
        }
        for (int x = 0; x<=2; x++) {
            a.withdraw(new BigDecimal("20.00"));
        }
        assertEquals(a.getBalance(), new BigDecimal("10.00"));
    }

    @Test
    void penniesCaring() {
        Account a = new Account("14h","Derek",new BigDecimal("5.45"), "GDP", "Savings");
        a.deposit(new BigDecimal("17.56"));
        assertEquals(a.getBalance(), new BigDecimal("23.01"));
    }

    @Test
    void testDoTransaction() {
        Account a = new Account("12h","Derek",new BigDecimal("20.00"), "GDP", "Savings");
        Account b = new Account("13h","Derek",new BigDecimal("20.00"), "GDP", "Savings");
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(a);
        accounts.add(b);
        Transaction t = new Transaction("1", a.getId(), b.getId(), new Date(), new BigDecimal("10.00"));
        t.doTransaction(accounts);
        assertEquals(a.getBalance(), new BigDecimal("10.00"));
        assertEquals(b.getBalance(), new BigDecimal("30.00"));
        assertEquals(t.getPostBalance(), new BigDecimal("10.00"));
        assertEquals(t.getPriorBalance(), new BigDecimal("20.00"));
    }

    @Test
    void test_failDoTransaction() {
        Account a = new Account("12h","Derek",new BigDecimal("20.00"), "GDP", "Savings");
        Account b = new Account("13h","Derek",new BigDecimal("20.00"), "GDP", "Savings");
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(a);
        accounts.add(b);
        Transaction t = new Transaction("1", a.getId(), b.getId(), new Date(), new BigDecimal("30.00"));
        assertFalse(t.doTransaction(accounts));
        assertEquals(a.getBalance(), new BigDecimal("20.00"));
        assertEquals(b.getBalance(), new BigDecimal("20.00"));
    }
}
