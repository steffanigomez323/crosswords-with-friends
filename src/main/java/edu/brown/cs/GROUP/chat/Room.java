package edu.brown.cs.GROUP.chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Room {
  private Integer nextRoom = 1;
  private Set<String> users = new HashSet<String>();
  public Room() {
    
  }
  
  public Integer getId() {
    return 0;
  }
  
  public Set<String> getUsers() {
    return null;
  }
  
  public void addUser(String username) {
    users.add(username);
  }
}