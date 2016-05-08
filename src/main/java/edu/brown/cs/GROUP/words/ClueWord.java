package edu.brown.cs.GROUP.words;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface outlines the functionality of a "getter" object, which takes
 * care of getting information from the database.
 * @author smg1
 *
 */

public interface ClueWord {

  /**
   * Given a length, this method will return a list of all words with that
   * length.
   * @param size the length of the word.
   * @param conn the connection to the database
   * @return a list of words
   * @throws SQLException exception in case the word is not found
   */

  List<String> getWords(int size, Connection conn) throws SQLException;

  /**
   * Given a word, return the clue of that word.
   * @param word the word
   * @param conn the connection
   * @return the clue
   * @throws SQLException exception in case the clue is not found
   */

  String getClue(String word, Connection conn) throws SQLException;

}
