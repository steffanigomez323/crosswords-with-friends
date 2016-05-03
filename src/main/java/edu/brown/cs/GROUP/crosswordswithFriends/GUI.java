package edu.brown.cs.GROUP.crosswordswithFriends;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.GROUP.chat.Chat;
import edu.brown.cs.GROUP.database.Database;
import freemarker.template.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
  private static HashMap<Integer, Crossword> crosswordCache;

  private Database db;
  public static AtomicInteger id;

  /**
   * Constructor starts server on instantiation.
   *
   * @param port Port number specified by command line or 4567 by default
   * @param d Database connection path
   */
  public GUI(int port, Database d) {
    Spark.port(port);
    db = d;
    id = new AtomicInteger(1000);
    runSparkServer();
    crosswordCache = new HashMap<Integer, Crossword>();
  }

  public static boolean checkValid(String word, int x, int y,  Orientation orientation, Integer id){
    if (!crosswordCache.containsKey(id)) {
      return false;
    }
    Crossword puzzle = crosswordCache.get(id);
    Box[][] crossword = puzzle.getArray();
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
   * Creates engine for server.
   *
   * @return FreeMarker engine.
   */
  private static FreeMarkerEngine createEngine() {

    Configuration config = new Configuration();
    File templates = new File(
        "src/main/resources/spark/template/freemarker");

    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.println("ERROR: Unable use src/main/resources/"
          + "spark/template/freemarker for template loading.");

      System.exit(1);
    }

    return new FreeMarkerEngine(config);
  }

  /**
   * Runs the server. Organizes get and put requests.
   * @throws IOException
   */
  private void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    try {
      Chat.initChatroom();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    FreeMarkerEngine freeMarker = createEngine();
    Spark.get("/home", new FrontHandler(), freeMarker);
    Spark.get("/two", new TwoHandler(db), freeMarker);
    Spark.get("/one", new OneHandler(db), freeMarker);
    Spark.get("/chatroom", new ChatHandler(), freeMarker);
  }

  private static class FrontHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {

      ImmutableMap<String, Object> variables = new ImmutableMap.Builder<String, Object>().build();

      return new ModelAndView(variables, "main.ftl");
    }

  }


  /** Handler for serving main page. */
  private static class TwoHandler implements TemplateViewRoute {

    private Database db;

    public TwoHandler(Database db) {
      this.db = db;
    }

    private Crossword createCrossword(){
      List<String> originalList = db.getAllUnderNine();
      return new Crossword(originalList, db);
    }

    @Override
    public ModelAndView handle(Request req, Response res) {

      String player = "ACROSS";

      Integer id2 = id.get();

      Crossword puzzle = crosswordCache.get(id2);

      if (puzzle == null){
        puzzle = createCrossword();
        player = "DOWN";
      } else if (puzzle.getPlayers() != 2){
        puzzle.addPlayer();
      } else {
        while (puzzle.getPlayers() == 2) {
          id2 = id.incrementAndGet();
          puzzle = crosswordCache.get(id2);
          if (puzzle == null){
            puzzle = createCrossword();
          }
        }
        player = "DOWN";
      }

      System.out.println("2 player : "+player);
      System.out.println(id2);

      List<Word> toPass = puzzle.getFinalList();
      Chat.setCensorWords(id2, toPass);

      Box[][] crossword = puzzle.getArray();
      System.out.println(puzzle);

      crosswordCache.put(id2, puzzle);

      ImmutableMap<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("crossword", crossword).put("id", id2.toString()).put("player", player)
          .put("roomNumber", id2.toString()).build();

      return new ModelAndView(variables, "crossword.ftl");
    }

  }

  /** Handler for serving main page. */
  private static class OneHandler implements TemplateViewRoute {

    private Database db;

    public OneHandler(Database db) {
      this.db = db;
    }

    private Crossword createCrossword(){
      List<String> originalList = db.getAllUnderNine();
      return new Crossword(originalList, db);
    }

    @Override
    public ModelAndView handle(Request req, Response res) {

      Integer id2 = id.get()+1;
      Crossword puzzle = crosswordCache.get(id2);

      if (puzzle == null){
        puzzle = createCrossword();
      } else {
        while (puzzle.getPlayers() == 2) {
          id2 = id2+1;
          puzzle = crosswordCache.get(id2);
          if (puzzle == null){
            puzzle = createCrossword();
          }
        }
      }
      puzzle.addPlayer();


      System.out.println("1 player");
      System.out.println(id2);

      Box[][] crossword = puzzle.getArray();
      System.out.println(puzzle);

      crosswordCache.put(id2, puzzle);

      ImmutableMap<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("crossword", crossword).put("id", id2.toString())
          .put("roomNumber", id2.toString()).build();

      return new ModelAndView(variables, "crossword_single.ftl");
    }

  }


  /** Handler for serving chat page. */
  private static class ChatHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      ImmutableMap<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("roomNumber", id.get()).build();
      return new ModelAndView(variables, "chat.ftl");
    }

  }

}