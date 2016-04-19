package edu.brown.cs.GROUP.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class handles the initialization of the database connection.
 * @author smg1
 *
 */

public class Database {

  /**
   * The connection to the database.
   */

  private final Connection conn;

  /**
   * This is the constructor which initializes an instance of this class with a
   * connection to the database whose location is passed in as a string.
   * @param db the path to the database
   * @throws SQLException in case the connection cannot be made.
   * @throws ClassNotFoundException in case the class is not found
   */

  public Database(String db) throws SQLException, ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");

    String urlToDB = "jdbc:sqlite:" + db;
    Connection connect = DriverManager.getConnection(urlToDB);
    this.conn = connect;

    try (Statement stat = conn.createStatement()) {
      stat.execute("DROP TABLE IF EXISTS cluewords");
      stat.close();
    }

    String schema = "CREATE TABLE cluewords (" + "word TEXT, "
        + "length INT, " + "clue TEXT, "
        + "PRIMARY KEY (word, length, clue));";
    buildTable(schema);

  }

  /**
   * This method returns a connection to this database.
   * @return a connection to this database.
   */

  public Connection getConnection() {
    return conn;
  }

  /**
   * This method builds the database table from which we will be querying
   * information.
   * @param schema the string with the schema
   * @throws SQLException if for some reason we can't create the table
   */

  private void buildTable(String schema) throws SQLException {
    try (PreparedStatement prep = conn.prepareStatement(schema)) {
      prep.executeUpdate();
      prep.close();
    }

  }

  /**
   * This method closes a connection to this database.
   * @throws SQLException just in case the database cannot close the connection.
   */

  public void close() throws SQLException {
    conn.close();
  }
}
