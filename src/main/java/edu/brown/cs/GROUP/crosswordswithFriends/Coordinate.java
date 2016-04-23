package edu.brown.cs.GROUP.crosswordswithFriends;

public class Coordinate {

  private int row;
  private int col;
  private Orientation o;
  private int score;

  public Coordinate(int row, int col, Orientation o, int score) {
    this.row = row;
    this.col = col;
    this.o = o;
    this.score = score;
  }

  public int getRow() {
    return this.row;
  }

  public int getCol() {
    return this.col;
  }

  public Orientation getOrientation() {
    return this.o;
  }

  public int getScore() {
    return this.score;
  }

}
