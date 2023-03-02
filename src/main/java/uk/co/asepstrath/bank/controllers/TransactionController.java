package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.annotations.Path;
import io.jooby.annotations.GET;
import io.jooby.annotations.QueryParam;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Page;
import uk.co.asepstrath.bank.models.Transaction;
import uk.co.asepstrath.bank.services.DatabaseService;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Path("/transactions")
public class TransactionController {

    private final Logger logger;
    private final DatabaseService db;

    public TransactionController(Logger logger, DatabaseService data) {
        this.logger = logger;
        this.db = data;
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
        model.put("tPageMode", true);
        ArrayList<Transaction> transactions;
        ArrayList<Page> pages;

        // Load Transactions
        transactions = db.selectAll(Transaction.class, "transactions");

        // Load Pagination
        ArrayList<Object> objects = new ArrayList<>(transactions);
        pages = Page.Paginate(objects, 100);
        model.put("pages", pages);

        // Load the selected page (If there is one)
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("transactions", pages.get(page - 1).getObjects());
            model.put("tCount", pages.get(page - 1).getObjects().size());
            pages.get(page - 1).setCurrent(true);
            // Set the active page;
            return new ModelAndView("transactionView.hbs", model);
        }

        // If the page is null, return the first page
        model.put("transactions", pages.get(0).getObjects());
        pages.get(0).setCurrent(true);
        model.put("tCount", pages.get(0).getObjects().size());
        return new ModelAndView("transactionView.hbs", model);
    }

    /*
    /**
     * Used to display fraudulent transactions
     * @return ModelAndView
    @GET("/fraud")
    public ModelAndView fraudulentTransactions(@QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions;
        model.put("title", "Fraudulent Transactions");
        model.put("fraud", "fraud");

        // Load frauds (Not in a service class because its silly)
        String url = "https://api.asep-strath.co.uk/api/team2/fraud";
        HttpResponse<List<String>> FraudListResponse = Unirest.get(url).accept("application/json").asObject(new GenericType<List<String>>() {});
        List<String> fraudList = FraudListResponse.getBody();

        // Load Transactions
        ResultSet rs = DatabaseService.executeQuery("SELECT * FROM transactions;");
        assert rs != null;
        transactions = DatabaseService.populateTransactions(rs);

        // Filter Transactions
        transactions.removeIf(transaction -> !fraudList.contains(transaction.getId()));
        model.put("transactions", transactions);

        // Load Pagination
        ArrayList<Object> objects = new ArrayList<>(transactions);
        ArrayList<Page> pages = Page.Paginate(objects, 100);
        model.put("pages", pages);

        // Load the selected page (If there is one)
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("transactions", pages.get(page - 1).getObjects());
            model.put("tCount", pages.get(page - 1).getObjects().size());
            pages.get(page - 1).setCurrent(true);
            // Set the active page;
            return new ModelAndView("transactionView.hbs", model);
        }

        // If the page is null, return the first page
        model.put("transactions", transactions);
        model.put("tCount", transactions.size());
        return new ModelAndView("transactionView.hbs", model);
    }

    /**
     * Used to display non fraudulent transactions
     * @return ModelAndView
    @GET("/successful")
    public ModelAndView successfulTransactions(@QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions;
        model.put("title", "Successful Transactions");
        model.put("normal", "normal");
        model.put("tPageMode", true);
        model.put("success", "success");

        // Load frauds (Not in a service class because its silly)
        String url = "https://api.asep-strath.co.uk/api/team2/fraud";
        HttpResponse<List<String>> FraudListResponse = Unirest.get(url).accept("application/json").asObject(new GenericType<List<String>>() {});
        List<String> fraudList = FraudListResponse.getBody();

        // Exclude frauds
        ResultSet rs = DatabaseService.executeQuery("SELECT * FROM transactions;");
        assert rs != null;
        transactions = DatabaseService.populateTransactions(rs);
        transactions.removeIf(transaction -> fraudList.contains(transaction.getId()));

        // Load Pagination
        ArrayList<Object> objects = new ArrayList<>(transactions);
        ArrayList<Page> pages = Page.Paginate(objects, 100);
        model.put("pages", pages);

        // Get page (If there is one)
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("transactions", pages.get(page - 1).getObjects());
            model.put("tCount", pages.get(page - 1).getObjects().size());
            pages.get(page - 1).setCurrent(true);
            // Set the active page;
            return new ModelAndView("transactionView.hbs", model);
        }

        // If the page is null, return the first page
        model.put("transactions", pages.get(0).getObjects());
        pages.get(0).setCurrent(true);
        model.put("tCount", pages.get(0).getObjects().size());
        return new ModelAndView("transactionView.hbs", model);
    }*/
}
