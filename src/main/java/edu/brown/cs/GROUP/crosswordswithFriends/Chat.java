package edu.brown.cs.GROUP.crosswordswithFriends;
import org.eclipse.jetty.websocket.api.*;
import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.*;
import java.util.*;
import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {

  static Map<Session, String> userUsernameMap = new HashMap<Session, String>();
  static List<String> stopWords = new ArrayList<String>();
  static List<String> wordsToCensor = new ArrayList<String>();
  static int nextUserNumber = 1; //Assign to username for next connecting user

  public static void main(String[] args) throws IOException {
    wordsToCensor.add("horse");
    wordsToCensor.add("hair");
    System.out.println("result of censor message " + censorMessage("I want a horse "));
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(new File(args[0])));
      String line;
      while ((line = reader.readLine()) != null) {
        stopWords.add(line);
      }
      reader.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    staticFileLocation("public"); //index.html is served at localhost:4567 (default port)
    webSocket("/chat", ChatWebSocketHandler.class);
    init();
  }
  
  public static String censorMessage(String message) {
    String[] messageArray = message.split(" ");
    for (int i = 0; i < messageArray.length; i++) {
      if (wordsToCensor.contains(messageArray[i])) {
        messageArray[i] = "*";
      }
    }
    String censored = "";
    for (String word : messageArray){
      censored += word + " ";
    }
    return censored;
  }

  //Sends a message from one user to all users, along with a list of current usernames
  public static void broadcastMessage(String sender, String message) {
    userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
      try {
        session.getRemote().sendString(String.valueOf(new JSONObject()
            .put("userMessage", createHtmlMessageFromSender(sender, censorMessage(message)))
            .put("userlist", userUsernameMap.values())
            ));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  //Builds a HTML element with a sender-name, a message, and a timestamp,
  private static String createHtmlMessageFromSender(String sender, String message) {
    return article().with(
        b(sender + " says:"),
        p(message),
        span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
  }

}
