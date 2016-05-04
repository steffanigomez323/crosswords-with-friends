package edu.brown.cs.GROUP.chat;
import edu.brown.cs.GROUP.crosswordswithFriends.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;
    private HashSet users;
    private static HashMap<Session, Integer> userRoom = new HashMap<Session, Integer>();
    private static int nextRoomNumber = GUI.id.get();
    //static int nextRoomNumber = 1; //Assign to username for next connecting user
    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "down" + nextRoomNumber;
        List<Session> usersInRoom = new ArrayList<Session>();
        if (Chat.roomUsers.get(nextRoomNumber) != null) {
          usersInRoom = Chat.roomUsers.get(nextRoomNumber);
          //Chat.roomUsers.remove(nextRoomNumber);
        }
        usersInRoom.add(user);
        if (Chat.userUsernameMap.containsValue(username)){
          username = "across" + nextRoomNumber;
        }
        Chat.roomUsers.put(nextRoomNumber,  usersInRoom);
        userRoom.put(user, nextRoomNumber);
        Chat.userUsernameMap.put(user, username);
        Chat.broadcastMessage(sender = "Server", msg = (username + " joined the chat"), nextRoomNumber);
        if (username.contains("across")) {
          nextRoomNumber++;
        }
    }

    public static Integer getRoomNumber(Session user) {
    	return userRoom.get(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        //users.remove(user);
        //rooms.remove(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"), userRoom.get(user));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
      if (message.startsWith("DATA")){
        Chat.broadcastCorrect(sender = Chat.userUsernameMap.get(user), message, userRoom.get(user));
      } else if (message.startsWith("LETTER")){
        Chat.broadcastLetter(sender = Chat.userUsernameMap.get(user), message, userRoom.get(user));
      } else if (message.startsWith("ANAGRAM")){
        Chat.broadcastLetter(sender = Chat.userUsernameMap.get(user), message, userRoom.get(user));
      } else {
        System.out.println("sender " + Chat.userUsernameMap.get(user)  + "message " + message);
        Chat.broadcastMessage(sender = Chat.userUsernameMap.get(user), msg = message, userRoom.get(user));
      }
    }

}
