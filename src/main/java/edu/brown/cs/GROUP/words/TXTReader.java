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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads clues and words from a tab delimited .txt file and puts them
 * into the database. This is only done once per file.
 * @author smg1
 *
 */

public class TXTReader {

  /**
   * This is the day that clues before will be filtered out.
   */

  private static final int DAY = 1;

  /**
   * This is the month that clues before this will be filtered out.
   */

  private static final int MONTH = 1;

  /**
   * This is the year that clues before this will be filtered out.
   */

  private static final int YEAR = 2009;

  /**
   * Instance variable for three.
   */

  private static final int THREE = 3;

  /**
   * Instance variable for setting years 00-11 to 2000-2012.
   */

  private static final int TWELVE = 12;

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
    cutoff.set(YEAR, MONTH, DAY, 0, 0);

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

        String pattern = "([0-9]+)-Across|([0-9]+)-Down";

        Pattern r = Pattern.compile(pattern);

        Matcher matcher = r.matcher(line);
        if (matcher.find()) {
          continue;
        }

        StringBuffer s = new StringBuffer("");
        if (Integer.parseInt(row[2]) <= TWELVE) {
          s.append("20");
        } else {
          s.append("19");
        }
        s.append(row[2]);
        row[2] = s.toString();

        int m = Integer.parseInt(row[THREE]);
        int y = Integer.parseInt(row[2]);

        Calendar c = Calendar.getInstance();
        c.set(y, m, DAY, 0, 0);

        if (c.before(cutoff)) {
          continue;
        }

        ps.setString(1, row[1].toLowerCase());
        ps.setInt(2, row[1].length());
        ps.setString(THREE, row[0]);

        ps.addBatch();

      }
      ps.executeBatch();
      ps.close();
      reader.close();
    }

  }

}
