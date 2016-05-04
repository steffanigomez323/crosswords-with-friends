package edu.brown.cs.GROUP.chat;

import edu.brown.cs.GROUP.crosswordswithFriends.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class TwoChatWebSocketHandler {

  private static HashMap<Session, Integer> userRoom = new HashMap<Session, Integer>();

  @OnWebSocketConnect
  public void onConnect(Session user) throws Exception {

    int nextRoomNumber = GUI.twoPlayerId.get();
    String username = "down" + nextRoomNumber;

    //roomUsers maps room#s to list of sessions
    List<Session> usersInRoom = new ArrayList<Session>();
    if (Chat.roomUsers.get(nextRoomNumber) != null) {
      usersInRoom = Chat.roomUsers.get(nextRoomNumber);
    }
    usersInRoom.add(user);
    Chat.roomUsers.put(nextRoomNumber, usersInRoom);

    //userUsernameMap maps sessions to usernames
    if (Chat.userUsernameMap.containsValue(username)) {
      username = "across" + nextRoomNumber;
    }
    Chat.userUsernameMap.put(user, username);

    //userRoom maps sessions to room#s
    userRoom.put(user, nextRoomNumber);

    Chat.broadcastStart("Server", (username + " joined the chat"), nextRoomNumber);
  }

  public static Integer getRoomNumber(Session user) {
    return userRoom.get(user);
  }

  @OnWebSocketMessage
  public void onMessage(Session user, String message) {
    if (message.startsWith("DATA")){
      Chat.broadcastCorrect(message, userRoom.get(user));
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
