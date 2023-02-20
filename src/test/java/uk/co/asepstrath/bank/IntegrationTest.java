package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import io.jooby.StatusCode;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.models.Account;
import java.io.IOException;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
class IntegrationTest {
    static final OkHttpClient client = new OkHttpClient();

    /*
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
    */
    @Test
    public void test_accounts_json(int serverPort) {
        // creates new instance of the webpage
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort + "/accounts?format=json")
                .build();

        try (Response rsp = client.newCall(req).execute()) {
            assert rsp.body() != null;


            HttpResponse<List<Account>> accountListResponse = Unirest.get("http://localhost:" + serverPort + "/accounts?format=json").asObject(new GenericType<List<Account>>() {
            });
            List<Account> actualValues = accountListResponse.getBody();
            actualValues.sort((a, b) -> a.getName().compareTo(b.getName()));


            /*It should just be JSON that is returned (Balance first then name)
            String expectedPattern = "\\{\"name\":\"([a-zA-Z]+)\",\\{\"id\":\"([a-zA-Z]+)\",\"balance\":([0-9.]+),\"currency\":\"([a-zA-Z]+)\",\"accountType\":\"\"\"([a-zA-Z]+)\"}";
            Pattern pattern = Pattern.compile(expectedPattern);
            Matcher matcher = pattern.matcher(responseBody);

            // creates a array list to store the actual values we want to look at ie - returns Rachel 50 from webpage when working
            List<String> actualValues = new ArrayList<>();
            while (matcher.find()) {
                actualValues.add(matcher.group(1) + "\n" + matcher.group(2));
            } */

            // saving the expected values in a list to compare actualValues
            HttpResponse<List<Account>> accountListResponse2 = Unirest.get("https://api.asep-strath.co.uk/api/team12/accounts").asObject(new GenericType<List<Account>>() {
            });
            List<Account> expectedValues = accountListResponse2.getBody();
            expectedValues.sort((a, b) -> a.getName().compareTo(b.getName()));

            int i = 0;
            for(Account expected: expectedValues){
                Account actual = actualValues.get(i);
                i++;

                assertEquals(expected.getId(), actual.getId());
                assertEquals(expected.getName(), actual.getName());
                assertEquals(expected.getBalance(), actual.getBalance());
                assertEquals(expected.getCurrency(), actual.getCurrency());
                assertEquals(expected.getAccountType(), actual.getAccountType());

            }

            assertEquals(StatusCode.OK.value(), rsp.code());
            assertEquals("application/json", rsp.header("Content-Type"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


