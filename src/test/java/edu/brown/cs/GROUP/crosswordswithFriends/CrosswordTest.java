package edu.brown.cs.GROUP.crosswordswithFriends;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.brown.cs.GROUP.database.Database;

public class CrosswordTest {

  private static List<String> unusedWords;
  private static Database db;
  private static Crossword cw;

  @BeforeClass
  public static void setUpClass() throws Exception {

    try {
      db = new Database("data/cluewords.sqlite3");
      unusedWords = db.getAllUnderNine();
      cw = new Crossword(unusedWords, db);

    } catch (ClassNotFoundException e) {
      System.out.println("Class not found");
    } catch (SQLException e) {
      System.out.println("SQL Exception");
    }
  }

  @Test
  public void shuffleAndSortTest() {
    cw.shuffleAndSortWords();
    String firstWord = unusedWords.get(0);
    assertTrue(firstWord.length() == 9);
    Collections.shuffle(unusedWords);
    assertTrue(unusedWords.get(0) != firstWord);

  }

  @Test
  public void isBoxEmptyTest() {
    assertTrue(!cw.isBoxEmpty(0, 0));

  }

  @Test
  public void checkFitTest() {
    assertTrue(cw.checkFit(0, 0, Orientation.ACROSS, "WAEASFS") == 0);

  }

  @Test
  public void suggestCoordTest() {
    char firstLetter = cw.getWord(0, 0, Orientation.ACROSS).charAt(0);
    String word = firstLetter + "blah";
    assertTrue(cw.suggestCoordForWord(word).size() != 0);

  }

  @Test
  public void getWordTest() {
    assertTrue(cw.getWord(0, 0, Orientation.ACROSS) != null);

  }

  @Test
  public void longFirstWordTest() {
    String firstWord = cw.getWord(0, 0, Orientation.ACROSS);
    assertTrue(firstWord.length() == 9);

  }

  @Test
  public void setCellTest() {
    cw.setCell(0, 0, 'a');
    assertTrue(!cw.isBoxEmpty(0, 0));

  }

}
