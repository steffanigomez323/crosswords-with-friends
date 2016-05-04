package edu.brown.cs.GROUP.crosswordswithFriends;

import java.util.Random;
public class Word {

  private String word;
  private int xIndex;
  private int yIndex;
  private Orientation orientation;
  private String clue;
  private int score;

  public Word(String w, int x, int y, Orientation o, int s) {
    word = w;
    xIndex = x;
    yIndex = y;
    orientation = o;
    score = s;
  }

  public Word(String w, int x, int y, Orientation o, int s, String c) {
    word = w;
    xIndex = x;
    yIndex = y;
    orientation = o;
    score = s;
    clue = c;
  }

  public String getWord() {
    return word;
  }


  public int getXIndex() {
    return xIndex;
  }

  public int getYIndex() {
    return yIndex;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public String getClue() {
    return clue;
  }

  public int getScore() {
    return score;
  }

  @Override
  public String toString() {
    return word + ": " + "(" + xIndex + "," + yIndex + ")" + ", "
        + orientation;
  }
}
