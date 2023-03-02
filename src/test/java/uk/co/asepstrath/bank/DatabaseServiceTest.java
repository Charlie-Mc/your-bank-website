package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.services.DatabaseService;
import uk.co.asepstrath.bank.models.Account;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
class DatabaseServiceTest {
    private DatabaseService db;

    @Test
    @DisplayName("Test clean() method has success")
    public void test_clean_string_success() {
        String input = "     test\n";
        String expected = "test";

        String actual = DatabaseService.clean(input);

        assertEquals(expected, actual);
    }

}