package edu.brown.cs.GROUP.database;

import java.sql.SQLException;

import org.junit.Test;

public class DatabaseTest {

  /**
   * This test checks to make sure that the database class is beginning the
   * connection properly.
   */

  @Test
  public void checkConnection() {
    try {
      Database db = new Database("data/cluewords.sqlite3");
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void getAllUnderNineTest() {

  }

  @Test
  public void getClueTest() {

  }

}
