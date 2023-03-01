package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.Path;
import io.jooby.annotations.GET;
import io.jooby.annotations.PathParam;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Transaction;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/transactions")
public class TransactionController {

    private final Logger logger;
    private final DataSource dataSource;

    public TransactionController(Logger logger, DataSource dataSource) {
        this.logger = logger;
        this.dataSource = dataSource;
    }

    /**
     * Used to display all transactions
     * @return ModelAndView
     */
    @GET
    public ModelAndView transactions() {
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
            model.put("transactions", transactions);
            model.put("tCount", transactions.size());
        } catch (SQLException e) {
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Unable to connect to database", e);
        }
        logger.info("All Transactions Loaded");
        return new ModelAndView("transactionView.hbs", model);
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
        model.put("tCount", transactions.size());
        // Logic Here
        logger.info("Fraudulent Transactions Loaded");
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
        logger.info("Successful Transactions Loaded");
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
