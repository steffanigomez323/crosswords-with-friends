package edu.brown.cs.GROUP.crosswordswithFriends;

public class Clue {

  private String clue;
  private Orientation orientation;

  public Clue(String c, Orientation o){
    clue = c;
    orientation = o;
  }

  public String getClue(){
    return clue;
  }

  public Orientation getOrientation(){
    return orientation;
  }

}
