package edu.brown.cs.GROUP.crosswordswithFriends;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.GROUP.chat.Chat;
import edu.brown.cs.GROUP.database.Database;
import freemarker.template.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/** GUI class for web server handling. */
public class GUI {

  /** For converting to JSON. */
  private static final Gson GSON = new Gson();
  private static final int ROWS = 9;
  private static final int COLS = 9;

  private static HashMap<Integer, Crossword> crosswordCache;

  private Database db;
  private Crossword puzzle;
  private static AtomicInteger id;
  /**
   * Constructor starts server on instantiation.
   *
   * @param port Port number specified by command line or 4567 by default
   * @param d Database connection path
   * @throws IOException
   */
  public GUI(int port, Database d) {
    Spark.port(port);
    db = d;
    // List<String> words = db.getAllUnderSeven();
    // Crossword puzzle = new Crossword(words);
    // puzzle.fillPuzzle();
    id = new AtomicInteger(1000);
    runSparkServer();
    crosswordCache = new HashMap<Integer, Crossword>();
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

  /** Runs the server. Organizes get and put requests.
   * @throws IOException */
  private void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    try {
      Chat.initChatroom();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    FreeMarkerEngine freeMarker = createEngine();
    Spark.get("/home", new FrontHandler(db), freeMarker);
    Spark.get("/check", new CheckHandler());
    Spark.get("/chat", new ChatHandler(), freeMarker);
  }

  /** Handler for serving main page. */
  private static class FrontHandler implements TemplateViewRoute {

    private Database db;

    public FrontHandler(Database db) {
      this.db = db;
    }
    @Override
    public ModelAndView handle(Request req, Response res) {

      Integer id2 = id.get();

      Crossword puzzle = crosswordCache.get(id2);
      if (puzzle == null || puzzle.getPlayers()==2) {
        if (puzzle == null){
          id2 = id.get();
        } else {
          id2 = id.incrementAndGet();
        }
        List<String> words = db.getAllUnderSeven();
        puzzle = new Crossword(words, db);
        puzzle.fillPuzzle();
      } else {
        puzzle.addPlayer();
      }

      List<Word> toPass = puzzle.getFinalList();
      Chat.setCensorWords(toPass);

      Box[][] crossword = puzzle.getArray();
      System.out.println(puzzle);
      
      crosswordCache.put(id2, puzzle);

      ImmutableMap<String, Object> variables =
          new ImmutableMap.Builder<String, Object>()
          .put("crossword", crossword)
          .put("id", id2.toString())
          .put("roomNumber", Chat.getRoomNumber())
          .build();

      return new ModelAndView(variables, "crossword.ftl");
    }

  }

  private class CheckHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {

      QueryParamsMap qm = req.queryMap();

      String word = qm.value("word");
      int x = Integer.valueOf(qm.value("x"));
      int y = Integer.valueOf(qm.value("y"));
      Orientation orientation = Orientation.valueOf(qm.value("orientation"));
      Integer id = Integer.valueOf(qm.value("id"));

      System.out.println("Cool!");

      if (!crosswordCache.containsKey(id)) {
        return "false";
      }
      System.out.println("checking : " + word);
      Crossword puzzle = crosswordCache.get(id);
      Box[][] crossword = puzzle.getArray();
      for (int i = 0; i < word.length(); i++){
        Box box = crossword[y][x];
        box.printLetter();
        if (!box.checkVal(word.charAt(i))) {
          System.out.println("CHECK : "+ word.charAt(i));
          return "false";
        }
        if (orientation == Orientation.ACROSS) {
          x++;
        } else {
          y++;
        }
      }
      return "true";
    }
  }

  /** Handler for serving chat page. */
  private static class ChatHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      System.out.println("in chat handler " );
      ImmutableMap<String, Object> variables =
          new ImmutableMap.Builder<String, Object>().put("roomNumber", Chat.getRoomNumber()).build();
      return new ModelAndView(variables, "chat.ftl");
    }

  }

}