package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.Path;
import io.jooby.annotations.GET;
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
     * Used to display successful transactions
     * @return ModelAndView
     */
    @GET
    public ModelAndView transactions() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Transactions");
        model.put("mode", "normal");
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
        } catch (SQLException e) {
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Unable to connect to database", e);
        }
        logger.info("Successful Transactions Loaded");
        return new ModelAndView("transactionView.hbs", model);
    }

    /**
     * Used to display fraudulent transactions
     * @return ModelAndView
     */
    @GET("/fraud")
    public ModelAndView fraudulentTransactions() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Fraudulent Transactions");
        model.put("mode", "fraud");
        // Logic Here
        logger.info("Fraudulent Transactions Loaded");
        return new ModelAndView("transactionView.hbs", model);
    }

    /**
     * Used to display all transactions
     * @return ModelAndView
     */
    @GET("/all")
    public ModelAndView allTransactions() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "All Transactions");
        model.put("mode", "all");
        // Logic Here
        logger.info("All Transactions Loaded");
        return new ModelAndView("transactionView.hbs", model);
    }
}
