package uk.co.asepstrath.bank.controllers;

import io.jooby.annotations.GET;
import io.jooby.annotations.Path;
import io.jooby.ModelAndView;

import java.util.HashMap;

@Path("/")
public class HomeController {
    @GET
    public ModelAndView home() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Home");
        return new ModelAndView("home.hbs", model);
    }
}
