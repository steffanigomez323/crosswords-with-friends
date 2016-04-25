package edu.brown.cs.GROUP.crosswordswithFriends;

public class Clue {

  private String clue;
  private Orientation orientation;
  private int size;

  public Clue(String c, Orientation o, int s){
    clue = c;
    orientation = o;
    size = s;
  }

  public String getClue(){
    return clue;
  }

  public Orientation getOrientation(){
    return orientation;
  }

  public int getSize(){
    return size;
  }

}
