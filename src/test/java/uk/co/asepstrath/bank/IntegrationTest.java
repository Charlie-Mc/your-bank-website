package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import io.jooby.MockRouter;
import io.jooby.StatusCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
class IntegrationTest {
    /*
    Integration tests should be here
    Example can be found in example/IntegrationTest.java
     */
    /**
     * Test the welcome route.
     * @since 1.0.0
     */
    @Test
    void test_home_200() {
        MockRouter router = new MockRouter(new App());
        router.get("/", rsp -> assertEquals(StatusCode.OK, rsp.getStatusCode()));
    }

    static OkHttpClient client = new OkHttpClient();

    @Test
    public void shouldDisplayAccountInformation(int serverPort) {
        // creates new instance of the webpage
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort + "/accounts")
                .build();

        try (Response rsp = client.newCall(req).execute()) {

            // gets a copy of the running html code
            assert rsp.body() != null;
            String responseBody = rsp.body().string();
            // defines what sections of it I actually want
            String expectedPattern = "<div class=\"account\">\\s*<h2>([a-zA-Z]+)</h2>\\s*<p>([0-9.]+)</p>";

            //regular expressions stuff - I do not really understand this but hey it works
            //My understanding is that it takes the responseBody and finds where it is in the expectedPattern
            Pattern pattern = Pattern.compile(expectedPattern);
            Matcher matcher = pattern.matcher(responseBody);

            // creates a array list to store the actual values we want to look at ie - returns Rachel 50 from webpage when working
            List<String> actualValues = new ArrayList<>();
            while (matcher.find()) {
                actualValues.add(matcher.group(1) + "\n" + matcher.group(2));
            }

            // saving the expected values in a list to compare actualValues
            List<String> expectedValues = Arrays.asList(
                    "Rachel\n50.00",
                    "Monica\n100.00",
                    "Phoebe\n76.00",
                    "Joey\n23.90",
                    "Chandler\n3.00",
                    "Ross\n54.32");

            assertEquals(expectedValues, actualValues);
            assertEquals(StatusCode.OK.value(), rsp.code());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_accounts_json(int serverPort) {
        // creates new instance of the webpage
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort + "/accounts?format=json")
                .build();

        try (Response rsp = client.newCall(req).execute()) {
            assert rsp.body() != null;
            String responseBody = rsp.body().string();

            // It should just be JSON that is returned (Balance first then name)
            String expectedPattern = "\\{\"balance\":([0-9.]+),\"name\":\"([a-zA-Z]+)\"}";
            Pattern pattern = Pattern.compile(expectedPattern);
            Matcher matcher = pattern.matcher(responseBody);

            // creates a array list to store the actual values we want to look at ie - returns Rachel 50 from webpage when working
            List<String> actualValues = new ArrayList<>();
            while (matcher.find()) {
                actualValues.add(matcher.group(1) + "\n" + matcher.group(2));
            }

            // saving the expected values in a list to compare actualValues
            List<String> expectedValues = Arrays.asList(
                    "50.00\nRachel",
                    "100.00\nMonica",
                    "76.00\nPhoebe",
                    "23.90\nJoey",
                    "3.00\nChandler",
                    "54.32\nRoss");

            assertEquals(expectedValues, actualValues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
