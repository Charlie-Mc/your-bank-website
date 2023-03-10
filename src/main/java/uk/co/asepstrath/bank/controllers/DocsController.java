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
        routes.add(new Route("/api/Team2/accounts", "ACCOUNT", "This fetches the accounts held by the bank", "200 = Bank account retrieved, 404 = Bank requested does not exist"));
        routes.add(new Route("/api/Team2/fraud", "FRAUD", "This fetches the fraudulent transactions reported by the Banking Regulators", "200 = Fraudulent transactions retrieved, 404 = Bank requested does not exist"));
        routes.add(new Route("/api/Team2/reversal", "REVERSAL", "This is too notify another bank of a transaction reversal", "200 = Reversal notification retrieved, 400 = An issue occurred, see error message to determine issue, 404 = Bank requested does not exist"));
        routes.add(new Route("/api/Team2/transaction", "TRANSACTION", "This fetches transactions from a bank", "200 = Transactions retrieved, 404 = Bank requested does not exist"));
        model.put("routes", routes);
        return new ModelAndView("docs.hbs", model);
    }
}
