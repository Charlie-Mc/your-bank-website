package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.Path;
import io.jooby.annotations.GET;
import io.jooby.annotations.PathParam;
import io.jooby.annotations.QueryParam;
import io.jooby.exception.StatusCodeException;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Page;
import uk.co.asepstrath.bank.models.Transaction;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Path("/transactions")
public class TransactionController {

    private final DataSource dataSource;
    private final Logger logger;

    public TransactionController(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    /**
     * Used to display all transactions
     * @return ModelAndView
     */
    @GET
    public ModelAndView transactions(@QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Transactions");
        model.put("all", "all");
        ArrayList<Transaction> transactions = new ArrayList<>();
        // Logic Here
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                transactions.add(new Transaction(
                        resultSet.getString("id"),
                        resultSet.getString("fromAccount"),
                        resultSet.getString("toAccount"),
                        resultSet.getDate("date"),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getString("currency")
                ));
            }
            ArrayList<Page> pages = new ArrayList<>();
            int count = 0;
            for (Transaction a : transactions) {
                count++;
                if (count % 100 == 0) {
                    // Add page to the model
                    Page p = new Page(new ArrayList<>(transactions.subList(count - 100, count)), count / 100);
                    pages.add(p);
                    model.put("pages", pages);
                }
            }
            if (page != null) {
                // If the page is not null, return the accounts for that page
                model.put("transactions", pages.get(page - 1).getObjects());
                model.put("tCount", pages.get(page - 1).getObjects().size());
                pages.get(page - 1).setCurrent(true);
                model.put("tPageMode", true);
                // Set the active page;
                return new ModelAndView("transactionView.hbs", model);
            }
            model.put("transactions", pages.get(0).getObjects());
            pages.get(0).setCurrent(true);
            model.put("tCount", pages.get(0).getObjects().size());
            model.put("tPageMode", true);
            return new ModelAndView("transactionView.hbs", model);
        } catch (SQLException e) {
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Unable to connect to database", e);
        }
    }

    /**
     * Used to display fraudulent transactions
     * @return ModelAndView
     */
    @GET("/fraud")
    public ModelAndView fraudulentTransactions() {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions = new ArrayList<>();
        model.put("title", "Fraudulent Transactions");
        model.put("fraud", "fraud");
        String url = "https://api.asep-strath.co.uk/api/team2/fraud";
        HttpResponse<List<String>> FraudListResponse = Unirest.get(url).asObject(new GenericType<List<String>>() {});
        List<String> fraudList = FraudListResponse.getBody();
        logger.info("Fraud List: " + fraudList.toString());
        model.put("tCount", transactions.size());
        return new ModelAndView("transactionView.hbs", model);
    }

    /**
     * Used to display non fraudulent transactions
     * @return ModelAndView
     */
    @GET("/successful")
    public ModelAndView successfulTransactions() {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions = new ArrayList<>();

        model.put("title", "Successful Transactions");
        model.put("normal", "normal");
        model.put("tCount", transactions.size());
        // Logic Here
        logger.info("Successful Transactions Loaded" );
        return new ModelAndView("transactionView.hbs", model);
    }

    @GET("/account/withdrawal/{user}")
    public ModelAndView accountWithdrawalTransactions(@PathParam String user) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Account Transactions");
        model.put("accountWithdrawal", "account");
        // Logic Here
        logger.info("Account Transactions Loaded");
        return new ModelAndView("transactionView.hbs", model);
    }

    @GET("/account/deposit/{user}")
    public ModelAndView accountDepositTransactions(@PathParam String user) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Account Transactions");
        model.put("accountDeposit", "account");
        // Logic Here
        logger.info("Account Transactions Loaded");
        return new ModelAndView("transactionView.hbs", model);
    }
}
