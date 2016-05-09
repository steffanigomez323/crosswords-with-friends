package edu.brown.cs.GROUP.crosswordswithFriends;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.brown.cs.GROUP.database.Database;

public class GUITest {

  private static Database db;
  private static int port = 9997;
  private static GUI g;
  private static List<String> unusedWords;
  private static Crossword cw;

  @BeforeClass
  public static void setUpClass() throws Exception {
    g = new GUI(port, db);

    try {
      db = new Database("data/cluewords.sqlite3");
      unusedWords = db.getAllUnderNine();
      cw = new Crossword(unusedWords, db);
      GUI.getCrosswordCache().put(1000, cw);
    } catch (ClassNotFoundException e) {
      System.out.println("Class not found");
    } catch (SQLException e) {
      System.out.println("SQL Exception");
    }
  }

  @Test
  public void getAnagramTest() {
    String firstWord = cw.getWord(0, 0, Orientation.ACROSS);
    String anagram = GUI.getAnagram(9, 0, 0, Orientation.ACROSS, 1000);
    assertTrue(isAnagram(firstWord, anagram));

  }

  public static boolean isAnagram(String firstWord, String secondWord) {
    char[] word1 = firstWord.replaceAll("[\\s]", "").toCharArray();
    char[] word2 = secondWord.replaceAll("[\\s]", "").toCharArray();
    Arrays.sort(word1);
    Arrays.sort(word2);
    return Arrays.equals(word1, word2);
  }

  @Test
  public void checkValidTest() {
    String firstWord = cw.getWord(0, 0, Orientation.ACROSS);
    String substring = firstWord.substring(0, 5);
    assertTrue(!GUI.checkValid(substring, 0, 0, Orientation.ACROSS, 1000));
    assertTrue(GUI.checkValid(firstWord, 0, 0, Orientation.ACROSS, 1000));
  }

  @Test
  public void getLetterTest() {
    char firstLetter = cw.getWord(0, 0, Orientation.ACROSS).charAt(0);
    assertTrue(GUI.getLetter(0, 0, 1000) != 0);
    assertTrue(firstLetter == GUI.getLetter(0, 0, 1000));
  }
}
