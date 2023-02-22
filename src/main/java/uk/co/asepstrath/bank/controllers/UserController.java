package uk.co.asepstrath.bank.controllers;

import com.google.gson.Gson;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import org.apache.http.annotation.Obsolete;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Page;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


@Path("/accounts")
public class UserController {



    private final DataSource dataSource;
    private final Logger logger;

    // Save accounts to an arraylist to prevent constant database calls
    private ArrayList<Account> accounts = new ArrayList<>();

    public UserController(DataSource ds, Logger lgr) {
        dataSource = ds;
        logger = lgr;
    }

    /**
     * This is the revised list method
     * @param format String
     * @param ctx Context
     * @param page Integer
     * @return Object
     */

    @GET
    public Object RevisedList(@QueryParam String format, Context ctx, @QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Page> pages = new ArrayList<>();
        // Add Page Title to the model
        model.put("title", "Accounts");
        if (this.accounts.isEmpty()) {
            // If the accounts arraylist is empty, load from database
            try (Connection conn = dataSource.getConnection()) {
                ArrayList<Account> accounts = new ArrayList<>();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    BigDecimal temp = rs.getBigDecimal("balance");
                    Account account = new Account(rs.getString("id"), rs.getString("name"), temp, rs.getString("currency"), rs.getString("accountType"));
                    accounts.add(account);
                }
                this.accounts = accounts;
                logger.info("Accounts Loaded: " + accounts.size());
                stmt.close();
            } catch (SQLException e) {
                logger.error("Error connecting to database", e);
                throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error connecting to database", e);
            }
        }
        // Load pagination
        int count = 0;
        for (Account a : accounts) {
            count++;
            if (count % 20 == 0) {
                // Add page to the model
                Page p = new Page(new ArrayList<>(this.accounts.subList(count - 20, count)), count / 20);
                pages.add(p);
                model.put("pages", pages);
            }
        }
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("accounts", pages.get(page - 1).getAccounts());
            pages.get(page - 1).setCurrent(true);
            model.put("pageCount", pages.get(pages.size() - 1).count);
            // Set the active page;
            return new ModelAndView("account.hbs", model);
        }
        if (format != null && format.equals("json")) {
            ctx.setResponseType("application/json");
            logger.info("Accounts Loaded in JSON: " + new Gson().toJson(accounts));
            return new Gson().toJson(accounts);
        }
        // If the page is null, return the first page
        model.put("accounts", pages.get(0).getAccounts());
        pages.get(0).setCurrent(true);
        model.put("pageCount", pages.get(pages.size() - 1).count);
        return new ModelAndView("account.hbs", model);
    }

    @GET("/{user}")
    public Object account(@PathParam String user, @QueryParam String format, Context ctx) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "View Account");
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            stmt.setString(1, user);
            ResultSet rs =  stmt.executeQuery();
            if (rs.next()) {
                BigDecimal temp = rs.getBigDecimal("balance");
                Account account = new Account(rs.getString("id"),rs.getString("name"), temp,rs.getString("currency"),rs.getString("accountType"));
                if (format != null && format.equals("json")) {
                    ctx.setResponseType("application/json");
                    logger.info("Account Loaded in JSON: " + account);
                    return new Gson().toJson(account);
                }
                model.put("account", account);
                logger.info("Account Loaded: " + account);
            } else {
                stmt.close();
                throw new StatusCodeException(StatusCode.NOT_FOUND, "Account not found");
            }
            stmt.close();
        } catch (SQLException e) {
            logger.error("Error connecting to database", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error connecting to database", e);
        }
        return new ModelAndView("account.hbs", model);
    }
}
