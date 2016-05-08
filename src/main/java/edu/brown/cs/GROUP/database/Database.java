package edu.brown.cs.GROUP.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.GROUP.words.ClueWordGetter;

/**
 * This class handles the initialization of the database connection.
 * @author smg1
 *
 */

public class Database {

  /**
   * This is the length of the crossword, which is a 9x9 grid.
   */

  private static final int LENGTH = 9;

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

    String schema =
        "CREATE TABLE IF NOT EXISTS cluewords (" + "word TEXT, "
            + "length INT, " + "clue TEXT, " + "PRIMARY KEY (word));";
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
   * This method returns all the words whose length is at most 9.
   * @return the list
   */

  public List<String> getAllUnderNine() {
    List<String> words = new ArrayList<String>();
    ClueWordGetter getter = new ClueWordGetter();
    try {
      words = getter.getWords(LENGTH, conn);
    } catch (SQLException e) {
      System.out.println("ERROR: Problem querying the database");
    }
    return words;
  }

  /**
   * This method returns the number of all the words in the data whose length is
   * at most 9.
   * @return the length
   */

  public int getAllUnderNineLength() {
    int length = 0;
    String query = "SELECT count(*) FROM cluewords WHERE length<=" + LENGTH
        + "ORDER BY length DESC;";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          length = rs.getInt(1);
        }
      }

    } catch (SQLException e) {
      System.out.println("ERROR: Problem querying the database");
    }
    return length;
  }

  /**
   * This is the method that returns the clue for a given word.
   * @param word the word
   * @return the clue
   */

  public String getClue(String word) {
    String clue = "";
    ClueWordGetter getter = new ClueWordGetter();
    try {
      clue = getter.getClue(word, conn);
    } catch (SQLException e) {
      System.out.println("ERROR: Problem querying the database");
    }

    return clue;

  }

  /**
   * This method closes a connection to this database.
   * @throws SQLException just in case the database cannot close the connection.
   */

  public void close() throws SQLException {
    conn.close();
  }
}
