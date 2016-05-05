package edu.brown.cs.GROUP.crosswordswithFriends;

/**
 * This class models a "coordinate" object, which has a row, a column, an
 * orientation, and a score. This is used in the rendering of the crossword in
 * the GUI.
 *
 */

public class Coordinate {

  /**
   * This is the row that this particular coordinate is in in the crossword
   * grid.
   */

  private int row;

  /**
   * This is the column that this particular coordinate is in the crossword.
   */

  private int col;

  /**
   * This is the orientation of this particular coordinate; this coordinate is
   * either going down or across.
   */

  private Orientation o;

  /**
   * This is the score of the coordinate.
   */

  private int score;

  /**
   * This is the constructor that instantiates the private instance variables to
   * the arguments passed into the constructor.
   * @param r the row
   * @param c the column
   * @param o the orientation
   * @param s the score
   */

  public Coordinate(int r, int c, Orientation o, int s) {
    this.row = r;
    this.col = c;
    this.o = o;
    this.score = s;
  }

  /**
   * This method returns the row of this coordinate.
   * @return the row
   */

  public int getRow() {
    return this.row;
  }

  /**
   * This method returns the column of this coordinate.
   * @return the column
   */

  public int getCol() {
    return this.col;
  }

  /**
   * This method returns the orientation of this coordinate.
   * @return the orientation
   */

  public Orientation getOrientation() {
    return this.o;
  }

  /**
   * This method returns the score associated with this coordinate.
   * @return the score
   */

  public int getScore() {
    return this.score;
  }

}
