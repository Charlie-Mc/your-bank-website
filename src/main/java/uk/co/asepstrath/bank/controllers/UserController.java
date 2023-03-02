package uk.co.asepstrath.bank.controllers;

import com.google.gson.Gson;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotations.*;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Page;
import uk.co.asepstrath.bank.models.Transaction;
import uk.co.asepstrath.bank.services.DatabaseService;

import java.util.ArrayList;
import java.util.HashMap;


@Path("/accounts")
public class UserController {
    private final Logger logger;
    private final DatabaseService db;

    public UserController(Logger lgr, DatabaseService data) {
        logger = lgr;
        db = data;
    }

    @GET
    public Object listAllAccounts(@QueryParam String format, Context ctx, @QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Page> Pages;
        ArrayList<Account> accounts;
        model.put("aPageMode", "accounts");

        // Add Page Title to the model
        model.put("title", "Accounts");

        // Load accounts
        accounts = db.selectAll(Account.class, "users");

        // Load pagination
        ArrayList<Object> accountObjs = new ArrayList<>(accounts);
        Pages = Page.Paginate(accountObjs, 20);
        model.put("pages", Pages);

        // Load the selected page (If there is one)
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("accounts", Pages.get(page - 1).getObjects());
            Pages.get(page - 1).setCurrent(true);
            model.put("pageCount", Pages.get(page - 1).count);
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
        model.put("pageCount", Pages.get(0).count);

        // Return the accounts
        return new ModelAndView("account.hbs", model);
    }

    @GET("/{user}")
    public Object account(@PathParam String user, @QueryParam String format, Context ctx) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "View Account");
        ArrayList<Transaction> transactions;
        Account account;

        // Load account
        account = db.selectById(Account.class, "users", user);

        // Load transactions
        transactions = db.selectAll(Transaction.class, "transactions");

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
        ArrayList<Object> accounts = new ArrayList<>();

        // Load accounts
        for (Account account : db.selectAll(Account.class, "users")) {
            if (account.getName().toLowerCase().startsWith(name.toLowerCase())) {
                accounts.add(account);
            }
        }

        // Return the accounts
        model.put("accounts", accounts);
        model.put("pageCount", accounts.size());
        return new ModelAndView("account.hbs", model);
    }
}
