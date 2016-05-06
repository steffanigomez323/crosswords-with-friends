package edu.brown.cs.GROUP.chat;

import edu.brown.cs.GROUP.crosswordswithFriends.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class TwoChatWebSocketHandler {

  private static HashMap<Session, Integer> userRoom = new HashMap<Session, Integer>();

  @OnWebSocketConnect
  public void onConnect(Session user) throws Exception {

    int nextRoomNumber = GUI.twoPlayerId.get();

    //roomUsers maps room#s to list of sessions
    List<Session> usersInRoom = new ArrayList<Session>();
    if (Chat.roomUsers.get(nextRoomNumber) != null) {
      usersInRoom = Chat.roomUsers.get(nextRoomNumber);
      GUI.twoPlayerId.getAndIncrement();
    }

    usersInRoom.add(user);
    Chat.roomUsers.put(nextRoomNumber, usersInRoom);

    String username = "down" + nextRoomNumber;
    //userUsernameMap maps sessions to usernames
    if (Chat.userUsernameMap.containsValue(username)) {
      username = "across" + nextRoomNumber;
    }
    Chat.userUsernameMap.put(user, username);

    //userRoom maps sessions to room#s
    userRoom.put(user, nextRoomNumber);

    System.out.println(username);
    Chat.broadcastStart("Server", (username + " joined the chat"), nextRoomNumber);
  }

  public static Integer getRoomNumber(Session user) {
    return userRoom.get(user);
  }

  @OnWebSocketClose
  public void onClose(Session user, int statusCode, String reason) {
      String username = Chat.userUsernameMap.get(user);
      Chat.userUsernameMap.remove(user);
      Chat.broadcastMessage("Server", (username + " left the chat"), userRoom.get(user));
  }


  @OnWebSocketMessage
  public void onMessage(Session user, String message) {
    if (message.startsWith("DATA")){
      Chat.broadcastCorrect(message, userRoom.get(user));
    } else if (message.startsWith("**ALL**")){
      Chat.broadcastAll(userRoom.get(user));
    } else if (message.startsWith("LETTER")){
      Chat.broadcastLetter(message, userRoom.get(user));
    } else if (message.startsWith("ANAGRAM")){
      System.out.println("in anagram web socket " + message);
      Chat.broadcastAnagram(message, userRoom.get(user));
    } else {
      Chat.broadcastMessage(Chat.userUsernameMap.get(user), message, userRoom.get(user));
    }
  }

}
