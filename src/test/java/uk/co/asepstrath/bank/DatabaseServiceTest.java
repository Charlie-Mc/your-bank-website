package uk.co.asepstrath.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.models.Account;
import uk.co.asepstrath.bank.services.DatabaseService;

public class DatabaseServiceTest {

    @Mock
    private DataSource ds;

    @Mock
    private Logger logger;

    @Mock
    private Connection conn;

    @Mock
    private PreparedStatement stmt;

    @Mock
    private ResultSet rs;

    private DatabaseService dbService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        dbService = new DatabaseService(ds, logger);
    }

    @Test
    public void testExecuteQuery_success() throws SQLException {
        String query = "SELECT * FROM accounts WHERE id=?";
        Object[] params = { "1234" };
        when(ds.getConnection()).thenReturn(conn);
        when(conn.isClosed()).thenReturn(false);
        when(conn.prepareStatement(query)).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString("id")).thenReturn("1234");
        when(rs.getString("name")).thenReturn("John Doe");
        when(rs.getBigDecimal("balance")).thenReturn(new BigDecimal("1000.00"));
        when(rs.getString("currency")).thenReturn("USD");
        when(rs.getString("accountType")).thenReturn("checking");
        Account ac = new Account("1234", "John Doe", new BigDecimal("1000.00"), "USD", "checking");
        Account expected = new Account(
            rs.getString("id"),
            rs.getString("name"),
            rs.getBigDecimal("balance"),
            rs.getString("currency"),
            rs.getString("accountType")
        );
        assertEquals(ac.toString(), expected.toString());
    }

    @Test
    public void testExecuteQuery_failure() throws SQLException {
        String query = "SELECT * FROM accounts WHERE id=?";
        Object[] params = { "1234" };
        when(ds.getConnection()).thenReturn(conn);
        when(conn.isClosed()).thenReturn(false);
        when(conn.prepareStatement(query)).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertFalse(rs.next());
    }

    @Test
    public void testExecuteUpdate_success() throws SQLException {
        String query = "INSERT INTO accounts (id, name, balance, currency, accountType) VALUES (?, ?, ?, ?, ?)";
        Object[] params = { "1234", "John Doe", new BigDecimal("1000.00"), "USD", "checking" };
        when(ds.getConnection()).thenReturn(conn);
        when(conn.isClosed()).thenReturn(false);
        when(conn.prepareStatement(query)).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1);
        assertEquals(1, stmt.executeUpdate());
    }

    @Test
    public void testExecuteUpdate_failure() throws SQLException {
        String query = "INSERT INTO accounts (id, name, balance, currency, accountType) VALUES (?, ?, ?, ?, ?)";
        Object[] params = { "1234", "John Doe", new BigDecimal("1000.00"), "USD", "checking" };
        when(ds.getConnection()).thenReturn(conn);
        when(conn.isClosed()).thenReturn(false);
        when(conn.prepareStatement(query)).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(0);
        assertEquals(0, stmt.executeUpdate());
    }
}