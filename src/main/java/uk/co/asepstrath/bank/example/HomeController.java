package uk.co.asepstrath.bank.example;

import io.jooby.annotations.GET;
import io.jooby.annotations.Path;
import io.jooby.ModelAndView;

@Path("/")
public class HomeController {
    public HomeController() {
    }

    @GET
    public ModelAndView home() {
        return new ModelAndView("home.hbs");
    }
}
