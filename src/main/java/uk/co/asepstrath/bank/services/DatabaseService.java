package uk.co.asepstrath.bank.services;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseService {
    private static DataSource _ds;
    private static Logger _logger;
    private static Connection _conn;

    public DatabaseService(DataSource ds, Logger logger) {
        _ds = ds;
        _logger = logger;
    }

    private static Connection init() {
        try {
            if (_conn == null || _conn.isClosed()) {
                _conn = _ds.getConnection();
            }
            return _conn;
        } catch (Exception e) {
            _logger.error("Failed to connect to the database | " + e.getMessage());
        }
        return null;
    }

    /**
     * Executes a query and returns the result set
     * Parameters must be in the order of the query
     * @param query Query to be executed
     * @param params Parameters to be inserted into the query
     * @return ResultSet
     */
    public static ResultSet executeQuery(String query, Object... params) {
        // Initialise the connection
        Connection conn = init();

        assert conn != null;

        // Prepare the statement
        PreparedStatement stmt;

        try {
            stmt = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.close();
            return stmt.executeQuery();
        } catch (SQLException e) {
            _logger.error("Failed to prepare statement | " + e.getMessage());
            return null;
        }
    }

    public static int executeUpdate(String query, Object... params) {
        // Initialise the connection
        Connection conn = init();

        // Prepare the statement
        PreparedStatement stmt;

        try {
            assert conn != null;
            stmt = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.close();
            return stmt.executeUpdate();
        } catch (SQLException e) {
            _logger.error("Failed to prepare statement | " + e.getMessage());
            return -1;
        }
    }

    public static ArrayList<Object> populateAccounts(ResultSet rs) {
        ArrayList<Object> objects = new ArrayList<>();
        try {
            while (rs.next()) {
                objects.add(new Account(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("balance"),
                        rs.getString("currency"),
                        rs.getString("accountType")));
                }
            return objects;
        } catch (SQLException e) {
            _logger.error("Failed to populate array | " + e.getMessage());
        }
        return objects;
    }

    public static ArrayList<Transaction> populateTransactions(ResultSet rs) {
        ArrayList<Transaction> objects = new ArrayList<>();
        try {
            while (rs.next()) {
                objects.add(new Transaction(
                        rs.getString("id"),
                        rs.getString("fromAccount"),
                        rs.getString("toAccount"),
                        rs.getDate("date"),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency")
                ));
            }
            return objects;
        } catch (SQLException e) {
            _logger.error("Failed to populate array | " + e.getMessage());
        }
        return objects;
    }
}
