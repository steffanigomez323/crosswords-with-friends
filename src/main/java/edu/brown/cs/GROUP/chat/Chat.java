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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

//  public static void main(String[] args) throws IOException {
//  }

  public static void initChatroom() throws IOException {
    // BufferedReader reader;
    try {
      try (FileInputStream fis = new FileInputStream(
          "cs032_stopwords.txt")) {
        try (InputStreamReader isr = new InputStreamReader(fis, "UTF8")) {
          try (BufferedReader reader = new BufferedReader(isr)) {
            // reader = new BufferedReader(new FileReader(new
            // File("cs032_stopwords.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
              stopWords.add(line);
            }
            reader.close();
          }
        }
      }
    } catch (FileNotFoundException e) {
      // e.printStackTrace();
      System.err
          .println("ERROR: The stop words corpus could not be found.");
      return;
    }
    // staticFileLocation("static"); //index.html is served at localhost:4567
    // (default port)
    webSocket("/chat/one", OneChatWebSocketHandler.class);
    webSocket("/chat/two", TwoChatWebSocketHandler.class);
    init();
  }

  public static void setCensorWords(Integer roomId, List<Word> toPass) {
    Set<String> censorWords = new HashSet<String>();
    for (Word word : toPass) {
      censorWords.add(word.getWord());
      String cleanedClue = word.getClue().replaceAll("[^a-zA-Z ]", "")
          .toLowerCase();
      String[] clueWords = cleanedClue.split(" ");
      for (String clueWord : clueWords) {
        if (!stopWords.contains(clueWord)) {
          censorWords.add(clueWord);
        }
      }
    }
    wordsToCensor.put(roomId, censorWords);
  }

  public static String censorMessage(Integer roomId, String message) {
    Set<String> censorWords = wordsToCensor.get(roomId);
    String cleanedMessage = message.replaceAll("[^a-zA-Z ]", "")
        .toLowerCase();
    String[] messageArray = cleanedMessage.split(" ");
    for (int i = 0; i < messageArray.length; i++) {
      if (censorWords.contains(messageArray[i])) {
        Integer numAstericks = messageArray[i].length();
        StringBuilder stars = new StringBuilder("");
        for (int g = 0; g < numAstericks; g++) {
          stars.append("-");
        }
        messageArray[i] = stars.toString();
      } else {
        for (String word : censorWords) {
          Integer numToCensor = word.length();
          String wordInArray = messageArray[i];
          String astericks = new String(new char[numToCensor])
              .replace("\0", "-");
          String censored = wordInArray.replace(word, astericks);
          messageArray[i] = censored;
        }
      }
    }
    StringBuilder censored = new StringBuilder("");
    for (String word : messageArray) {
      censored.append(word).append(" ");
    }
    return censored.toString();
  }

  // Sends a message from one user to all users, along with a list of current
  // usernames
  public static void broadcastStart(String sender, String message,
      Integer roomId) {
    try {

      if (roomUsers.get(roomId) != null) {
        for (Session session : roomUsers.get(roomId)) {
          if (session.isOpen()) {
            session.getRemote().sendString(
                String.valueOf(new JSONObject().put("userMessage",
                    createHtmlMessageFromSender(sender, censorMessage(roomId, message)))));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Sends a message from one user to all users, along with a list of current
  // usernames
  public static void broadcastMessage(String sender, String message,
      Integer roomId) {
    try {
      if (roomUsers.get(roomId) != null) {
        for (Session session : roomUsers.get(roomId)) {
          if (session.isOpen()) {
            session.getRemote()
                .sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender,
                        censorMessage(roomId, message)))));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void broadcastCorrect(String message,
      Integer roomId) {
    String[] variables = message.split(";");
    int x = Integer.valueOf(variables[2]);
    int y = Integer.valueOf(variables[3]);
    Orientation o = Orientation.valueOf(variables[4]);
    Integer id = Integer.valueOf(variables[5]);
    boolean valid = GUI.checkValid(variables[1], x, y, o, id);
    if (valid) {
      try {
        if (roomUsers.get(roomId) != null) {
          for (Session session : roomUsers.get(roomId)) {
            if (session.isOpen()) {
              session.getRemote().sendString(message);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void broadcastLetter(String message, Integer roomId) {
    String[] variables = message.split(";");
    int x = Integer.valueOf(variables[1]);
    int y = Integer.valueOf(variables[2]);
    Integer id = Integer.valueOf(variables[3]);
    Character letter = GUI.getLetter(x, y, roomId);
    try {
      if (roomUsers.get(roomId) != null ) {
        for (Session session : roomUsers.get(roomId)) {
          if (session.isOpen()) {
            String toSend = "LETTER;" + x + ";" + y + ";" + letter.toString();
            System.out.println(toSend);
            session.getRemote().sendString(toSend);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void broadcastAnagram(String message, Integer roomId) {
    String[] variables = message.split(";");
    System.out.println("ANAGRAM " );
    int length = Integer.valueOf(variables[1]);
    int x = Integer.valueOf(variables[2]);
    int y = Integer.valueOf(variables[3]);
    Orientation o = Orientation.valueOf(variables[4]);
    int wordId = Integer.valueOf(variables[5]);
    Integer id = Integer.valueOf(variables[6]);
    String scrambled = GUI.getAnagram(length, x, y, o, id);
    System.out.println("scrambled "+ scrambled);
    try {
      if (roomUsers.get(roomId) != null) {
        for (Session session : roomUsers.get(roomId)) {
          String toSend = "ANAGRAM;" + x + ";" + y + ";" + o + ";" + wordId +";" + scrambled;
          System.out.println(toSend);
          session.getRemote().sendString(toSend);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Builds a HTML element with a sender-name, a message, and a timestamp,
  private static String createHtmlMessageFromSender(String sender,
      String message) {
    return article()
        .with(b(sender + " says:"), p(message),
            span().withClass("timestamp").withText(
                new SimpleDateFormat("HH:mm:ss").format(new Date())))
        .render();
  }

}
