package edu.brown.cs.GROUP.crosswordswithFriends;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ClueTest {

  @Test
  public void clueTest() {
    Clue c = new Clue("this is a clue", Orientation.DOWN, 1);
    assertTrue(c.getClue().equals("this is a clue"));
  }

  @Test
  public void orientationTest() {
    Clue c = new Clue("this is a clue", Orientation.DOWN, 1);
    assertTrue(c.getOrientation().equals(Orientation.DOWN));
  }

  @Test
  public void sizeTest() {
    Clue c = new Clue("this is a clue", Orientation.DOWN, 1);
    assertTrue(c.getSize() == 1);

  }

}
