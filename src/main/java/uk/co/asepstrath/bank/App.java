package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.controllers.*;
import uk.co.asepstrath.bank.models.*;
import uk.co.asepstrath.bank.services.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class App extends Jooby {
    private final DatabaseService db;

    {
        /*
        This section is used for setting up the Jooby Framework modules
         */
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));

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
        assets("/service_worker.js", "/service_worker.js");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger lgr = getLog();
        // Initialise DatabaseService
        db = new DatabaseService(ds, lgr);
        // Add Logger below this line when we implement it

        mvc(new HomeController());
        mvc(new UserController(lgr, db));
        mvc(new AccountController(ds, lgr));
        mvc(new TransactionController(lgr, db));


        /*
        Finally we register our application lifecycle methods
         */
        onStarted(this::onStart);
        onStop(this::onStop);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */
    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");
        createDatabase(log);
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        Logger log = getLog();
        log.info("Shutting Down...");
    }

    public void createDatabase(Logger log){
        boolean success;
        String[] columns;
        String[] types;
        String tableName;
        String url;
        log.info("Creating Database...");

        // Accounts
        tableName = "users";
        columns = new String[]{"id", "name", "balance", "currency", "accountType"};
        types = new String[]{"VARCHAR(255) PRIMARY KEY", "VARCHAR(255)", "DECIMAL(10,2)", "VARCHAR(3)", "VARCHAR(255)"};
        success = db.createTable(tableName, columns, types);

        if(success){
            log.info("Created table " + tableName);
        } else {
            log.error("Failed to create table " + tableName);
            System.exit(1);
        }

        // Transactions
        tableName = "transactions";
        columns = new String[]{"id", "fromAccount", "toAccount", "amount", "currency", "date"};
        types = new String[]{"VARCHAR(255) PRIMARY KEY", "VARCHAR(255)", "VARCHAR(255)", "DECIMAL(10,2)", "VARCHAR(3)", "TIMESTAMP"};
        success = db.createTable(tableName, columns, types);

        if(success){
            log.info("Created table " + tableName);
        } else {
            log.error("Failed to create table " + tableName);
            System.exit(1);
        }

        // Populate Database
        log.info("Populating Database...");
        url = "https://api.asep-strath.co.uk/api/team2/accounts";
        HttpResponse<List<Account>> accountListResponse = Unirest.get(url).asObject(new GenericType<List<Account>>(){});
        List<Account> AccountList = accountListResponse.getBody();

        ArrayList<Account> accounts = db.cleanAccountInput(new ArrayList<>(AccountList));

            AccountList = filter(AccountList);

            // Create user table

        int count = 0;
        for(Account account : accounts){
            success = db.insert("users", new String[]{"id", "name", "balance", "currency", "accountType"},
                    new String[]{account.getId(), account.getName(), account.getBalance().toString(),
                            account.getCurrency(), account.getAccountType()});
            if(success){
                count++;
            } else {
                log.error("Failed to insert account " + account.getId());
            }
        }

        url = "https://api.asep-strath.co.uk/api/team2/transactions?page=1&pageSize=1000";
        HttpResponse<List<Transaction>> transactionListResponse = Unirest.get(url).accept("application/json").asObject(new GenericType<List<Transaction>>(){});
        List<Transaction> TransactionList = transactionListResponse.getBody();

        int count2 = 0;
        for(Transaction transaction : TransactionList){
            success = db.insert("transactions", new String[]{"id", "fromAccount", "toAccount", "amount", "currency", "date"},
                    new String[]{transaction.getId(), transaction.getWithdrawAccount(), transaction.getDepositAccount(),
                            transaction.getAmount().toString(), transaction.getCurrency(), transaction.getDate() == null ? null : transaction.getDate().toString()});
            if(success){
                count2++;
            } else {
                log.error("Failed to insert transaction " + transaction.getId());
            }
        }


        log.info("Database Created" + " (" + count + " accounts inserted)");
        log.info("Database Created" + " (" + count2 + " transactions inserted)");
    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    public List<Account> filter(List<Account> AccountList){

        List<Account> filteredList = new ArrayList<>();
        List<Account> deletedAccounts = new ArrayList<>();
        for (Account account : AccountList) {
            account.setName(account.getName().replace("'", "''"));
            account.setName(account.getName().strip());
            if (account.getName().length() <= 255 && !account.getName().trim().isEmpty() && !account.getName().matches(".*[<>\\&'\"/\\\\%#\\{\\}|\\^~\\[\\]`=;:\\?!\\*\\(\\)\\-\\+\\.\\$,\\@0123456789].*")) {
                filteredList.add(account);
            }else {
                deletedAccounts.add(account);
            }
        }
        return filteredList;
    }
}
