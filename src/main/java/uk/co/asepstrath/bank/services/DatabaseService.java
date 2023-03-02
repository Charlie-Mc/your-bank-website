package uk.co.asepstrath.bank.services;

import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

            String query = "SELECT * FROM ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, tableName);

            ResultSet rs = stmt.executeQuery();
            if (rs == null) {
                return null;
            }

            ArrayList<E> results = new ArrayList<>();
            while (rs.next()) {
                results.add(classType.cast(rs));
            }

            conn.close();
            stmt.close();
            rs.close();
            return results;
        } catch (SQLException e) {
            this.logger.error("Error getting database connection: " + e.getMessage());
            return null;
        }
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
    private String clean(String input) {
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