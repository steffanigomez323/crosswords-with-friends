package edu.brown.cs.GROUP.words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles reading information from the given CVS corpus and putting
 * it into the database.
 * @author smg1
 *
 */

public class CSVReader {

  /**
   * Private instance variable for 3.
   */

  private static final int THREE = 3;

  /**
   * This is the constructor that begins an instance of this class.
   */

  public CSVReader() {
  }

  /**
   * This method reads information from the .csv file and puts it into the
   * database.
   * @param path file path to read from
   * @param conn the database connection
   * @throws SQLException in case we are unable to add to the database
   */

  public void readtoDB(File path, Connection conn) throws SQLException {

    try (FileInputStream fis = new FileInputStream(path)) {
      try (InputStreamReader isr = new InputStreamReader(fis, "UTF8")) {
        try (BufferedReader reader = new BufferedReader(isr)) {

          String line = reader.readLine();
          if (line != null) {
            List<String> headers = Arrays.asList(line.split(","));

            int nameidx = headers.indexOf("Word");
            int clueidx = headers.indexOf("Clue");

            String query = "INSERT OR IGNORE INTO "
                + "cluewords(word, length, clue) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {

              while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");

                ps.setString(1, row[nameidx].toLowerCase());
                ps.setInt(2, row[nameidx].length());
                ps.setString(THREE, row[clueidx].toLowerCase());

                ps.addBatch();

              }
              ps.executeBatch();
              ps.close();
              reader.close();
            }
          }
        }
      }
    } catch (IOException e) {
      System.err.println("ERROR: file cannot be read from.");
      return;
    }
  }

}
