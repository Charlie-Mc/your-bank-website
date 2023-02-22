package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;
import javax.sql.DataSource;
import org.slf4j.Logger;

import java.util.HashMap;

/**
 * Controller for user accounts
 * @author Team 2
 * @version 1.0 {@summary Handles the login, signup and logout of users}
 */
@Path("/users")
public class AccountController {

    private final DataSource ds;
    private final Logger lgr;

    public AccountController(DataSource ds, Logger lgr) {
        this.ds = ds;
        this.lgr = lgr;
        lgr.info("AccountController Created");
    }

    @GET("/login")
    public Object login() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Login");
        model.put("login", "login");
        return new ModelAndView("authView.hbs", model);
    }

    @GET("/signup")
    public Object signup() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Sign Up");
        model.put("signup", "signup");
        return new ModelAndView("authView.hbs", model);
    }

    @GET("/logout")
    public Object logout() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Logout");
        model.put("logout", "logout");
        return new ModelAndView("authView.hbs", model);
    }
}
