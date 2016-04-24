package edu.brown.cs.GROUP.crosswordswithFriends;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.GROUP.database.Database;
import edu.brown.cs.GROUP.chat.Chat;
import freemarker.template.Configuration;
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
  public GUI(int port, Database d) throws IOException {
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
  private void runSparkServer() throws IOException {
    Spark.externalStaticFileLocation("src/main/resources/static");
    Chat.initChatroom();
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
      System.out.println("Atomic id : "+id2);
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


      /*
       * words.add(new Word("Bruh", 0, 0, Orientation.ACROSS,
       * "\"Dude, cmon ...,\" in modern lingo")); words.add(new Word("Ripen", 0,
       * 1, Orientation.ACROSS, "Turn yellow, as a banana")); words.add(new
       * Word("Apple", 0, 2, Orientation.ACROSS,
       * "Company that tangled with the F.B.I. over encryption")); words.add(new
       * Word("Duels", 0, 3, Orientation.ACROSS, "Burr vs. Hamilton and others"
       * )); words.add(new Word("Pros", 1, 4, Orientation.ACROSS, "___ and cons"
       * )); words.add(new Word("Brad", 0, 0, Orientation.DOWN,
       * "Pitt of \"The Big Short\"")); words.add(new Word("Ripup", 1, 0,
       * Orientation.DOWN, "Tear to pieces")); words.add(new Word("Upper", 2, 0,
       * Orientation.DOWN, "Opposite of lower")); words.add(new Word("Hello", 3,
       * 0, Orientation.DOWN, "One meaning of \"aloha\"")); words.add(new
       * Word("Ness", 4, 1, Orientation.DOWN, "Loch ___ monster"));
       */
      // System.out.println(puzzle.getFinalList());
      List<Word> toPass = puzzle.getFinalList();
      Chat.setCensorWords(toPass);
      System.out.println(puzzle.toString());
      System.out.println("id : "+id2.toString());
      Box[][] crossword = puzzle.getArray();

//      Box[][] crossword = new Box[COLS][ROWS];

//      for (int i=0; i<crossword.length; i++){
//        for (int j=0; j<crossword[0].length; j++){
//          crossword[i][j] = new Box();
//        }
//      }
//
//      for (Word w : toPass) {
//        String word = w.getWord();
//        int x = w.getXIndex();
//        int y = w.getYIndex();
//        Orientation o = w.getOrientation();
//
//        Box b = crossword[y][x];
//        if (b.getIsBox()){
//          crossword[y][x] = new Box(word.charAt(0), w.getClue(), o);
//        } else {
//          b.addClue(w.getClue(), o);
//        }
//        for (int i=1; i<word.length(); i++){
//          if (o == Orientation.ACROSS){
//            x++;
//          } else {
//            y++;
//          }
//          char c = word.charAt(i);
//          b = crossword[y][x];
//          if (b.getIsBox()){
//            crossword[y][x] = new Box(c);
//          }
//        }
//      }


      crosswordCache.put(id2, puzzle);

      //System.out.println("room number " + Chat.getRoomNumber());
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