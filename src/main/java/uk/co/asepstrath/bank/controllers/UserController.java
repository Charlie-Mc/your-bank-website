package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.annotations.*;
import uk.co.asepstrath.bank.models.Account;

import javax.sql.DataSource;
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
        model.put("accounts", accounts);
        // TODO: Add SQL for getting data from database then returning to account.hbs
        return new ModelAndView("account.hbs", model);
    }

    @GET("/{user}")
    public ModelAndView account(@PathParam String user) {
        // TODO: Add Logic to get specific data from account in param
        return new ModelAndView("account.hbs");
    }
}
