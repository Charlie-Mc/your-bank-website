package uk.co.asepstrath.bank.controllers;

import com.google.gson.Gson;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Page;
import uk.co.asepstrath.bank.models.Transaction;
import uk.co.asepstrath.bank.services.DatabaseService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


@Path("/accounts")
public class UserController {
    private final DataSource dataSource;
    private final Logger logger;

    public UserController(DataSource ds, Logger lgr) {
        dataSource = ds;
        logger = lgr;
    }

    @GET
    public Object listAllAccounts(@QueryParam String format, Context ctx, @QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Page> Pages;
        ArrayList<Object> accounts;
        ResultSet rs;
        model.put("aPageMode", "accounts");

        // Add Page Title to the model
        model.put("title", "Accounts");

        // Load accounts
        rs = DatabaseService.executeQuery("SELECT id, name, balance, currency, accountType FROM users;");
        assert rs != null;
        accounts = DatabaseService.populateAccounts(rs);

        // Load pagination
        Pages = Page.Paginate(accounts, 20);
        model.put("pages", Pages);

        // Load the selected page (If there is one)
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("accounts", Pages.get(page - 1).getObjects());
            Pages.get(page - 1).setCurrent(true);
            model.put("pageCount", Pages.get(Pages.size() - 1).count);
            // Set the active page;
            return new ModelAndView("account.hbs", model);
        }

        // If the format is JSON, return the accounts in JSON
        if (format != null && format.equals("json")) {
            ctx.setResponseType("application/json");
            logger.info("Accounts Loaded in JSON: " + new Gson().toJson(accounts));
            return new Gson().toJson(accounts);
        }

        // If the page is null, return the first page
        model.put("accounts", Pages.get(0).getObjects());
        Pages.get(0).setCurrent(true);
        model.put("pageCount", Pages.get(Pages.size() - 1).count);

        // Return the accounts
        return new ModelAndView("account.hbs", model);
    }

    @GET("/{user}")
    public Object account(@PathParam String user, @QueryParam String format, Context ctx) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "View Account");
        ArrayList<Transaction> transactions;
        Account account = null;

        // Load account
        ResultSet rs = DatabaseService.executeQuery("SELECT id, name, balance, currency, accountType FROM users WHERE id = ?;", user);
        try {
            assert rs != null;
            if (rs.next()) {
                account = new Account(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("balance"),
                        rs.getString("currency"),
                        rs.getString("accountType")
                );
                logger.info("Account Loaded: " + new Gson().toJson(account));
            }
        } catch (SQLException e) {
            throw new StatusCodeException(StatusCode.NOT_FOUND, "No Account Found", e);
        }

        // Load transactions
        rs = DatabaseService.executeQuery("SELECT id, fromAccount, toAccount, amount, currency, date FROM transactions WHERE fromAccount = ? OR toAccount = ?;", user, user);
        assert rs != null;
        transactions = DatabaseService.populateTransactions(rs);

        // Return the account and transactions
        model.put("account", account);
        model.put("transactions", transactions);
        return new ModelAndView("account.hbs", model);
    }

    /**
     * Used to search for accounts by name
     * @param name Account name
     * @return ModelAndView
     */
    @GET("/search")
    public ModelAndView accountSearch(@QueryParam String name) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Search Accounts");
        model.put("accountSearch", "account");
        ArrayList<Object> accounts;

        // Load accounts
        ResultSet rs = DatabaseService.executeQuery("SELECT id, name, balance, currency, accountType FROM users WHERE lower(name) LIKE ?", name + "%");
        assert rs != null;
        accounts = DatabaseService.populateAccounts(rs);

        // Return the accounts
        model.put("accounts", accounts);
        model.put("pageCount", accounts.size());
        return new ModelAndView("account.hbs", model);
    }
}
