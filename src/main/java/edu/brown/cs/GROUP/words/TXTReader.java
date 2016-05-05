package edu.brown.cs.GROUP.words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class reads clues and words from a tab delimited .txt file and puts them
 * into the database. This is only done once per file.
 * @author smg1
 *
 */

public class TXTReader {

  /**
   * This method reads from the file, checking to make sure the word length and
   * clue are not in the database already. If they are not, then the word and
   * its associated length and clue are stored in the database.
   * @param path the file path of the .txt file
   * @param conn the database connection
   * @throws SQLException in case the information cannot be stored in the
   *           database
   * @throws IOException in case the file cannot be opened
   */

  public void readtoDB(File path, Connection conn)
      throws SQLException, IOException {

    FileInputStream fis = new FileInputStream(path);
    InputStreamReader isr = new InputStreamReader(fis, "UTF8");
    BufferedReader reader = new BufferedReader(isr);

    String query = "INSERT OR IGNORE INTO "
        + "cluewords(word, length, clue) VALUES (?,?,?)";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] row = line.split("\t");
        if (row[0].length() == 0) {
          continue;
        }
        if (row[1].length() <= 1) {
          continue;
        }
        ps.setString(1, row[1].toLowerCase());
        ps.setInt(2, row[1].length());
        ps.setString(3, row[0]);

        ps.addBatch();

      }
      ps.executeBatch();
      ps.close();
      reader.close();
    }

  }

}
