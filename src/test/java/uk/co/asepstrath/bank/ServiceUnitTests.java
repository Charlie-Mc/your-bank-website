package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.DatabaseService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@JoobyTest(App.class)
public class ServiceUnitTests {

    @Mock
    private DataSource ds;

    @Mock
    private Logger logger;

    private DatabaseService databaseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        databaseService = new DatabaseService(ds, logger);
    }

    @Test
    @DisplayName("Database Service - Select Test")
    public void selectDBTest() throws SQLException {
        // mock ResultSet
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt("id")).thenReturn(1);

        // mock Connection
        Connection conn = mock(Connection.class);
        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(mock(PreparedStatement.class));
        when(conn.prepareStatement(anyString()).execute()).thenReturn(true);
        when(conn.prepareStatement(anyString()).getResultSet()).thenReturn(rs);

        // execute query
        ResultSet result = databaseService.executeQuery("SELECT id FROM test_table");

        // assert result
        assertNotNull(result);
        assertTrue(result.next());
        assertEquals(1, result.getInt("id"));
        assertFalse(result.next());
    }

    @Test
    @DisplayName("Database Service - NULL ResultSet Test")
    public void nullSelectTest() throws SQLException {
        // mock Connection
        Connection conn = mock(Connection.class);
        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(mock(PreparedStatement.class));
        when(conn.prepareStatement(anyString()).execute()).thenReturn(true);
        when(conn.prepareStatement(anyString()).getResultSet()).thenReturn(null);

        // execute query
        ResultSet result = databaseService.executeQuery("SELECT id FROM test_table");

        // assert result
        assertNull(result);
    }

    @Test
    @DisplayName("Database Service - NULL Connection Test")
    public void nullConnectionTest() throws SQLException {
        // mock Connection
        when(ds.getConnection()).thenReturn(null);

        // execute query
        ResultSet result = databaseService.executeQuery("SELECT id FROM test_table");

        // assert result
        assertNull(result);
    }

    @Test
    @DisplayName("Database Service - NULL PreparedStatement Test")
    public void nullPreparedStatementTest() throws SQLException {
        // mock Connection
        Connection conn = mock(Connection.class);
        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(null);

        // execute query
        ResultSet result = databaseService.executeQuery("SELECT id FROM test_table");

        // assert result
        assertNull(result);
    }

    @Test
    @DisplayName("Database Service - SQLException Test (Wrong number of parameters)")
    public void SQLExceptionTest() throws SQLException {
        // mock Connection
        Connection conn = mock(Connection.class);
        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(mock(PreparedStatement.class));

        // Call the method with the wrong number of parameters
        String sql = "SELECT id FROM test_table WHERE id = ? AND name = ?";

        ResultSet rs = databaseService.executeQuery(sql, 1);

        // assert result
        assertNull(rs);
    }
}
