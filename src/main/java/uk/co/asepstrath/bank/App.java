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
import uk.co.asepstrath.bank.models.Transaction;
import uk.co.asepstrath.bank.services.DatabaseService;

import javax.sql.DataSource;
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

        // Initialise DatabaseService
        //noinspection InstantiationOfUtilityClass
        new DatabaseService(ds, lgr);

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(this::onStart);
        onStop(this::onStop);
    }

    public static void main(final String[] args) { runApp(args, App::new); }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */

    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");
        createAndFillDatabase(log);
    }
    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        Logger log = getLog();
        log.info("Shutting Down...");
    }

    public void createAndFillDatabase(Logger log){
        // Accounts
        String url = "https://api.asep-strath.co.uk/api/team2/accounts";
        HttpResponse<List<Account>> accountListResponse = Unirest.get(url).asObject(new GenericType<List<Account>>(){});
        List<Account> AccountList = accountListResponse.getBody();
        DatabaseService.executeUpdate("CREATE TABLE users (id VARCHAR PRIMARY KEY, name VARCHAR(255), balance DECIMAL(10,2), currency VARCHAR(3), accountType VARCHAR(255))");
        for (Account account : AccountList) {
            int status = DatabaseService.executeUpdate("INSERT INTO users (id, name, balance, currency, accountType) VALUES (?, ?, ?, ?, ?)",
                    account.getId(),
                    account.getName().length() <= 255 ? account.getName()  : null,
                    account.getBalance(),
                    account.getCurrency(),
                    account.getAccountType()
            );
            if (status == 0) {
                log.error("Failed to insert account: " + account.getId());
            }
        }

        // Transactions
        DatabaseService.executeUpdate("CREATE TABLE transactions (id VARCHAR PRIMARY KEY, fromAccount VARCHAR(255), toAccount VARCHAR(255), amount DECIMAL(10,2), currency VARCHAR(3), date VARCHAR(255))");
        url = "https://api.asep-strath.co.uk/api/team2/transactions?PageNumber=1&PageSize=1000";
        HttpResponse<List<Transaction>> transactionListResponse = Unirest.get(url).asObject(new GenericType<List<Transaction>>(){});
        List<Transaction> TransactionList = transactionListResponse.getBody();

        for (Transaction transaction : TransactionList) {
            int status = DatabaseService.executeUpdate("INSERT INTO transactions (id, fromAccount, toAccount, amount, currency, date) VALUES (?, ?, ?, ?, ?, ?)",
                    transaction.getId(),
                    transaction.getWithdrawAccount(),
                    transaction.getDepositAccount(),
                    transaction.getAmount(),
                    transaction.getCurrency(),
                    // Inline if statement to check if date is null
                    transaction.getDate() == null ? null : transaction.getDate().toString()
            );
            if (status == 0) {
                log.error("Failed to insert transaction: " + transaction.getId());
            }
        }
        log.info("Database Created");
    }
}
