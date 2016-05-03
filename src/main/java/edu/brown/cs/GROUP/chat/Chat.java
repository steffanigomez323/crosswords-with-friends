package edu.brown.cs.GROUP.chat;
import static j2html.TagCreator.article;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static spark.Spark.init;
import static spark.Spark.webSocket;
import edu.brown.cs.GROUP.crosswordswithFriends.GUI;
import edu.brown.cs.GROUP.crosswordswithFriends.Orientation;
import edu.brown.cs.GROUP.crosswordswithFriends.Word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

public class Chat {

  static Map<Session, String> userUsernameMap = new HashMap<Session, String>();
  static Set<String> stopWords = new HashSet<String>();
  static Map<Integer, List<Session>> roomUsers = new HashMap<Integer, List<Session>>();
  static HashMap<Integer, Set<String>> wordsToCensor = new HashMap<Integer, Set<String>>();
  
  public static void main(String[] args) throws IOException {
  }

  public static void initChatroom() throws IOException {
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(new File("cs032_stopwords.txt")));
      String line;
      while ((line = reader.readLine()) != null) {
        stopWords.add(line);
      }
      reader.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //staticFileLocation("static"); //index.html is served at localhost:4567 (default port)
    webSocket("/chat", ChatWebSocketHandler.class);
    init();
  }

  public static void setCensorWords(Integer roomId, List<Word> toPass) {
    Set<String> censorWords = new HashSet<String>();
    for (Word word : toPass) {
      censorWords.add(word.getWord());
      String cleanedClue = word.getClue().replace("[^a-zA-Z ]", "");
      String[] clueWords = cleanedClue.split(" ");
      System.out.println("clues " + word.getClue());
      for (String clueWord : clueWords) {
        if (! stopWords.contains(clueWord)) {
          censorWords.add(word.getWord());
        }
      }
    }
    wordsToCensor.put(roomId,  censorWords);
  }

  public static String censorMessage(Set<String> censorList, String message) {
    String cleanedMessage = message.replace("[^a-zA-Z ]", "").toLowerCase();
    String[] messageArray = cleanedMessage.split(" ");
    for (int i = 0; i < messageArray.length; i++) {
      if (censorList.contains(messageArray[i])) {
        Integer numAstericks = messageArray[i].length();
        String stars = "";
        for (int g = 0; g < numAstericks; g++) {
          stars += "-";
        }
        messageArray[i] = stars;
      } else {
        for (String word : censorList) {
          Integer numToCensor = word.length();
          String wordInArray = messageArray[i];
          String astericks = new String(new char[numToCensor]).replace("\0", "-");
          String censored = wordInArray.replace(word, astericks);
          messageArray[i] = censored;
        }
      }
    }
    String censored = "";
    for (String word : messageArray) {
      censored += word + " ";
    }
    return censored;
  }

  //Sends a message from one user to all users, along with a list of current usernames
  public static void broadcastMessage(String sender, String message, Integer roomId) {
    try {
      System.out.println("room id " + roomId);
      if (roomUsers.get(roomId) != null ) {
        System.out.println("room id " + roomId + roomUsers.get(roomId));
      for (Session session : roomUsers.get(roomId)) {
        System.out.println("in broadcast message");
        System.out.println("sender " + sender);
        if (session.isOpen()) {
          System.out.println("sesion is open");
          session.getRemote().sendString(String.valueOf(new JSONObject()
              .put("userMessage", createHtmlMessageFromSender(sender, censorMessage(wordsToCensor.get(roomId), message)))
              ));
        }
      }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void broadcastCorrect(String sender, String message, Integer roomId) {
    String[] variables = message.split(";");
    int x = Integer.valueOf(variables[2]);
    int y = Integer.valueOf(variables[3]);
    Orientation o = Orientation
        .valueOf(variables[4]);
    Integer id = Integer.valueOf(variables[5]);
    System.out.println("x : "+x+" y: "+y);
    boolean valid = GUI.checkValid(variables[1], x, y, o, id);
    System.out.println(valid);
    if (valid){
      try {
        if (roomUsers.get(roomId) != null ) {
        for (Session session : roomUsers.get(roomId)) {
          if (session.isOpen()) {
            System.out.println("broadcastin");
            session.getRemote().sendString(message);
          }
        }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public static void broadcastLetter(String sender, String message, Integer roomId) {
    String[] variables = message.split(";");
    int x = Integer.valueOf(variables[2]);
    int y = Integer.valueOf(variables[3]);
    Orientation o = Orientation
        .valueOf(variables[4]);
    Integer id = Integer.valueOf(variables[5]);
    boolean valid = GUI.checkValid(variables[1], x, y, o, id);
    System.out.println(valid);
    if (valid){
      try {
        if (roomUsers.get(roomId) != null ) {
        for (Session session : roomUsers.get(roomId)) {
          if (session.isOpen()) {
            System.out.println("broadcastin");
            session.getRemote().sendString(message);
          }
        }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
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
