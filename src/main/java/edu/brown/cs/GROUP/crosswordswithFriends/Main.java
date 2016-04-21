package edu.brown.cs.GROUP.crosswordswithFriends;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import edu.brown.cs.GROUP.database.Database;
import edu.brown.cs.GROUP.words.CSVReader;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

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

    OptionParser parser = new OptionParser();

    // OptionSpec
    // parser.accepts("corpus").withRequiredArg().ofType(String.class);
    OptionSpec<String> database = parser.accepts("db").withRequiredArg()
        .ofType(String.class);
    OptionSpec<String> files = parser.nonOptions().ofType(String.class);
    OptionSet options;
    try {
      options = parser.parse(this.arguments);
    } catch (OptionException e) {
      System.err.println("ERROR: Please provide an argument to --db.");
      return;
    }

    // if (options.valuesOf(files).isEmpty()) {
    // System.out
    // .println("ERROR: Please provide a valid argument to --db.");
    // System.exit(1);
    // throw new FileNotFoundException();
    // }

    // if (this.arguments.length < 1) {
    // System.err.println("ERROR: Please specify at least one corpus file");
    // throw new FileNotFoundException();
    // }

    Database db = null;

    System.out.println(this.arguments.length);

    if (options.has("db") && options.hasArgument("db")) {
      String path = null;
      try {
        path = options.valueOf(database);

      } catch (Exception e) {
        System.out
            .println("ERROR: Please provide a valid argument to --db");
        throw new FileNotFoundException();
      }
      try {
        db = new Database(path);
      } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
        System.err.println(
            "ERROR: The database file was unable to be connected to.");
        return;
      }
    }

    else if (this.arguments.length < 2) {
      System.err.println("ERROR: Invalid number of arguments. USAGE: "
          + "./run --db <path_to_database> [optional] --corpus "
          + "<corpus1>...<corpusn>");
      throw new IOException();
    }

    // else if (options.has("corpus") && options.hasArgument("corpus")) {
    // ledistance = true;
    // try {
    // count = options.valueOf(led);
    // } catch (Exception e) {
    // System.out
    // .println("ERROR: Please provide a valid argument to --led");
    // System.exit(1);
    // }
    // }

    else {
      // Database db = null;
      assert (db != null);
      CSVReader reader = new CSVReader();
      try {
        reader.readtoDB(this.arguments[this.arguments.length - 1],
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
