package edu.brown.cs.GROUP.words;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class functions as a "getter" class, retrieving the information from the
 * database, including words up to a certain length, and clues associated with a
 * particular word.
 * @author smg1
 *
 */

public class ClueWordGetter implements ClueWord {

  @Override
  public List<String> getWords(int length, Connection conn)
      throws SQLException {
    List<String> words = new ArrayList<String>();
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT " + "word FROM cluewords WHERE length<=? "
            + "ORDER BY length DESC;")) {
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
  public String getClue(String word, Connection conn) throws SQLException {
    String clue = null;
    try (PreparedStatement prep = conn
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
