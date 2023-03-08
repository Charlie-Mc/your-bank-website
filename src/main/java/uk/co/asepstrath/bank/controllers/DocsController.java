package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;
import javax.sql.DataSource;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/docs")
public class DocsController {
    private final DataSource ds;
    private final Logger lgr;

    public DocsController(DataSource ds, Logger lgr) {
        this.ds = ds;
        this.lgr = lgr;
        lgr.info("DocsController Created");
    }

    @GET
    public ModelAndView docs() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "API Documentation");
        List<Route> routes = new ArrayList<>();
        routes.add(new Route("/api/routea", "GET", "this does abc"));
        routes.add(new Route("/api/routeb", "POST", "this does ijk"));
        routes.add(new Route("/api/routec", "DELETE", "this does xyz"));
        model.put("routes", routes);
        return new ModelAndView("docs.hbs", model);
    }
}
