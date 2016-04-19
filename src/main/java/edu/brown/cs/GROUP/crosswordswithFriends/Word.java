package edu.brown.cs.GROUP.crosswordswithFriends;

public class Word {

  private String word;
  private int xIndex;
  private int yIndex;
  private Orientation orientation;
  private String clue;

  public Word(String w, int x, int y, Orientation o, String c){
    word = w;
    xIndex = x;
    yIndex = y;
    orientation = o;
    clue = c;
  }

  public String getWord(){
    return word;
  }

  public int getXIndex(){
    return xIndex;
  }

  public int getYIndex(){
    return yIndex;
  }

  public Orientation getOrientation(){
    return orientation;
  }

  public String getClue(){
    return clue;
  }

}
