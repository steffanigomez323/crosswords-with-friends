package edu.brown.cs.GROUP.crosswordswithFriends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.brown.cs.GROUP.database.Database;

public class Crossword {

  private Box[][] puzzle;
  private List<String> words;


  private static final int ROWS = 9;
  private static final int COLS = 9;
  private List<String> unusedWords;
  private Set<String> usedWords;
  private List<Word> finalList;
  private Database db;

  public Crossword(List<String> words, Database db) {
    unusedWords = new ArrayList<String>(words);
    usedWords = new HashSet<String>();
    finalList = new ArrayList<Word>();
    puzzle = new Box[COLS][ROWS];
    this.words = words;
    shuffleAndSortWords();
    unusedWords = new ArrayList<String>(words);
    System.out.println(words);
    this.db = db;
  }

  public void fillPuzzle() {

    for (String word : unusedWords) {
      if (!usedWords.contains(word)) {
        fitAndAdd(word);
      }

    }

  }

  public void fitAndAdd(String word) {
    usedWords.add(word);
    boolean fit = false;
    int count = 0;
    while (!fit) {
      if (finalList.size() == 0) {
        System.out.println("why am i in here?");
        Orientation o = Orientation.ACROSS;
        int row = 0;
        int col = 0;
        if (checkFit(col, row, o, word) > 0) {
          fit = true;
          setWord(col, row, o, word);
          System.out.println(this);
        }
      } else {
        // System.out.println("getting in here");
        List<Word> coordList = suggestCoordForWord(word);
        // System.out.println(coordList);
        if (coordList.size() == 0) {
          break;
        }

        Word w = coordList.get(count);
        int row = w.getYIndex();
        int col = w.getXIndex();
        Orientation o = w.getOrientation();
        String currWord = w.getWord();
        if (checkFit(col, row, o, currWord) > 0) {
          fit = true;
          setWord(col, row, o, currWord);
          System.out.println(this);
        } else {
          break;
        }
      }
    }
  }

  public void setWord(int col, int row, Orientation o, String word) {
    System.out.println(word + ": " + "(" + col + "," + row + ")" + ", " + o);
    finalList.add(new Word(word, col, row, o, 1, db.getClue(word
                                                                .toLowerCase())));
    for (int i = 0; i < word.length(); i++) {
      setCell(col, row, word.charAt(i), o);
      if (o == Orientation.ACROSS) {
        col++;
      } else if (o == Orientation.DOWN) {
        row++;
      }
    }
  }

  public void setCell(int col, int row, char c, Orientation o) {
    puzzle[col][row] = new Box(c, o);

  }

  public List<Word> suggestCoordForWord(String word) {
    // System.out.println("stuck word: " + word);
    List<Word> coordList = new ArrayList<Word>();
    for (int i = 0; i < word.length(); i++) {
      for (int j = 0; j < ROWS; j++) {
        for (int k = 0; k < COLS; k++) {
          if (puzzle[k][j] != null) {
            // System.out.print("(" + k + ", " + j + "): ");
            // System.out.println(puzzle[k][j]);
            // System.out.println("why am i stuck in here");

            if (Character.toUpperCase(word.charAt(i)) == puzzle[k][j]
                .getLetter()) {
              // System.out.println("char in concern: " + Character.toUpperCase(
              // word.charAt(i)));
              // System.out.println("puzzle letter: " +
              // puzzle[k][j].getLetter());
              // System.out.println("I should not be getting in here for
              // concern");
              if (j - i >= 0) { // vertical placement
                if (j - i + word.length() <= ROWS) {
                  // System.out.println("getting in vertical");
                  coordList.add(new Word(word, k, j - i, Orientation.DOWN,
                      0));
                }
              }
              // k should be either 0 or 4
              // i should be 4
              if (k - i >= 0) { // horizontal placement
                if (k - i + word.length() <= COLS) {
                  // System.out.println("getting in across");
                  coordList.add(new Word(word, k - i, j, Orientation.ACROSS,
                      0));
                }
              }
            }
          }
        }
      }
    }
    // System.out.println("ever getting to return?")
    return coordList;
  }

  public int checkFit(int col, int row, Orientation o, String word) {
    // System.out.println("getting into check fit");
    // System.out.println("(" + col + ", " + row + "), " + o);
    int length = word.length();
    int score = 1;
    int letterCount = 1;
    for (int i = 0; i < length; i++) {

      Box currBox = puzzle[col][row];
      char currLetter = Character.toUpperCase((word).charAt(i));
      if (currBox != null) {
        if (currBox.getLetter() != currLetter) {
          // System.out.println("this should be getting here");
          return 0;
        }
        if (currBox.getLetter() == currLetter) {
          score += 1;
        }

        if (o == Orientation.DOWN) {
          if (currBox.getLetter() != currLetter) {
            if (col < COLS - 1) {
              if (!isBoxEmpty(col + 1, row)) {
                return 0;
              }
            }
            if (col > 0) {
              if (!isBoxEmpty(col - 1, row)) {
                return 0;
              }
            }
          }
          if (letterCount == 1) {
            if (row > 0) {
              if (!isBoxEmpty(col, row - 1)) {
                return 0;
              }
            }
          }
          if (letterCount == word.length()) {
            if (row < ROWS - 1) {
              if (!isBoxEmpty(col, row + 1)) {
                return 0;
              }
            }
          }
        } else { // ACROSS
          if (currBox.getLetter() != currLetter) {
            if (row > 0) {
              if (!isBoxEmpty(col, row - 1)) {
                return 0;
              }
            }
            if (row < ROWS - 1) {
              if (!isBoxEmpty(col, row + 1)) {
                return 0;
              }
            }
          }
          if (letterCount == 1) {
            if (col > 0) {
              if (!isBoxEmpty(col - 1, row)) {
                return 0;
              }
            }
          }
          if (letterCount == word.length()) {
            if (col < COLS - 1) {
              if (!isBoxEmpty(col + 1, row)) {
                return 0;
              }
            }
          }
        }
      }
      else {
        if (o == Orientation.DOWN) {

          if (col < COLS - 1) {
            if (!isBoxEmpty(col + 1, row)) {
              return 0;
            }
          }
          if (col > 0) {
            if (!isBoxEmpty(col - 1, row)) {
              return 0;
            }
          }

          if (letterCount == 1) {
            if (row > 0) {
              if (!isBoxEmpty(col, row - 1)) {
                return 0;
              }
            }
          }
          if (letterCount == word.length()) {
            if (row < ROWS - 1) {
              if (!isBoxEmpty(col, row + 1)) {
                return 0;
              }
            }
          }
        } else { // ACROSS
          if (row > 0) {
            if (!isBoxEmpty(col, row - 1)) {
              return 0;
            }
          }
          if (row < ROWS - 1) {
            if (!isBoxEmpty(col, row + 1)) {
              return 0;
            }
          }

          if (letterCount == 1) {
            if (col > 0) {
              if (!isBoxEmpty(col - 1, row)) {
                return 0;
              }
            }
          }
          if (letterCount == word.length()) {
            if (col < COLS - 1) {
              if (!isBoxEmpty(col + 1, row)) {
                return 0;
              }
            }
          }
        }
      }
      if (o == Orientation.ACROSS) {
        col++;
      } else {
        row++;
      }
      letterCount += 1;
    }
    return score;
  }

  public boolean isBoxEmpty(int col, int row) {
    Box currBox = puzzle[col][row];
    return currBox == null;

  }

  private void shuffleAndSortWords() {
    Collections.shuffle(words);
    Collections.sort(words, (word1, word2) -> (word2.length() - word1
        .length()));
  }

  public List<Word> getFinalList() {
    return finalList;
  }

  public Box[][] getArray() {
    return puzzle;
  }

  @Override
  public String toString() {
    StringBuffer toReturn = new StringBuffer();
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLS; j++) {
        Box currBox = puzzle[j][i];
        if (currBox == null) {
          toReturn.append("_ ");
        } else {
          toReturn.append(puzzle[j][i].getLetter() + " ");
        }
      }
      toReturn.append("\n");

    }
    return toReturn.toString();
  }
}
