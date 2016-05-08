package edu.brown.cs.GROUP.crosswordswithFriends;

/** This class models a Word object, which contains a word as a string, an
 * x-index,a y-index, an orientation, a clue, and a score. This is used in the
 * construction of the actual layout of the crossword puzzle. */

public class Word {

  /** This is the word that the class contains. */

  private String word;

  /** This is the x-index of where the word will go in the grid of tiles on the
   * GUI. */

  private int xIndex;

  /** This is the y-index of where the world will go in the grid of tiles on the
   * GUI. */

  private int yIndex;

  /** This is an enum that defines what orientation the word will go in in the
   * crossword. */

  private Orientation orientation;

  /** This is the clue associated with the word. */

  private String clue;

  /** This is the score of the word. */

  private int score;

  /** This is one constructor that sets the values of the word, the x-index, the
   * y-index, the orientation, and the score.
   *
   * @param w the word
   * @param x the x-index
   * @param y the y-index
   * @param o the orienation
   * @param s the score */

  public Word(String w, int x, int y, Orientation o, int s) {
    word = w;
    xIndex = x;
    yIndex = y;
    orientation = o;
    score = s;
  }

  /** This is the other constructor that sets the word, the x-index,
   *  the y-index, the orientation, the score, and the clue values.
   * @param w the word
   * @param x the x-index
   * @param y the y-index
   * @param o the orientation
   * @param s the score
   * @param c the clue */

  public Word(String w, int x, int y, Orientation o, int s, String c) {
    word = w;
    xIndex = x;
    yIndex = y;
    orientation = o;
    score = s;
    clue = c;
  }

  /** This method returns the word.
   *
   * @return the word */

  public String getWord() {
    return word;
  }

  /** This method returns the x-index of the word.
   *
   * @return the x-index */

  public int getXIndex() {
    return xIndex;
  }

  /** This method returns the y-index of the word.
   *
   * @return the y-index */

  public int getYIndex() {
    return yIndex;
  }

  /** This method returns the orientation of the word.
   *
   * @return the orientation */

  public Orientation getOrientation() {
    return orientation;
  }

  /** This method returns the clue of the word.
   *
   * @return the clue */

  public String getClue() {
    return clue;
  }

  /** This method returns the score of the word.
   *
   * @return the score */

  public int getScore() {
    return score;
  }

  @Override
  public String toString() {
    return word + ": " + "(" + xIndex + "," + yIndex + ")" + ", " + orientation;
  }
}
