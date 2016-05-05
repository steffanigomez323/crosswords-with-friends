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

import edu.brown.cs.GROUP.crosswordswithFriends.GUI;
import edu.brown.cs.GROUP.crosswordswithFriends.Orientation;
import edu.brown.cs.GROUP.crosswordswithFriends.Word;

/**
 * This class implements the creation of a chat between users.
 *
 */

public class Chat {

  /**
   * This is a hashmap mapping users to usernames.
   */

  static Map<Session, String> userUsernameMap = new HashMap<Session, String>();

  /**
   * This is a hashset of all the stop words, the words to definitely not
   * censor.
   */

  static Set<String> stopWords = new HashSet<String>();

  /**
   * This is a hashmap of rooms to users, to handle multiple people messaging
   * with different exclusive people.
   */

  static Map<Integer, List<Session>> roomUsers = new HashMap<Integer, List<Session>>();

  /**
   * This is a hashmap of rooms to words to censor.
   */

  static HashMap<Integer, Set<String>> wordsToCensor = new HashMap<Integer, Set<String>>();

  // public static void main(String[] args) throws IOException {
  // }

  /**
   * This constructor instantiates the chatroom and reads in the stop words from
   * a stop words file and sets up two users to talk to each other.
   * @throws IOException in case the stop words file is unable to be opened.
   */

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

  /**
   * This method sets the words that need to be censored from the list of words
   * in the clues in the given room id.
   * @param roomId the room id
   * @param toPass the words in the clues
   */

  public static void setCensorWords(Integer roomId, List<Word> toPass) {
    Set<String> censorWords = new HashSet<String>();
    for (Word word : toPass) {
      censorWords.add(word.getWord());
      String cleanedClue = word.getClue().replaceAll("[^a-zA-Z ]", "")
          .toLowerCase();
      String[] clueWords = cleanedClue.split(" ");
      for (String clueWord : clueWords) {
        if (!stopWords.contains(clueWord)) {
          System.out.println("clueword " + clueWord);
          censorWords.add(clueWord);
        }
      }
    }
    wordsToCensor.put(roomId, censorWords);
  }

  /**
   * This method actually censors the words, checking whether a word is in the
   * list of words to be censored, and if it is, the word is replaced by dashes.
   * @param roomId the room id
   * @param message the message that was sent
   * @return the string that the other user will see, the censored string
   */

  public static String censorMessage(Integer roomId, String message) {
    Set<String> censorWords = wordsToCensor.get(roomId);
    String cleanedMessage = message.replaceAll("[^a-zA-Z ]", "")
        .toLowerCase();
    System.out.println("message: " + cleanedMessage);
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

  /**
   * This method sends a message from one user to all users, alone with a list
   * of current usernames.
   * @param sender the user who sent the message
   * @param message the message
   * @param roomId the room id
   */

  public static void broadcastStart(String sender, String message,
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

  /**
   * This method sends a message from one user to all users, along with a list
   * of current usernames.
   * @param sender the user who sent the message
   * @param message the message
   * @param roomId the room id
   */

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

  /**
   * This method broadcasts the correct (?).
   * @param message the message
   * @param roomId the room id
   */

  public static void broadcastCorrect(String message, Integer roomId) {
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
    System.out.println("BROADCAST LETTER ");
    int x = Integer.valueOf(variables[1]);
    int y = Integer.valueOf(variables[2]);
    // Integer id = Integer.valueOf(variables[3]);
    Character letter = GUI.getLetter(x, y, roomId);
    System.out.println(letter);
    try {
      if (roomUsers.get(roomId) != null) {
        for (Session session : roomUsers.get(roomId)) {
          if (session.isOpen()) {
            String toSend = "LETTER;" + x + ";" + y + ";"
                + letter.toString();
            System.out.println(toSend);
            session.getRemote().sendString(toSend);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This method broadcasts an anagram of the answer to the chatroom (?).
   * @param message the message
   * @param roomId the room id
   */

  public static void broadcastAnagram(String message, Integer roomId) {
    String[] variables = message.split(";");
    System.out.println("ANAGRAM ");
    int x = Integer.valueOf(variables[1]);
    int y = Integer.valueOf(variables[2]);
    // Integer id = Integer.valueOf(variables[3]);
    Character letter = GUI.getLetter(x, y, roomId);
    System.out.println(letter);
    try {
      if (roomUsers.get(roomId) != null) {
        for (Session session : roomUsers.get(roomId)) {
          if (session.isOpen()) {
            String toSend = "LETTER;" + x + ";" + y + ";"
                + letter.toString();
            System.out.println(toSend);
            session.getRemote().sendString(toSend);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This method builds a HTML element with a sender-name, a message, and a
   * timestamp.
   * @param sender the user who sent the message
   * @param message the message
   * @return the HTML element
   */

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
