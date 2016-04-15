package edu.brown.cs.GROUP.crosswordswithFriends;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import freemarker.template.Configuration;

import java.io.File;
import java.io.IOException;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/** GUI class for web server handling. */
public class GUI {

  /** For converting to JSON. */
  private static final Gson GSON = new Gson();

  /** Constructor starts server on instantiation.
   *
   * @param port Port number specified by command line or 4567 by default
   * @param d Database connection path */
  public GUI(int port) {
    Spark.setPort(port);
    runSparkServer();

  }

  /** Creates engine for server.
   *
   * @return FreeMarker engine. */
  private static FreeMarkerEngine createEngine() {

    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");

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

    FreeMarkerEngine freeMarker = createEngine();

    Spark.get("/home", new FrontHandler(), freeMarker);
  }

  /** Handler for serving main page. */
  private static class FrontHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {

      Box[][] crossword = {
        {new Box(), new Box("u"), new Box("s"), new Box("a"), new Box()},
        {new Box("b"), new Box("b"), new Box("a"), new Box("l"), new Box("l")},
        {new Box("b"), new Box("e"), new Box("l"), new Box("l"), new Box("e")},
        {new Box("c"), new Box("r"), new Box("u"), new Box("e"), new Box("t")},
        {new Box(), new Box("s"), new Box("t"), new Box("y"), new Box()}};

      ImmutableMap<String, Object> variables =
          new ImmutableMap.Builder<String, Object>()
              .put("crossword", crossword).build();

      return new ModelAndView(variables, "crossword.ftl");
    }

  }
}