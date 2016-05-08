package edu.brown.cs.GROUP.crosswordswithFriends;

import edu.brown.cs.GROUP.database.Database;
import edu.brown.cs.GROUP.words.CSVReader;
import edu.brown.cs.GROUP.words.TXTReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/** This class handles starting the program, the database, and the GUI.
 *
 * @author smg1 */

public final class Main {

  /** Initializes the program being run.
   *
   * @param args the arguments from the command line */
  public static void main(String[] args) {
    try {
      new Main(args).run();
    } catch (IOException e) {
      System.exit(1);
    }

  }

  /** This is the private instance variable of the arguments from the command
   * line. */

  private String[] arguments;

  /** That is the port number. */

  private static final int PORT = 9998;

  /** This constructor takes the arguments from the command line and sets it to
  /**
   * The argument length.
   */

  private static final int LENGTH = 3;

  /**
   * This constructor takes the arguments from the command line and sets it to
   * the private instance variable.
   *
   * @param args arguments from command line interface */

  private Main(String[] args) {
    this.arguments = args;
  }

  /** This is the method that runs the program, starting the database and the
   * GUI.
   *
   * @throws IOException when the corpus file is cannot be opened */

  private void run() throws IOException {

    OptionParser parser = new OptionParser();
    OptionSpec<String> database =
        parser.accepts("db").withRequiredArg().ofType(String.class);
    OptionSpec<String> files = parser.nonOptions().ofType(String.class);
    OptionSet options;
    try {
      options = parser.parse(this.arguments);
    } catch (OptionException e) {
      System.err.println("ERROR: Please provide an argument to --db.");
      return;
    }

    Database db = null;

    if (options.has("db") && options.hasArgument("db")) {
      String path = null;
      try {
        path = options.valueOf(database);

      } catch (Exception e) {
        System.out.println("ERROR: Please provide a valid argument to --db");
        throw new FileNotFoundException();
      }
      try {
        db = new Database(path);
      } catch (ClassNotFoundException | SQLException e) {
        System.err
            .println("ERROR: The database file was unable to be connected to.");
        return;
      }
    }

    if (this.arguments.length < LENGTH) {
      System.err.println("ERROR: Invalid number of arguments. USAGE: "
          + "./run --db <path_to_database> " + "<corpus1>...<corpusn>");
      throw new IOException();
    } else {
      CSVReader csvreader = new CSVReader();
      TXTReader txtreader = new TXTReader();
      if (!options.valuesOf(files).isEmpty()) {
        assert db != null;
        try {
          for (String s : options.valuesOf(files)) {
            File filename = new File(s);
            if (filename.getName().endsWith(".txt")) {
              txtreader.readtoDB(filename, db.getConnection());
            } else if (filename.getName().endsWith(".csv")) {
              csvreader.readtoDB(filename, db.getConnection());
            } else {
              System.err
                  .println("ERROR: The given file must be either .txt file or "
                      + "a .csv file");
              return;
            }
          }
        } catch (SQLException e) {
          e.printStackTrace();
          System.err
              .println("ERROR: Cannot write information to the database.");
          return;
        } catch (IOException e) {
          System.err.println("ERROR: Cannot read from the corpus file given.");
          return;
        }
      } else {
        System.err.println("ERROR: There must be at least one corpus file "
            + "to start the program with.");
        return;
      }
      new GUI(PORT, db);
      try {
        InputStreamReader isr = new InputStreamReader(System.in, "UTF8");
        BufferedReader sysreader = new BufferedReader(isr);
        String input = sysreader.readLine();
        while (input != null && !input.equals("")) {
          String[] ip = input.split(" ");
          for (String s : ip) {
            Path file = Paths.get(s);
            try {
              if (Files.isRegularFile(file) && Files.isReadable(file)) {
                File filename = new File(s);
                if (filename.getName().endsWith(".txt")) {
                  txtreader.readtoDB(filename, db.getConnection());
                } else if (filename.getName().endsWith(".csv")) {
                  csvreader.readtoDB(filename, db.getConnection());
                } else {
                  System.err
                      .println("ERROR: The given file must be either"
                          + " .txt file or a .csv file");
                  return;
                }
              } else {
                System.err
                    .println("ERROR: The file entered is not a file accessable "
                        + "by this program.");
              }
            } catch (IOException e) {
              System.err
                  .println("ERROR: The given file is not a file accessable "
                      + "by this program.");
            } catch (SQLException e) {
              System.err
                  .println("ERROR: The database was unable to be "
                      + "connected to.");
              throw new IOException();
            }
          }
          input = sysreader.readLine();
        }
      } catch (IOException e) {
        System.err
            .println("ERROR: Unable to read input from the command line.");
        throw new IOException();
      }
    }

  }

}
