package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import uk.co.asepstrath.bank.models.Account;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/accounts")
public class UserController {

    private final DataSource dataSource;

    public UserController(DataSource ds) {
        dataSource = ds;
    }

    @GET
    public ModelAndView account() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Accounts");
        ArrayList<Account> accounts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BigDecimal temp = rs.getBigDecimal("balance");
                Account account = new Account(temp, rs.getString("name"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error connecting to database", e);
        } finally {
            // Close connection


        }
        model.put("accounts", accounts);
        return new ModelAndView("account.hbs", model);
    }

    @GET("/{user}")
    public ModelAndView account(@PathParam String user) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "View Account");
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
            stmt.setString(1, user);
            ResultSet rs =  stmt.executeQuery();
            if (rs.next()) {
                BigDecimal temp = rs.getBigDecimal("balance");
                Account account = new Account(temp, rs.getString("name"));
                model.put("account", account);
            } else {
                throw new StatusCodeException(StatusCode.NOT_FOUND, "Account not found");
            }

        } catch (SQLException e) {
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error connecting to database", e);
        } finally {
            // Close connection
        }
        return new ModelAndView("account.hbs", model);

    }
}
