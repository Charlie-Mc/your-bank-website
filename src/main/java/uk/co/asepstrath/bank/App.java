package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.controllers.AccountController;
import uk.co.asepstrath.bank.controllers.HomeController;
import uk.co.asepstrath.bank.controllers.TransactionController;
import uk.co.asepstrath.bank.controllers.UserController;
import uk.co.asepstrath.bank.models.Account;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
        Logger lgr = getLog();
        // Add Logger below this line when we implement it

        mvc(new HomeController());
        mvc(new UserController(ds, lgr));
        mvc(new AccountController(ds, lgr));
        mvc(new TransactionController(lgr, ds));

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
        String url = "https://api.asep-strath.co.uk/api/team2/accounts";
        // Open Connection to DB
        createAndFillDatabase(ds,url,log);
    }
    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        Logger log = getLog();
        log.info("Shutting Down...");
    }

    public void createAndFillDatabase(DataSource ds, String url, Logger log){
        // create a database connection
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();

            // Fetch data from API
            HttpResponse<List<Account>> accountListResponse = Unirest.get(url).asObject(new GenericType<List<Account>>(){});
            List<Account> AccountList = accountListResponse.getBody();

            // Create user table
            stmt.execute("CREATE TABLE users (id VARCHAR PRIMARY KEY, name VARCHAR(255), balance DECIMAL(10,2), currency VARCHAR(3), accountType VARCHAR(255))");
            // Insert some test data
            PreparedStatement insert = connection.prepareStatement("INSERT INTO users (id, name, balance, currency, accountType) VALUES (?, ?, ?, ?, ?)");

            for(Account account : AccountList) {
                insert.setString(1, account.getId());
                insert.setString(2, account.getName());
                insert.setBigDecimal(3, account.getBalance());
                insert.setString(4, account.getCurrency());
                insert.setString(5, account.getAccountType());
                insert.executeUpdate();
            }
      /*    // some test cases
            stmt.execute("INSERT INTO users (id, name, balance, currency, accountType) VALUES (1,'Rachel', 50, 'GBP', 'Current')");
            stmt.execute("INSERT INTO users (id, name, balance,currency, accountType) VALUES (2,'Monica', 100, 'GBP', 'Current')");
            stmt.execute("INSERT INTO users (id, name, balance,currency, accountType) VALUES (3,'Phoebe', 76, 'GBP', 'Current')");
            stmt.execute("INSERT INTO users (id, name, balance,currency, accountType) VALUES (4,'Joey', 23.90, 'GBP', 'Current')");
            stmt.execute("INSERT INTO users (id, name, balance,currency, accountType) VALUES (5,'Chandler', 3, 'GBP', 'Current')");
            stmt.execute("INSERT INTO users (id, name, balance,currency, accountType) VALUES (6,'Ross', 54.32, 'GBP', 'Current')");
     */       stmt.close();
        } catch (SQLException e) {
            log.error("Database Creation Error",e);
        }
    }

}
