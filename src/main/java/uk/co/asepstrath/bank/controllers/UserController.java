package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import uk.co.asepstrath.bank.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;

@Path("/account")
public class UserController {

    private final DataSource dataSource;

    public UserController(DataSource ds) {
        dataSource = ds;
    }

    @GET("/{user}")
    public ModelAndView account(@SessionParam String username, @PathParam String user) {
        if (username == null) {
            HashMap<String, Object> model = new HashMap<>();
            model.put("message", "Please log in");
            model.put("title", "Login");
            return new ModelAndView("login.hbs", model);
        } else if (!username.equals(user)) {
            throw new StatusCodeException(StatusCode.UNAUTHORIZED, "Not your account");
        }

        // Get account details
        try (Connection conn = dataSource.getConnection()) {
            // Create prepared statement
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE name = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HashMap<String, Object> model = new HashMap<>();

                model.put("user", new User(rs.getString("name"), rs.getString("password"), rs.getInt("balance")));
                model.put("title", "Account");
                // Start a new session
                return new ModelAndView("account.hbs", model);
            }
        } catch (SQLException e) {
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error connecting to database", e);

        }
        return new ModelAndView("account.hbs");
    }

    /**
     * User login page
     * @param username Session username variable
     * @return ModelAndView
     */
    @GET("/login")
    public ModelAndView login(@SessionParam String username) {
        ModelAndView result;
        HashMap<String, Object> model = new HashMap<>();
        if (username != null) {
            model.put("title", "Home");
            result = new ModelAndView("home.hbs", model);
        } else {
            model.put("title", "Login");
            result = new ModelAndView("login.hbs", model);
        }
        return result;
    }

    /**
     * User logout page
     * @param username Session username variable
     * @param session Session object
     * @return ModelAndView
     */
    @GET("/logout")
    public ModelAndView logout(Session session, @SessionParam String username) {
        HashMap<String, Object> model = new HashMap<>();
        if (username == null) {
            model.put("message", "Please log in");
            return new ModelAndView("login.hbs", model);
        }
        session.clear();
        model.put("message", "Logged out");
        model.put("title", "Logged out!");
        return new ModelAndView("login.hbs", model);
    }

    /**
     * Used to process user login submissions
     * @param username Username variable
     * @param password Password variable
     * @param session Session object
     * @return ModelAndView
     */
    @POST("/login")
    public ModelAndView login(String username, String password, Session session) {
        // Verify the user exists by counting number of rows returned when username and password are queried
        try (Connection conn = dataSource.getConnection()) {
            // Clean the input
            username = username.replaceAll("[^a-zA-Z0-9]", "");
            password = password.replaceAll("[^a-zA-Z0-9]", "");
            // Create prepared statement
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*), password FROM users WHERE name = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            HashMap<String, Object> model = new HashMap<>();
            if (rs.next()) {
                int count = rs.getInt(1);
                String dbPassword = rs.getString(2);
                if (count == 0) {
                    // Redirect back to login page with error message
                    model.put("message", "User does not exist");
                    model.put("title", "Login");
                    return new ModelAndView("login.hbs", model);
                } else if (!dbPassword.equals(password)) {
                    // Redirect back to login page with error message
                    model.put("message", "Incorrect password");
                    model.put("title", "Login");
                    return new ModelAndView("login.hbs", model);
                } else {
                    // User exists and password is correct
                    session.put("username", username);
                    model.put("title", "Home");
                    return new ModelAndView("home.hbs", model);
                }
            }
        } catch (SQLException e) {
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error connecting to database", e);
        }
        return new ModelAndView("login.hbs");
    }
}
