package edu.brown.cs.GROUP.crosswordswithFriends;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.GROUP.chat.Chat;
import edu.brown.cs.GROUP.database.Database;
import freemarker.template.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/** GUI class for web server handling. */
public class GUI {

  /** For converting to JSON. */

  private static HashMap<Integer, Crossword> crosswordCache =
      new HashMap<Integer, Crossword>();

  /** This is the database that holds the connection. */

  private Database db;

  /** This is the user id for the second player. */

  public static final AtomicInteger TWOPLAYERID = new AtomicInteger(1000);

  /** This is the user id for the first player. */

  public static final AtomicInteger ONEPLAYERID = new AtomicInteger(1001);

  /** Constructor starts server on instantiation.
   *
   * @param port Port number specified by command line or 4567 by default
   * @param d Database connection path */
  public GUI(int port, Database d) {
    Spark.port(port);
    db = d;
    runSparkServer();
  }

  /** This method checks whether the given word at the x-index, y-index,
   * orientation and user id is correct.
   *
   * @param word the word
   * @param x x-index
   * @param y y-index
   * @param orientation the orientation
   * @param id the id
   * @return boolean boolean */

  public static boolean checkValid(String word, int x, int y,
      Orientation orientation, Integer id) {
    if (!crosswordCache.containsKey(id)) {
      return false;
    }
    Crossword puzzle = crosswordCache.get(id);
    Box[][] crossword = puzzle.getArray();
    String wordInPuzzle = puzzle.getWord(x, y, orientation);
    if (word.length() != wordInPuzzle.length()) {
      return false;
    }
    for (int i = 0; i < word.length(); i++) {
      Box box = crossword[y][x];
      if (!box.checkVal(word.charAt(i))) {
        return false;
      }
      if (orientation == Orientation.ACROSS) {
        x++;
      } else {
        y++;
      }
    }


    return true;
  }

  /**
   * This method returns the crossword given the roomid from the cache.
   * @param roomId the room id
   * @return the crossword of that room
   */

  public static Crossword getCrossword(Integer roomId) {
    return crosswordCache.get(roomId);
  }

  /**
   * This method removes the crossword of a room from the cache.
   * @param roomId the room id
   */

  public static void removeCrossword(Integer roomId) {
    crosswordCache.remove(roomId);
  }


  /** This method gets an anagram of the answer of the word located at that.
   * @return The cache of all crosswords. */
  public static HashMap<Integer, Crossword> getCrosswordCache() {
    return crosswordCache;
  }

  /**
   * This method gets an anagram of the answer of the word located at that
   * x-index, y-index, and orientation and with the id.
   *
   * @param length the length of the word
   * @param x the x-index
   * @param y the y-index
   * @param orientation the orientation
   * @param id the id
   * @return anagram string */
  public static String getAnagram(int length, int x, int y,
      Orientation orientation, Integer id) {
    System.out.println("id: " + id);

    if (!crosswordCache.containsKey(id)) {
      System.out.println("no crosswrd");
      return "";
    }
    Crossword puzzle = crosswordCache.get(id);
    Box[][] crossword = puzzle.getArray();
    StringBuffer word = new StringBuffer("");
    for (int i = 0; i < length; i++) {
      word.append(String.valueOf(crossword[y][x].getLetter()));
      if (orientation == Orientation.ACROSS) {
        x++;
      } else {
        y++;
      }
    }
    Random random = new Random();
    String scrambled = word.toString();
    char[] a = scrambled.toCharArray();
    for (int i = 0; i < a.length - 1; i++) {
      int j = random.nextInt(a.length - 1);
      char temp = a[i];
      a[i] = a[j];
      a[j] = temp;
    }
    return new String(a);
  }

  /** This method returns the letter at x-index, y-index, and with that id.
   *
   * @param x x-index
   * @param y y-index
   * @param id cache id
   * @return the letter */

  public static char getLetter(int x, int y, Integer id) {
    if (!crosswordCache.containsKey(id)) {
      return '-';
    }
    Crossword puzzle = crosswordCache.get(id);
    Box[][] crossword = puzzle.getArray();
    Box box = crossword[y][x];
    return box.getLetter();
  }

  /** Creates engine for server.
   *
   * @return FreeMarker engine. */
  private static FreeMarkerEngine createEngine() {

    Configuration config = new Configuration();
    File templates =
        new File("src/main/resources/spark/template/freemarker");

    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.println("ERROR: Unable use src/main/resources/"
          + "spark/template/freemarker for template loading.");

      System.exit(1);
    }

    return new FreeMarkerEngine(config);
  }

  /** Runs the server. Organizes get and put requests. */
  private void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    try {
      Chat.initChatroom();
    } catch (IOException e) {
      e.printStackTrace();
    }
    FreeMarkerEngine freeMarker = createEngine();
    Spark.get("/home", new FrontHandler(), freeMarker);
    Spark.get("/two", new TwoHandler(db), freeMarker);
    Spark.get("/one", new OneHandler(db), freeMarker);
  }

  /** This class handles the front end of the main page. */

  private static class FrontHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {

      ImmutableMap<String, Object> variables =
          new ImmutableMap.Builder<String, Object>().build();

      return new ModelAndView(variables, "home.ftl");
    }

  }

  /** Handler for serving main page. */
  private static class TwoHandler implements TemplateViewRoute {

    /**
     * This is the private instance variable of the database for this class.
     */

    private Database db;

    /**
     * This is the constructor for this class which takes a database and sets it
     * to the private instance variable.
     * @param d the database
     */

    public TwoHandler(Database d) {
      this.db = d;
    }

    /**
     * This method creates a new crossword object.
     * @return the crossword
     */

    private Crossword createCrossword() {
      List<String> originalList = db.getAllUnderNine();

      return new Crossword(originalList, db);
    }

    @Override
    public ModelAndView handle(Request req, Response res) {

      String player = "ACROSS";

      Integer id2 = TWOPLAYERID.get();

      Crossword puzzle = crosswordCache.get(id2);

      if (puzzle == null) {
        puzzle = createCrossword();
        player = "DOWN";
      } else if (puzzle.getPlayers() != 2) {
        puzzle.addPlayer();
      } else {
        TWOPLAYERID.set(ONEPLAYERID.get());
        id2 = TWOPLAYERID.get();
        puzzle = createCrossword();
        player = "DOWN";
      }

      List<Word> toPass = puzzle.getFinalList();
      Chat.setCensorWords(id2, toPass);

      Box[][] crossword = puzzle.getArray();
      System.out.println(puzzle);

      crosswordCache.put(id2, puzzle);

      ImmutableMap<String, Object> variables =
          new ImmutableMap.Builder<String, Object>()
          .put("crossword", crossword).put("id", id2.toString())
          .put("player", player).put("roomNumber", id2.toString())
          .build();

      return new ModelAndView(variables, "crossword.ftl");
    }

  }

  /** Handler for serving main page. */
  private static class OneHandler implements TemplateViewRoute {

    /**
     * This is the private instance variable of the database for this class.
     */

    private Database db;

    /**
     * This is the constructor for this class which takes a database and sets it
     * to the private instance variable.
     * @param d the database
     */

    public OneHandler(Database d) {
      this.db = d;
    }

    /**
     * This method creates a new crossword object.
     * @return returns the new crossword object
     */

    private Crossword createCrossword() {
      List<String> originalList = db.getAllUnderNine();
      return new Crossword(originalList, db);
    }

    @Override
    public ModelAndView handle(Request req, Response res) {

      Integer id2 = ONEPLAYERID.getAndIncrement();

      while (crosswordCache.containsKey(id2)) {
        id2 = ONEPLAYERID.getAndIncrement();
      }

      Crossword puzzle = createCrossword();
      puzzle.addPlayer();

      Box[][] crossword = puzzle.getArray();
      crosswordCache.put(id2, puzzle);

      ImmutableMap<String, Object> variables =
          new ImmutableMap.Builder<String, Object>()
          .put("crossword", crossword).put("id", id2.toString())
          .put("roomNumber", id2.toString()).build();

      return new ModelAndView(variables, "crossword_single.ftl");
    }

  }

}
