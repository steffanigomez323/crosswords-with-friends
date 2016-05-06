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
public class OneChatWebSocketHandler {

  private static HashMap<Session, Integer> userRoom = new HashMap<Session, Integer>();

  @OnWebSocketConnect
  public void onConnect(Session user) throws Exception {
    int nextRoomNumber = GUI.onePlayerId.get()-1;

    List<Session> usersInRoom = new ArrayList<Session>();
    usersInRoom.add(user);
    Chat.roomUsers.put(nextRoomNumber, usersInRoom);

    userRoom.put(user, nextRoomNumber);
  }

  public static Integer getRoomNumber(Session user) {
    return userRoom.get(user);
  }

  @OnWebSocketMessage
  public void onMessage(Session user, String message) {
    System.out.println(message);
    if (message.startsWith("DATA")) {
      Chat.broadcastCorrect(message, userRoom.get(user));
    } else if (message.startsWith("LETTER")) {
      //System.out.println("user room get " + userRoom.get(user) + "user" + user);
      Chat.broadcastLetter(message, userRoom.get(user));
    } else if (message.startsWith("ANAGRAM")){
      System.out.println("in anagram web socket " + message);
      Chat.broadcastAnagram(message, userRoom.get(user));
    } else if (message.startsWith("**ALL**")){
      Chat.broadcastAll(userRoom.get(user));
    }
  }

}
