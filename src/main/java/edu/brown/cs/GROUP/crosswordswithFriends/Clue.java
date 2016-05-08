package edu.brown.cs.GROUP.crosswordswithFriends;

/** This class models a "clue" object, that contains a clue, an orientation and
 * the size. */

public class Clue {

  /** This is the clue that the "clue" object holds. */

  private String clue;

  /** This is the orientation of this clue, whether the word that this clue is
   * associated with is down or across. */

  private Orientation orientation;

  /** This is the size of the word associated with this clue. */

  private int size;

  /** This constructor instantiates an instance of this class with a clue string,
   * an orientation, and a size, which the private instance variables are set
   * to.
   * 
   * @param c the clue
   * @param o the orientation
   * @param s the score */

  public Clue(String c, Orientation o, int s) {
    clue = c;
    orientation = o;
    size = s;
  }

  /** This method returns the clue string of this clue.
   * 
   * @return the clue */

  public String getClue() {
    return clue;
  }

  /** This method returns the orientation of this clue.
   * 
   * @return the orientation */

  public Orientation getOrientation() {
    return orientation;
  }

  /** This method returns the size of this clue.
   * 
   * @return the size */

  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    return this.clue;
  }

}
