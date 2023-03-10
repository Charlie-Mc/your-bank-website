package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.DatabaseService;
import uk.co.asepstrath.bank.models.Account;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@JoobyTest(App.class)
class DatabaseServiceTest {

    @Mock
    private DataSource ds;
    @Mock
    private Logger logger;

    @InjectMocks
    private DatabaseService db;

    @Test
    @DisplayName("Test clean() method has success")
    public void test_clean_string_success() {
        String input = "     test\n";
        String expected = "test";

        String actual = DatabaseService.clean(input);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get database connection")
    public void test_get_connection_success() throws SQLException {
        Connection conn = mock(Connection.class);
        when(ds.getConnection()).thenReturn(conn);

        Connection actual = db.getConnection();

        assertEquals(conn, actual);
    }

    @Test
    @DisplayName("Get a null database connection")
    public void test_get_connection_null() throws SQLException {
        when(ds.getConnection()).thenReturn(null);

        Connection actual = db.getConnection();

        assertNull(actual);
    }

    @Test
    @DisplayName("Get all accounts")
    public void testSelectAll() throws Exception {
        // Set up test data
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString("id")).thenReturn("1");
        when(rs.getString("name")).thenReturn("John");
        when(rs.getString("balance")).thenReturn("100.00");
        when(rs.getString("currency")).thenReturn("USD");
        when(rs.getString("accountType")).thenReturn("checking");
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);
        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);

        // Call the method under test
        ArrayList<Account> accounts = db.selectAll(Account.class, "users");

        // Verify the results
        assertEquals(1, accounts.size());
        Account account = accounts.get(0);
        assertEquals("1", account.getId());
        assertEquals("John", account.getName());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
        assertEquals("USD", account.getCurrency());
        assertEquals("checking", account.getAccountType());
    }

    @Test
    @DisplayName("Get all accounts with no results")
    public void testSelectAllNoResults() throws Exception {
        // Set up test data
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);
        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);

        // Call the method under test
        ArrayList<Account> accounts = db.selectAll(Account.class, "users");

        // Verify the results
        assertEquals(0, accounts.size());
    }

    @Test
    @DisplayName("Get all accounts with an exception")
    public void testSelectAllException() throws Exception {
        // Set up test data
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(null);
        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);

        // Call the method under test
        ArrayList<Account> accounts = db.selectAll(Account.class, "users");

        // Verify the results
        assertNull(accounts);
    }

    @Test
    @DisplayName("Get specific account")
    public void test_selectById() throws SQLException {
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString("id")).thenReturn("1");
        when(rs.getString("name")).thenReturn("John");
        when(rs.getString("balance")).thenReturn("100.00");
        when(rs.getString("currency")).thenReturn("USD");
        when(rs.getString("accountType")).thenReturn("checking");
        when(stmt.executeQuery()).thenReturn(rs);

        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);

        Account account = db.selectById(Account.class, "users", "1");

        assertEquals("1", account.getId());
        assertEquals("John", account.getName());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
        assertEquals("USD", account.getCurrency());
        assertEquals("checking", account.getAccountType());
    }

    @Test
    @DisplayName("Get specific account with no results")
    public void test_selectByIdNoResults() throws SQLException {
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);

        Account account = db.selectById(Account.class, "users", "1");

        assert(Objects.equals(account.toString(), "Name: null Balance: null"));
    }

    @Test
    @DisplayName("Get specific account with an exception")
    public void test_selectByIdException() throws SQLException {
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(null);

        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);

        Account account = db.selectById(Account.class, "users", "1");

        assertNull(account);
    }

    @Test
    @DisplayName("Insert account")
    public void test_insert_account() throws SQLException {
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn(0);

        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);

        Account account = new Account("1", "John", new BigDecimal("100.00"), "USD", "checking");
        String tableName = "accounts";
        String[] columns = {"id", "name", "balance", "currency", "accountType"};
        String[] values = {account.getId(), account.getName(), account.getBalance().toString(), account.getCurrency(), account.getAccountType()};
        boolean result = db.insert(tableName, columns, values);

        assertTrue(result);
    }

    @Test
    @DisplayName("Create table")
    public void test_table_creation() throws SQLException {
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn(0);

        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);

        String tableName = "accounts";
        String[] columns = {"id", "name", "balance", "currency", "accountType"};
        String[] types = {"VARCHAR(255)", "VARCHAR(255)", "DECIMAL(10,2)", "VARCHAR(255)", "VARCHAR(255)"};
        boolean result = db.createTable(tableName, columns, types);

        assertTrue(result);
    }

}