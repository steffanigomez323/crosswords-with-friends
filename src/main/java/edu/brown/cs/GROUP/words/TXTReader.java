package edu.brown.cs.GROUP.words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * This class reads clues and words from a tab delimited .txt file and puts them
 * into the database. This is only done once per file.
 * @author smg1
 *
 */

public class TXTReader {

  private static final int day = 1;
  private static final int month = 1;
  private static final int year = 2009;

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

    Calendar cutoff = Calendar.getInstance();
    cutoff.set(year, month, day, 0, 0);

    String query = "INSERT OR IGNORE INTO "
        + "cluewords(word, length, clue) VALUES (?,?,?)";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      String line;
      while ((line = reader.readLine()) != null) {
        // System.out.println(line);
        String[] row = line.split("\t");
        if (row[0].length() == 0) {
          continue;
        }
        if (row[1].length() <= 1) {
          continue;
        }

        StringBuffer s = new StringBuffer("");
        if (Integer.parseInt(row[2]) <= 12) {
          s.append("20");
        } else {
          s.append("19");
        }
        s.append(row[2]);
        row[2] = s.toString();

        int m = Integer.parseInt(row[3]);
        int y = Integer.parseInt(row[2]);

        Calendar c = Calendar.getInstance();
        c.set(y, m, day, 0, 0);

        if (c.before(cutoff)) {
          continue;
        }

        // System.out.println("Adding clue: " + row[0] + " and answer: "
        // + row[1].toLowerCase());

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
