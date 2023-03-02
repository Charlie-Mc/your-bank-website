package uk.co.asepstrath.bank.services;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.models.Transaction;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseService {
    private final Logger logger;
    private final DataSource dataSource;

    public DatabaseService(DataSource ds, Logger lgr) {
        dataSource = ds;
        logger = lgr;
    }

    /**
     * Get all records from a table
     * @param classType The class type to return
     * @param tableName The table to select from
     * @return A list of objects of the class type
     * @param <E> The class type to return
     */
    public <E> ArrayList<E> selectAll(Class<E> classType, String tableName) {
        tableName = clean(tableName);

        try (Connection conn = getConnection()) {
            if (conn == null) {
                return null;
            }

            String query = "SELECT * FROM " + tableName;
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();
            if (rs == null) {
                return null;
            }

            E object = classType.newInstance();
            ArrayList<E> results = new ArrayList<>();
            while (rs.next()) {
                if (object instanceof Account) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String balance = rs.getString("balance");
                    String currency = rs.getString("currency");
                    String accountType = rs.getString("accountType");
                    object = (E) new Account(id, name, new BigDecimal(balance), currency, accountType);
                } else if (object instanceof Transaction) {
                    String id = rs.getString("id");
                    String fromAccount = rs.getString("fromAccount");
                    String toAccount = rs.getString("toAccount");
                    String amount = rs.getString("amount");
                    String currency = rs.getString("currency");
                    Date date = rs.getDate("date");
                    object = (E) new Transaction(id, fromAccount, toAccount, date, new BigDecimal(amount), currency);
                }
                results.add(object);
            }
            conn.close();
            stmt.close();
            rs.close();
            return results;
        } catch (SQLException e) {
            this.logger.error("Error getting database connection: " + e.getMessage());
            return null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a single record from a table
     * @param type The class type to return
     * @param tableName The table to select from
     * @param id The ID of the record to select
     * @return An object of the class type
     * @param <E> The class type to return
     */
    public <E> E selectById(Class<E> type, String tableName, String id) {
        tableName = clean(tableName);

        try (Connection conn = getConnection()) {
            if (conn == null) {
                return null;
            }

            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs == null) {
                return null;
            }

            E object = type.newInstance();
            if (rs.next()) {
                if (object instanceof Account) {
                    String id1 = rs.getString("id");
                    String name = rs.getString("name");
                    String balance = rs.getString("balance");
                    String currency = rs.getString("currency");
                    String accountType = rs.getString("accountType");
                    object = (E) new Account(id1, name, new BigDecimal(balance), currency, accountType);
                } else if (object instanceof Transaction) {
                    String id2 = rs.getString("id");
                    String fromAccount = rs.getString("fromAccount");
                    String toAccount = rs.getString("toAccount");
                    BigDecimal amount = rs.getBigDecimal("amount");
                    String currency = rs.getString("currency");
                    Date date = rs.getDate("date");
                    object = (E) new Transaction(id2, fromAccount, toAccount, date, amount, currency);
                }
            }

            conn.close();
            stmt.close();
            rs.close();
            return object;
        } catch (SQLException e) {
            this.logger.error("Error getting database connection: " + e.getMessage());
            return null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert record into a table
     * @param tableName The table to insert into
     * @param columns The columns to insert into
     * @param values The values to insert
     * @return True if successful, false otherwise
     */
    public boolean insert(String tableName, String[] columns, String[] values) {
        tableName = clean(tableName);
        for (int i = 0; i < columns.length; i++) {
            columns[i] = clean(columns[i]);
        }

        try (Connection conn = getConnection()) {
            if (conn == null) {
                return false;
            }

            StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
            for (int i = 0; i < columns.length; i++) {
                query.append(columns[i]);
                if (i != columns.length - 1) {
                    query.append(", ");
                }
            }
            query.append(") VALUES (");
            for (int i = 0; i < values.length; i++) {
                query.append("?");
                if (i != values.length - 1) {
                    query.append(", ");
                }
            }
            query.append(")");

            PreparedStatement stmt = conn.prepareStatement(query.toString());
            for (int i = 0; i < values.length; i++) {
                stmt.setString(i + 1, values[i]);
            }

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            this.logger.error("Error getting database connection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create a new table
     * @param tableName The name of the table to create
     * @param columns The columns to create
     * @param types The types of the columns
     * @return True if successful, false otherwise
     */
    public boolean createTable(String tableName, String[] columns, String[] types) {
        tableName = clean(tableName);

        try (Connection conn = getConnection()) {
            if (conn == null) {
                return false;
            }

            StringBuilder query = new StringBuilder("CREATE TABLE " + tableName + " (");
            for (int i = 0; i < columns.length; i++) {
                query.append(columns[i]).append(" ").append(types[i]);
                if (i != columns.length - 1) {
                    query.append(", ");
                }
            }
            query.append(")");

            PreparedStatement stmt = conn.prepareStatement(query.toString());

            stmt.executeUpdate();

            if (stmt.getUpdateCount() == 0) {
                return true;
            }
        } catch (SQLException e) {
            this.logger.error("Error getting database connection: " + e.getMessage());
            return false;
        }
        return false;

    }

    // Getters
    public Logger getLogger() {
        return logger;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    // Utility Methods
    /**
     * Clean a string by removing all whitespace and newlines
     * @param input The string to clean
     * @return String - The cleaned string
     */
    public static String clean(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", "");
    }

    /**
     * Initialise the database connection
     * @return Connection - The database connection
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error getting database connection: " + e.getMessage());
            return null;
        }
    }
}