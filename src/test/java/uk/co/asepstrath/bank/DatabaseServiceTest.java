package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.services.DatabaseService;
import uk.co.asepstrath.bank.models.Account;

@JoobyTest(App.class)
class DatabaseServiceTest {
    private DatabaseService db;

    @Test
    void testSelectAll() {
        db.selectAll(Account.class, "accounts");
    }

}