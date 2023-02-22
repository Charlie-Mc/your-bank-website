package uk.co.asepstrath.bank.controllers;

import com.google.gson.Gson;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Account;
import javax.sql.DataSource;
import java.math.BigDecimal;
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
    public Object accounts(@QueryParam String format, Context ctx) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Accounts");
        ArrayList<Account> accounts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BigDecimal temp = rs.getBigDecimal("balance");
                Account account = new Account(rs.getString("id"),rs.getString("name"), temp,rs.getString("currency"),rs.getString("accountType"));
                accounts.add(account);
            }
            logger.info("Accounts Loaded: " + accounts);
            stmt.close();
        } catch (SQLException e) {
            logger.error("Error connecting to database", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error connecting to database", e);
        }
        if (format != null && format.equals("json")) {
            ctx.setResponseType("application/json");
            logger.info("Accounts Loaded in JSON: " + new Gson().toJson(accounts));
            return new Gson().toJson(accounts);
        }
        model.put("accounts", accounts);
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
