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

@Path("/staff/swagger/docs")
public class DocsController {

    public DocsController(DataSource ds, Logger lgr) {
        lgr.info("DocsController Created");
    }

    @GET
    public ModelAndView docs() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "API Documentation");
        List<Route> routes = new ArrayList<>();
        routes.add(new Route("/api/team2/accounts", "GET", "This fetches the accounts held by the bank").addResponse("200", "Bank account retrieved").addResponse("404", "Bank requested does not exist"));
        routes.add(new Route("/api/team2/fraud", "GET", "This fetches the fraudulent transactions reported by the Banking Regulators").addResponse("200", "Fraudulent transactions retrieved").addResponse("404", "Bank requested does not exist"));
        routes.add(new Route("/api/team2/reversal", "POST", "This is too notify another bank of a transaction reversal").addResponse("200", "Reversal notification retrieved").addResponse("400", "An issue occurred, see error message to determine issue").addResponse("404", "Bank requested does not exist"));
        routes.add(new Route("/api/team2/transaction", "GET", "This fetches transactions from a bank").addResponse("200", "Transactions retrieved").addResponse("404", "Bank requested does not exist"));
        model.put("routes", routes);
        return new ModelAndView("docs.hbs", model);
    }
}
