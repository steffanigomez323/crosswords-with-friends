package edu.brown.cs.GROUP.crosswordswithFriends;

import java.util.ArrayList;
import java.util.List;

/** This class models a box in a crossword puzzle, which is a grid of boxes.
 *  Each box contains a boolean that determines whether it is part of a word
 *   or not, the letter that goes in it, if it is, and a list of clues that
 *    are associated with the words associated with this particular box. */

public class Box {

  /** This is the boolean that determines whether this box is part of a word or
   * whether it is just black. It is true if the box is not part of word. */

  private boolean isBox;

  /** If it is a box, then this variable holds the letter associated with it. */

  private char letter;

  /** This is a list of clues that are associated with the letter of the word(s)
   * that go through this box. */

  private ArrayList<Clue> clues;

  /** This is a constructor that without any arguments simply creates the box
   * object, sets it to be a box and is associated with the letter that is a
   * hyphen. */

  Box() {
    isBox = true;
    letter = '-';
  }

  /** If the constructor contains the argument of a single letter, then it is
   * part of a word, and it is the boolean is set to false, the letter made
   * uppercase, and clues instantiated.
   *
   * @param l the letter */

  Box(char l) {
    isBox = false;
    letter = Character.toUpperCase(l);
    clues = new ArrayList<Clue>();

  }

  /** If the constructor has arguments that are a letter, a clue, an
   * orientation, and a score, then all the private instance variables
   *  are instantiated with those values.
   *
   * @param l the letter
   * @param c the clue
   * @param o the orientation
   * @param s the score */

  Box(char l, String c, Orientation o, int s) {
    isBox = false;
    letter = Character.toUpperCase(l);
    clues = new ArrayList<Clue>();
    clues.add(new Clue(c, o, s));
  }

  /** This method returns a boolean indicating whether the box is part of a word
   * or not.
   *
   * @return the boolean */

  public boolean getIsBox() {
    return isBox;
  }

  /** This method checks whether the argument is equal to the letter that this
   * box contains. This is used to check whether an entered word is correct in
   * the crossword.
   *
   * @param check the character to check
   * @return a boolean indicating whether it is or isn't */

  public boolean checkVal(char check) {
    return letter == Character.toUpperCase(check);
  }

  /** This method adds a Clue to the list of clues kept by this instance of Box.
   * A clue contains a clue, an orientation and a score.
   *
   * @param c2 the clue
   * @param o2 the orientation
   * @param s2 the score */

  public void addClue(String c2, Orientation o2, int s2) {
    clues.add(new Clue(c2, o2, s2));
  }

  /** This method returns the letter kept by this box.
   *
   * @return the character */

  public char getLetter() {
    return this.letter;
  }

  /** This method returns the list of clues kept by this box, the clues
   * associated with the letter this box holds.
   *
   * @return the list of clues */

  public List<Clue> getClues() {
    return clues;
  }

  @Override
  public String toString() {
    return letter + "";
  }

}
