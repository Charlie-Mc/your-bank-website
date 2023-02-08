package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.controllers.HomeController;
import uk.co.asepstrath.bank.controllers.UserController;
import uk.co.asepstrath.bank.models.Account;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
public class App extends Jooby {

    {
        /*
        This section is used for setting up the Jooby Framework modules
         */
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));

        /*
        This will host any files in src/main/resources/assets on <host>/assets
        For example in the dice template (dice.hbs) it references "assets/dice.png" which is in resources/assets folder
         */
        assets("/assets/*", "/assets");
        assets("/service_worker.js","/service_worker.js");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger log = getLog();

        mvc(new HomeController());
        mvc(new UserController(ds));

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(() -> onStart());
        onStop(() -> onStop());
    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */
    public ArrayList<Account> dataset = new ArrayList<>();

    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");
        dataset.add(new Account(new BigDecimal(50), "Rachel"));
        dataset.add(new Account(new BigDecimal(100), "Monica"));
        dataset.add(new Account(new BigDecimal(76), "Phoebe"));
        dataset.add(new Account(new BigDecimal(23.90), "joey"));
        dataset.add(new Account(new BigDecimal(3), "Chandler"));
        dataset.add(new Account(new BigDecimal(54.32), "Ross"));
        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            // Create user table
            stmt.execute("CREATE TABLE users (id INTEGER PRIMARY KEY, name VARCHAR(255), balance DECIMAL(10,2))");
        } catch (SQLException e) {
            log.error("Database Creation Error",e);
        }
    }
    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
