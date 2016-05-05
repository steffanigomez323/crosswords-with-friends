package edu.brown.cs.GROUP.words;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.GROUP.database.Database;

/**
 * This class functions as a "getter" class, retrieving the information from the
 * database, including words up to a certain length, and clues associated with a
 * particular word.
 * @author smg1
 *
 */

public class ClueWordGetter implements ClueWord {

  /**
   * This is the database which holds the database connection.
   */
  private Database db;

  /**
   * This is the constructor, which sets the database that is passed in to the
   * private instance variable of this class.
   * @param d the database
   */

  public ClueWordGetter(Database d) {
    this.db = d;
  }

  @Override
  public List<String> getWord(int length) throws SQLException {
    List<String> words = new ArrayList<String>();
    try (PreparedStatement prep = db.getConnection().prepareStatement(
        "SELECT " + "word FROM cluewords WHERE length<=?;")) {
      prep.setInt(1, length);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          words.add(rs.getString(1));
        }
      }
    }
    return words;
  }

  @Override
  public String getClue(String word) throws SQLException {
    String clue = null;
    try (PreparedStatement prep = db.getConnection()
        .prepareStatement("SELECT clue FROM cluewords WHERE word=?;")) {
      prep.setString(1, word);
      try (ResultSet rs = prep.executeQuery()) {
        clue = rs.getString(1);
      }
    }
    assert clue != null;
    return clue;
  }

}
