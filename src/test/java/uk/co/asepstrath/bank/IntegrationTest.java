package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@JoobyTest(App.class)
class IntegrationTest {

    private OkHttpClient client;

    @BeforeEach
    void setup() {
        client = new OkHttpClient();
    }

    @Test
    @DisplayName("Homepage 2OO Test")
    void testHomepage() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + 8911 + "/")
                .build();

        Response response = client.newCall(request).execute();

        assertEquals(200, response.code());
    }

    @Test
    @DisplayName("Accounts 2OO Test")
    void testAccounts() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + 8911 + "/accounts")
                .build();

        Response response = client.newCall(request).execute();

        assertEquals(200, response.code());
    }

    @Test
    @DisplayName("Transactions 2OO Test")
    void testTransactions() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + 8911 + "/transactions")
                .build();

        Response response = client.newCall(request).execute();

        assertEquals(200, response.code());
    }

    @Test
    @DisplayName("Fraud 2OO Test")
    void testTransactionsfraud() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + 8911 + "/transactions/fraud")
                .build();

        Response response = client.newCall(request).execute();

        assertEquals(200, response.code());
    }

    @Test
    @DisplayName("Successful Transaction 2OO Test")
    void testTransactionsSuccess() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + 8911 + "/transactions/successful")
                .build();

        Response response = client.newCall(request).execute();

        assertEquals(200, response.code());
    }

    @Test
    @DisplayName("Successful Search 2OO Test")
    void test_account_search() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + 8911 + "/accounts/search?name=am")
                .build();

        Response response = client.newCall(request).execute();

        assertEquals(200, response.code());
    }
}
