package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.annotations.*;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Page;
import uk.co.asepstrath.bank.models.Transaction;
import uk.co.asepstrath.bank.services.DatabaseService;



import java.math.BigDecimal;
import java.util.*;

@Path("/transactions")
public class TransactionController {

    private final Logger logger;
    private final DatabaseService db;

    public TransactionController(Logger logger, DatabaseService data) {
        this.logger = logger;
        this.db = data;
    }

    /**
     * Used to display all transactions
     * @return ModelAndView
     */
    @GET
    public ModelAndView transactions(@QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Transactions");
        model.put("all", "all");
        model.put("tPageMode", true);
        ArrayList<Transaction> transactions;
        ArrayList<Page> pages;

        // Load Transactions
        transactions = db.selectAll(Transaction.class, "transactions");
        logger.info("Loaded " + transactions.size() + " transactions");

        // Load Pagination
        ArrayList<Object> objects = new ArrayList<>(transactions);
        pages = Page.Paginate(objects, 100);
        model.put("pages", pages);
        logger.info("Loaded " + pages.size() + " pages");

        // Load the selected page (If there is one)
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("transactions", pages.get(page - 1).getObjects());
            model.put("tCount", pages.get(page - 1).getObjects().size());
            pages.get(page - 1).setCurrent(true);
            // Set the active page;
            return new ModelAndView("transactionView.hbs", model);
        }

        // If the page is null, return the first page
        model.put("transactions", pages.get(0).getObjects());
        pages.get(0).setCurrent(true);
        model.put("tCount", pages.get(0).getObjects().size());
        return new ModelAndView("transactionView.hbs", model);
    }

    /**
     * Used to display fraudulent transactions
     * @return ModelAndView
     */
    @GET("/fraud")
    public ModelAndView fraudulentTransactions(@QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions;
        model.put("title", "Fraudulent Transactions");
        model.put("fraud", "fraud");

        // Load frauds (Not in a service class because its silly)
        String url = "https://api.asep-strath.co.uk/api/team2/fraud";
        HttpResponse<List<String>> FraudListResponse = Unirest.get(url).accept("application/json").asObject(new GenericType<List<String>>() {});
        List<String> fraudList = FraudListResponse.getBody();

        // Load Transactions
        transactions = db.selectAll(Transaction.class, "transactions");

        // Filter frauds
        transactions.removeIf(transaction -> !fraudList.contains(transaction.getId()));
        logger.info("Loaded " + transactions.size() + " fraudulent transactions");

        // Load Pagination
        ArrayList<Object> objects = new ArrayList<>(transactions);
        ArrayList<Page> pages = Page.Paginate(objects, 100);
        model.put("pages", pages);
        logger.info("Loaded " + pages.size() + " pages");

        // Load the selected page (If there is one)
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("transactions", pages.get(page - 1).getObjects());
            model.put("tCount", pages.get(page - 1).getObjects().size());
            pages.get(page - 1).setCurrent(true);
            // Set the active page;
            return new ModelAndView("transactionView.hbs", model);
        }

        // If the page is null, return the first page
        model.put("transactions", transactions);
        model.put("tCount", transactions.size());
        return new ModelAndView("transactionView.hbs", model);
    }

    /**
     * Used to display non fraudulent transactions
     * @return ModelAndView
     *
     */
    @GET("/successful")
    public ModelAndView successfulTransactions(@QueryParam Integer page) {
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions;
        model.put("title", "Successful Transactions");
        model.put("normal", "normal");
        model.put("tPageMode", true);
        model.put("success", "success");

        // Load frauds (Not in a service class because its silly)
        String url = "https://api.asep-strath.co.uk/api/team2/fraud";
        HttpResponse<List<String>> FraudListResponse = Unirest.get(url).accept("application/json").asObject(new GenericType<List<String>>() {});
        List<String> fraudList = FraudListResponse.getBody();

        // Load Transactions
        transactions = db.selectAll(Transaction.class, "transactions");

        // Exclude frauds
        transactions.removeIf(transaction -> fraudList.contains(transaction.getId()));
        logger.info("Loaded " + transactions.size() + " successful transactions");

        // Load Pagination
        ArrayList<Object> objects = new ArrayList<>(transactions);
        ArrayList<Page> pages = Page.Paginate(objects, 100);
        model.put("pages", pages);
        logger.info("Loaded " + pages.size() + " pages");

        // Get page (If there is one)
        if (page != null) {
            // If the page is not null, return the accounts for that page
            model.put("transactions", pages.get(page - 1).getObjects());
            model.put("tCount", pages.get(page - 1).getObjects().size());
            pages.get(page - 1).setCurrent(true);
            // Set the active page;
            return new ModelAndView("transactionView.hbs", model);
        }

        // If the page is null, return the first page
        model.put("transactions", pages.get(0).getObjects());
        pages.get(0).setCurrent(true);
        model.put("tCount", pages.get(0).getObjects().size());
        return new ModelAndView("transactionView.hbs", model);
    }


    // This is the method that is called when the user clicks the repeat button on the transaction page
    @POST("/repeat")
    public void repeatTransaction(@FormParam("id") String id, @FormParam("withdrawAccount") String withdrawAccount, @FormParam("depositAccount") String depositAccount, @FormParam("amount") BigDecimal amount, @FormParam("currency") String currency) {
        Logger log;
        Transaction transaction = new Transaction(id, withdrawAccount, depositAccount, null, amount, currency);

        // gets the accounts from the api;
        String url = "https://api.asep-strath.co.uk/api/team2/accounts";
        HttpResponse<ArrayList<Account>> accountListResponse = Unirest.get(url).asObject(new GenericType<ArrayList<Account>>() {
        });
        ArrayList<Account> AccountList = accountListResponse.getBody();

        boolean result = transaction.doTransaction(AccountList);


        // if the transaction is not successful, it will do a fake transaction due to fake accounts present
        if (!result) {
          //  transaction.doFakeTransaction(AccountList);
        }


        // generates a random id for the transaction in the same format as the api
        boolean success = false;
        int count = 0;
        UUID uuid = null;
        String randomStringId;

        while (!success) {
            count++;
            uuid = UUID.randomUUID();
            randomStringId = uuid.toString();
            success = db.insert("transactions", new String[]{"id", "fromAccount", "toAccount", "amount", "currency", "date"},
                    new String[]{randomStringId, transaction.getWithdrawAccount(), transaction.getDepositAccount(),
                            transaction.getAmount().toString(), transaction.getCurrency(), transaction.getDate() == null ? null : transaction.getDate().toString()});
            if (count > 1000) {
                logger.error("Failed to insert repeat transaction: " + transaction.getId());
            }

        }
    }

    @POST("/reverse")
    public void reverseTransaction(@FormParam("id") String id, @FormParam("withdrawAccount") String withdrawAccount, @FormParam("depositAccount") String depositAccount, @FormParam("amount") BigDecimal amount, @FormParam("currency") String currency) {
        Logger log;
        Transaction transaction = new Transaction(id, withdrawAccount, depositAccount, null, amount, currency);

        // gets the accounts from the api;
        String url = "https://api.asep-strath.co.uk/api/team2/accounts";
        HttpResponse<ArrayList<Account>> accountListResponse = Unirest.get(url).asObject(new GenericType<ArrayList<Account>>() {
        });
        ArrayList<Account> accounts = accountListResponse.getBody();

        transaction.reverseTransaction(accounts);
        boolean deleteSuccess = db.delete("transactions", new String[]{"id"}, new String[]{id});


    }


}

