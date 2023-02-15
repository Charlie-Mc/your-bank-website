package uk.co.asepstrath.bank;

import io.jooby.MockRouter;
import io.jooby.StatusCode;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.models.Account;

import java.math.BigDecimal;

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
    public void createAccount() {
        Account a = new Account();
        assertNotNull(a);
    }

    @Test
    public void newAccountValue() {
        Account a = new Account();
        assertEquals(a.getBalance(), new BigDecimal("0.00"));
    }

    @Test
    public void addFunds() {
        Account a = new Account(new BigDecimal("20.00"), "Derek");
        a.deposit(new BigDecimal("50.00"));
        assertEquals(a.getBalance(), new BigDecimal("70.00"));
    }

    @Test
    public void withdrawFunds() {
        Account a = new Account(new BigDecimal("30.00"), "Derek");
        assertThrows(Exception.class,() -> a.withdraw(new BigDecimal("100")));
    }

    @Test
    public void superSaving() {
        Account a = new Account(new BigDecimal("20.00"), "Derek");
        for (int x = 0; x<=4; x++) {
            a.deposit(new BigDecimal("10.00"));
        }
        for (int x = 0; x<=2; x++) {
            a.withdraw(new BigDecimal("20.00"));
        }
        assertEquals(a.getBalance(), new BigDecimal("10.00"));
    }

    @Test
    public void penniesCaring() {
        Account a = new Account(new BigDecimal("5.45"), "Derek");
        a.deposit(new BigDecimal("17.56"));
        assertEquals(a.getBalance(), new BigDecimal("23.01"));
    }

}
