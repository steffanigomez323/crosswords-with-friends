package edu.brown.cs.GROUP.words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TXTReader {

  public TXTReader() {
  }

  public void readtoDB(File path, Connection conn)
      throws SQLException, IOException {

    FileInputStream fis = new FileInputStream(path);
    InputStreamReader isr = new InputStreamReader(fis, "UTF8");
    BufferedReader reader = new BufferedReader(isr);

    String query = "INSERT OR IGNORE INTO cluewords(word, length, clue) VALUES (?,?,?)";
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
      System.out.println("start batch");
      ps.executeBatch();
      System.out.println("end batch");
      ps.close();
      reader.close();
    }

  }

}
