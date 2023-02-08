package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.controllers.HomeController;
import uk.co.asepstrath.bank.controllers.UserController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

        // Add Logger below this line when we implement it

        mvc(new HomeController());
        mvc(new UserController(ds));

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(this::onStart);
        onStop(this::onStop);
    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */

    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");
        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            // Create user table
            stmt.execute("CREATE TABLE users (id INTEGER PRIMARY KEY, name VARCHAR(255), balance DECIMAL(10,2))");
            // Insert some test data
            stmt.execute("INSERT INTO users (id, name, balance) VALUES (1,'Rachel', 50)");
            stmt.execute("INSERT INTO users (id, name, balance) VALUES (2,'Monica', 100)");
            stmt.execute("INSERT INTO users (id, name, balance) VALUES (3,'Phoebe', 76)");
            stmt.execute("INSERT INTO users (id, name, balance) VALUES (4,'Joey', 23.90)");
            stmt.execute("INSERT INTO users (id, name, balance) VALUES (5,'Chandler', 3)");
            stmt.execute("INSERT INTO users (id, name, balance) VALUES (6,'Ross', 54.32)");
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
