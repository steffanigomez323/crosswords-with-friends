package edu.brown.cs.GROUP.crosswordswithFriends;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import edu.brown.cs.GROUP.database.Database;
import edu.brown.cs.GROUP.words.CVSReader;

/**
 * This class handles starting the program, the database, and the GUI.
 * @author smg1
 *
 */

public final class Main {

  /**
   * Initializes the program being run.
   * @param args the arguments from the command line
   */
  public static void main(String[] args) {
    // System.out.println("Hi");
    // new GUI(9999);

    try {
      new Main(args).run();
    } catch (IOException e) {
      System.exit(1);
    }

  }

  /**
   * This is the private instance variable of the arguments from the command
   * line.
   */

  private String[] arguments;

  /**
   * That is the port number.
   */

  private static final int PORT = 9999;

  /**
   * This constructor takes the arguments from the command line and sets it to
   * the private instance variable.
   * @param args arguments from command line interface
   */

  private Main(String[] args) {
    this.arguments = args;
  }

  /**
   * This is the method that runs the program, starting the database and the
   * GUI.
   * @throws IOException when the corpus file is cannot be opened
   */

  private void run() throws IOException {

    if (this.arguments.length < 1) {
      System.err.println("ERROR: Please specify at least one corpus file");
      throw new FileNotFoundException();
    } else if (this.arguments.length > 1) {
      System.err.println("ERROR: Invalid number of arguments. USAGE: "
          + "./run <path_to_corpus>");
      throw new IOException();
    } else {
      Database db = null;
      try {
        db = new Database("data/cluewords.sqlite3");
        // db = new Database(this.arguments[this.arguments.length - 1]);
      } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
        System.err.println(
            "ERROR: The database file was unable to be connected to.");
        return;
      }
      assert (db != null);
      CVSReader reader = new CVSReader();
      try {
        reader.readtoDB(
            new File(this.arguments[this.arguments.length - 1]),
            db.getConnection());
      } catch (SQLException e) {
        System.err
            .println("ERROR: Cannot write information to the database.");
        return;
      } catch (IOException e) {
        System.err
            .println("ERROR: Cannot read from the corpus file given.");
        return;
      }

      new GUI(PORT, db);

    }
  }

}
