package edu.brown.cs.GROUP.crosswordswithFriends;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BoxTest {

  @Test
  public void isBoxTest() {

    Box b = new Box('c');
    assertTrue(!b.getIsBox());

  }

  @Test
  public void isNotBoxTest() {
    Box b = new Box();
    assertTrue(b.getIsBox());

  }

  @Test
  public void checkValTest() {
    Box b = new Box('c');
    assertTrue(b.checkVal('C'));
  }

  @Test
  public void addClueTest() {


    Box b = new Box('c', "this is a clue", Orientation.DOWN, 1);
    b.addClue("this is also a clue", Orientation.ACROSS, 1);
    assertTrue(b.getClues().size() == 2);
    assertTrue(b.getClues().get(1).toString().equals("this is also a clue"));
  }

  @Test
  public void clueTest() {
    Box b = new Box('c', "this is a clue", Orientation.DOWN, 1);
    assertTrue(b.getClues().get(0).toString().equals("this is a clue"));

  }
}
