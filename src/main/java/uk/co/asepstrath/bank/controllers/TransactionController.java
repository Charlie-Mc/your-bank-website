package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.annotations.Path;
import io.jooby.annotations.GET;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.util.HashMap;

@Path("/transactions")
public class TransactionController {

    private final Logger logger;
    private final DataSource dataSource;

    public TransactionController(Logger logger, DataSource dataSource) {
        this.logger = logger;
        this.dataSource = dataSource;
    }

    @GET
    public Object transactions() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Transactions");
        model.put("mode", "normal");
        // Logic Here
        return new ModelAndView("transactionView.hbs", model);
    }

    @GET("/fraud")
    public Object fraudulentTransactions() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Fraudulent Transactions");
        model.put("mode", "fraud");
        // Logic Here
        return new ModelAndView("transactionView.hbs", model);
    }

    @GET("/all")
    public Object allTransactions() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "All Transactions");
        model.put("mode", "all");
        // Logic Here
        return new ModelAndView("transactionView.hbs", model);
    }
}
