package edu.brown.cs.GROUP.words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

public class CVSReader {

  /**
   * This is the reader that reads the file that it is given.
   */

  private BufferedReader reader;

  /**
   * This is the File that holds the file specified in the string.
   */

  private File filename;

  /**
   * This is the constructor that begins an instance of this class.
   */

  public CVSReader() {
  }

  /**
   * This method reads information from the cvs file and puts it into the
   * database.
   * @param file file to read from
   * @param conn the database connection
   * @throws SQLException in case we are unable to add to the database
   * @throws IOException in case we are unable to read from the file
   */

  public void readtoDB(File file, Connection conn)
      throws SQLException, IOException {
    filename = file;
    reader = new BufferedReader(new FileReader(file));

    String line = reader.readLine();
    List<String> headers = Arrays.asList(line.split(","));

    int nameidx = headers.indexOf("Word");
    int clueidx = headers.indexOf("Clue");
    // int ex_idx = headers.indexOf("Example");

    String query = "INSERT INTO cluewords VALUES (?,?,?)";
    PreparedStatement ps = conn.prepareStatement(query);

    while ((line = reader.readLine()) != null) {
      String[] row = line.split(",");

      ps.setString(1, row[nameidx]);
      ps.setInt(2, row[nameidx].length());
      ps.setString(3, row[clueidx]);

      ps.addBatch();

    }
    ps.executeBatch();
    ps.close();
  }

}
