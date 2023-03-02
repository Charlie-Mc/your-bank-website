package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import io.jooby.StatusCode;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.controllers.TransactionController;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.services.DatabaseService;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
    }
}
