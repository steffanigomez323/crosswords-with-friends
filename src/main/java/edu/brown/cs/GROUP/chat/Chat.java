package edu.brown.cs.GROUP.chat;

import static j2html.TagCreator.article;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static spark.Spark.init;
import static spark.Spark.webSocket;

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

import edu.brown.cs.GROUP.crosswordswithFriends.Word;

public class Chat {

  static Map<Session, String> userUsernameMap = new HashMap<Session, String>();
  static Set<String> stopWords = new HashSet<String>();
  static Map<Integer, List<Session>> roomUsers = new HashMap<Integer, List<Session>>();
  static Set<String> wordsToCensor = new HashSet<String>();

  public static void main(String[] args) throws IOException {
  }

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
    webSocket("/chat", ChatWebSocketHandler.class);
    init();
  }

  public static void setCensorWords(List<Word> toPass) {
    for (Word word : toPass) {
      wordsToCensor.add(word.getWord());
      String cleanedClue = word.getClue().replace("[^a-zA-Z ]", "");
      String[] clueWords = cleanedClue.split(" ");
      System.out.println("clues " + word.getClue());
      for (String clueWord : clueWords) {
        if (!stopWords.contains(clueWord)) {
          wordsToCensor.add(clueWord);
        }
      }
    }
  }

  public static String censorMessage(Set<String> censorList,
      String message) {
    String cleanedMessage = message.replace("[^a-zA-Z ]", "")
        .toLowerCase();
    String[] messageArray = cleanedMessage.split(" ");
    for (int i = 0; i < messageArray.length; i++) {
      if (censorList.contains(messageArray[i])) {
        Integer numAstericks = messageArray[i].length();
        StringBuilder stars = new StringBuilder("");
        for (int g = 0; g < numAstericks; g++) {
          stars.append("-");
        }
        messageArray[i] = stars.toString();
      } else {
        for (String word : censorList) {
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
  public static void broadcastMessage(String sender, String message,
      Integer roomId) {
    try {
      System.out.println("room id " + roomId);
      if (roomUsers.get(roomId) != null) {
        System.out.println("room id " + roomId + roomUsers.get(roomId));
        for (Session session : roomUsers.get(roomId)) {
          System.out.println("in broadcast message");
          System.out.println("sender " + sender);
          if (session.isOpen()) {
            System.out.println("sesion is open");
            session.getRemote()
                .sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender,
                        censorMessage(wordsToCensor, message)))));
          }
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
