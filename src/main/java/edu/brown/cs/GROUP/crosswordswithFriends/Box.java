package edu.brown.cs.GROUP.crosswordswithFriends;

public class Box{
  private boolean isBox;
  private String letter;

  Box(){
    isBox = true;
  }

  Box(String l){
    isBox = false;
    letter = l.toUpperCase();
  }

  public boolean getIsBox(){
    return isBox;
  }

  public String getLetter(){
    return letter;
  }
}