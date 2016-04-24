package edu.brown.cs.GROUP.chat;
import org.eclipse.jetty.websocket.api.*;
import org.json.*;

import edu.brown.cs.GROUP.crosswordswithFriends.Word;

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
  static Set<String> stopWords = new HashSet<String>();
  static Set<Room> rooms = new HashSet<Room>();
  static Set<String> wordsToCensor = new HashSet<String>();
  static int nextRoomNumber = 1; //Assign to username for next connecting user

  public static void main(String[] args) throws IOException {
  }
  
  public static Integer getRoomNumber() {
    return nextRoomNumber;
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
    webSocket("/chat/1", ChatWebSocketHandler.class);
    init();
  }

  public static void setCensorWords(List<Word> toPass) {
    for (Word word : toPass) {
      wordsToCensor.add(word.getWord());
      String cleanedClue = word.getClue().replace("[^a-zA-Z ]", "");
      String[] clueWords = cleanedClue.split(" ");
      System.out.println("clues " + word.getClue());
      for (String clueWord : clueWords) {
        if (! stopWords.contains(clueWord)) {
          wordsToCensor.add(clueWord);
        }
      }
    }
  }

  public static String censorMessage(Set<String> censorList, String message) {
    String cleanedMessage = message.replace("[^a-zA-Z ]", "");
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
  public static void broadcastMessage(String sender, String message) {
    userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
      try {
        session.getRemote().sendString(String.valueOf(new JSONObject()
            .put("userMessage", createHtmlMessageFromSender(sender, censorMessage(wordsToCensor, message)))
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
