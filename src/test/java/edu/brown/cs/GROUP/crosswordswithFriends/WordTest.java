package edu.brown.cs.GROUP.crosswordswithFriends;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WordTest {

  @Test
  public void wordTest() {
    Word w = new Word("sanitaria", 0, 0, Orientation.ACROSS, 1,
        "a place for mending");
    assertTrue(w.getWord().equals("sanitaria"));
  }

  @Test
  public void XYTest() {
    Word w = new Word("sanitaria", 0, 0, Orientation.ACROSS, 1,
        "a place for mending");
    assertTrue(w.getXIndex() == 0);
    assertTrue(w.getYIndex() == 0);
  }

  @Test
  public void orientationTest() {
    Word w = new Word("sanitaria", 0, 0, Orientation.ACROSS, 1,
        "a place for mending");
    assertTrue(w.getOrientation() == Orientation.ACROSS);

  }

  @Test
  public void clueTest() {
    Word w = new Word("sanitaria", 0, 0, Orientation.ACROSS, 1,
        "a place for mending");
    assertTrue(w.getClue().toString().equals("a place for mending"));
  }

  @Test
  public void scoreTest() {
    Word w = new Word("sanitaria", 0, 0, Orientation.ACROSS, 1,
        "a place for mending");
    assertTrue(w.getScore() == 1);

  }

  @Test
  public void toStringTest() {
    Word w = new Word("sanitaria", 0, 0, Orientation.ACROSS, 1,
        "a place for mending");
    assertTrue(w.toString().equals("sanitaria: (0,0), ACROSS"));

  }
}
