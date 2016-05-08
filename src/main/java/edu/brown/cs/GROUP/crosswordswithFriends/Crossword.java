package edu.brown.cs.GROUP.crosswordswithFriends;

import edu.brown.cs.GROUP.database.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** This class handles the notion of a "crossword", complete with the 2D array of
 * boxes, clues, words, players. This class also handles the construction of a
 * crossword and puts it in a manner that the front end can understand to
 * render. */

public class Crossword {

  /** This 2D array that contains the box grid that models the crossword. */

  private Box[][] puzzle;

  /** This is the list of all the words considered in making this crossword
   * puzzle, meaning all the possible words that could have gone in the puzzle. */

  private List<String> originalList;

  /** This is the list of all the words that ultimately were not in the finished
   * product of the crossword puzzle. */

  private List<String> unusedWords;

  /** This is a final integer denoting that there will the number of rows will be
   * 9. */

  private static final int ROWS = 9;

  /** This is a final integer denoting that the number of columns will be 9. */

  private static final int COLS = 9;

  /** This is the set of words that were used in the creation of the crossword
   * puzzle. */

  private Set<String> usedWords;

  /** This is a list of the words that are in the crossword puzzle. */

  private List<Word> finalList;

  /** This is the database that holds a connection to the database. */

  private Database db;

  /** This is the number of players playing on this crossword. */

  private int players;

  /** This is the constructor that initializes the original list to a list of
   * words passed in, and the database is set to the database variables that is
   * passed into the constructor. The other word lists are instantiated and the
   * unused word list is shuffled and sorted by length. Then fill puzzle is
   * called with the first word of that list, and fills the puzzle.
   *
   * @param originalList the list of words to consider when making the crossword
   * @param db the database variable that holds the connection to the database. */

  public Crossword(List<String> originalList, Database db) {
    usedWords = new HashSet<String>();
    finalList = new ArrayList<Word>();
    puzzle = new Box[ROWS][COLS];
    this.originalList = originalList;
    unusedWords = new ArrayList<String>(originalList);
    shuffleAndSortWords();
    this.db = db;
    players = 1;
    String firstWord = unusedWords.remove(0);
    fillPuzzle(firstWord);
  }

  /** This method takes care of filling the puzzles, by fitting and adding each
   * possible word, until there are at least 4 down and 4 across.
   *
   * @param firstWord the first word to build puzzle off of */

  public void fillPuzzle(String firstWord) {

    Collections.shuffle(unusedWords);
    fitAndAdd(firstWord);
    usedWords.add(firstWord);

    for (String word : unusedWords) {
      if (!usedWords.contains(word)) {
        fitAndAdd(word);
      }
    }
    for (String word : unusedWords) {
      if (!usedWords.contains(word)) {
        fitAndAdd(word);
      }
    }
    int acrossCount = 0;
    int downCount = 0;
    for (int i = 0; i < finalList.size(); i++) {
      if (finalList.get(i).getOrientation() == Orientation.ACROSS) {
        acrossCount += 1;
      } else {
        downCount += 1;
      }
    }
    if (acrossCount < 4 || downCount < 4) {
      refill();
    }

    for (int j = 0; j < COLS; j++) {
      for (int i = 0; i < ROWS; i++) {
        Box currBox = puzzle[i][j];
        if (currBox == null) {
          puzzle[i][j] = new Box();
        }
      }
    }

  }

  /** This method "refills" the entire 2D array of boxes again with a crossword
   * puzzle. */

  public void refill() {
    puzzle = new Box[ROWS][COLS];
    finalList = new ArrayList<Word>();
    unusedWords = new ArrayList<String>(originalList);
    usedWords = new HashSet<String>();
    shuffleAndSortWords();
    String firstWord = unusedWords.remove(0);
    Collections.shuffle(unusedWords);

    fillPuzzle(firstWord);
  }

  /** This method returns the number of players on the crossword.
   *
   * @return the number of players */

  public int getPlayers() {
    return players;
  }

  /** This method increases the player count to 2. */

  public void addPlayer() {
    players = 2;
  }

  /** This method adds the word passed in as an argument to the usedWords list,
   * and then tries coordinates and orientations for the word to try to fit it
   * into the crossword puzzle.
   *
   * @param word the word to add to the crossword puzzle */

  public void fitAndAdd(String word) {
    usedWords.add(word);
    boolean fit = false;
    int count = 0;
    while (!fit) {
      if (finalList.size() == 0) {
        Orientation o = Orientation.ACROSS;
        int row = 0;
        int col = 0;
        if (checkFit(col, row, o, word) > 0) {
          fit = true;
          setWord(col, row, o, word);
        }
      } else {
        List<Word> coordList = suggestCoordForWord(word);
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
        } else {
          break;
        }
      }
    }
  }

  /** This method sets the word into the boxes in the puzzle starting at the
   * given row, column, and orientation.
   *
   * @param col the column
   * @param row the row
   * @param o the orientation
   * @param word the word to set */

  public void setWord(int col, int row, Orientation o, String word) {
    String clue = db.getClue(word.toLowerCase());
    finalList.add(new Word(word, col, row, o, 1, clue));
    for (int i = 0; i < word.length(); i++) {
      if (puzzle[row][col] == null) {
        if (i == 0) {
          puzzle[row][col] =
              new Box(word.charAt(0), clue, o, word.length());
        } else if (i == word.length() - 1) {
          puzzle[row][col] =
              new Box(word.charAt(i), null, o, word.length());
        } else {
          setCell(col, row, word.charAt(i));
        }
      } else if (i == 0) {
        puzzle[row][col].addClue(clue, o, word.length());
      } else if (i == word.length() - 1) {
        puzzle[row][col].addClue(null, o, word.length());
      }

      if (o == Orientation.ACROSS) {
        col++;
      } else if (o == Orientation.DOWN) {
        row++;
      }
    }
  }

  /** This method returns the word located at that particular row, column, and
   * orientation.
   *
   * @param col the column
   * @param row the row
   * @param o the orientation
   * @return the word */

  public String getWord(int col, int row, Orientation o) {
    StringBuffer s = new StringBuffer("");
    if (puzzle[row][col] == null) {
      return "";
    } else {
      while (String.valueOf(puzzle[row][col].getLetter()) != null) {
        s.append(puzzle[row][col].getLetter());
        if (o == Orientation.ACROSS) {
          if (col + 1 < COLS) {
            col++;
          } else {
            break;
          }
        } else if (o == Orientation.DOWN) {
          if (row + 1 < ROWS) {
            row++;
          } else {
            break;
          }
        }
      }
    }
    return s.toString();
  }

  /** This method sets the row and column of the 2D box array to a new box with
   * character c at those indexes.
   *
   * @param col the column
   * @param row the row
   * @param c the character */

  public void setCell(int col, int row, char c) {
    puzzle[row][col] = new Box(c);
  }

  /** This method goes through the crossword puzzle and suggests possible
   * coordinates for the word that is passed in as an argument by checking to
   * see if the letters match up with letters that are already in the crossword
   * puzzles.
   *
   * @param word the word to suggest coordinates for
   * @return a list of words, complete with different row, columns, and
   * orientations */

  public List<Word> suggestCoordForWord(String word) {
    List<Word> coordList = new ArrayList<Word>();
    for (int i = 0; i < word.length(); i++) {
      for (int j = 0; j < ROWS; j++) {
        for (int k = 0; k < COLS; k++) {
          if (puzzle[j][k] != null && Character
              .toUpperCase(word.charAt(i)) == puzzle[j][k].getLetter()) {
            if (j - i >= 0 && j - i + word.length() <= ROWS) { // vertical
                                                               // placement
              coordList.add(new Word(word, k, j - i, Orientation.DOWN, 0));

            }
            if (k - i >= 0 && k - i + word.length() <= COLS) { // horizontal
                                                               // placement
              coordList
                  .add(new Word(word, k - i, j, Orientation.ACROSS, 0));
            }

          }
        }
      }
    }
    return coordList;
  }

  /** This method checks to see if the word will fit in the given row, column,
   * and orientation in the crossword puzzle.
   *
   * @param col the column
   * @param row the row
   * @param o the orientation
   * @param word the word
   * @return the length of the word as a score (?) */

  public int checkFit(int col, int row, Orientation o, String word) {
    int length = word.length();
    int score = 1;
    int letterCount = 1;
    for (int i = 0; i < length; i++) {
      Box currBox = puzzle[row][col];
      char currLetter = Character.toUpperCase((word).charAt(i));
      if (currBox != null) {
        if (currBox.getLetter() != currLetter) {
          return 0;
        }

        if (currBox.getLetter() == currLetter) {
          if (i + 1 < length) {
            if (o == Orientation.DOWN) {
              if (row + 1 < ROWS) {
                Box nextBox = puzzle[row + 1][col];
                if (nextBox != null) {
                  char nextLetterInPuzzle = nextBox.getLetter();
                  char nextLetterInWord =
                      Character.toUpperCase((word).charAt(i + 1));
                  if (nextLetterInPuzzle == nextLetterInWord) {
                    return 0;
                  }

                }
              }
            } else {
              if (col + 1 < COLS) {
                Box nextBox = puzzle[row][col + 1];
                if (nextBox != null) {
                  char nextLetterInPuzzle = nextBox.getLetter();
                  char nextLetterInWord =
                      Character.toUpperCase((word).charAt(i + 1));
                  if (nextLetterInPuzzle == nextLetterInWord) {
                    return 0;
                  }
                }
              }
            }
          }
          score += 1;
        }
        if (o == Orientation.DOWN) {
          if (currBox.getLetter() != currLetter) {
            if (col < COLS - 1 && !isBoxEmpty(col + 1, row)) {
              return 0;
            }
            if (col > 0 && !isBoxEmpty(col - 1, row)) {
              return 0;
            }
          }
          if (letterCount == 1 && row > 0 && !isBoxEmpty(col, row - 1)) {
            return 0;
          }
          if (letterCount == word.length() && row < ROWS - 1
              && !isBoxEmpty(col, row + 1)) {
            return 0;
          }
        } else { // ACROSS
          if (currBox.getLetter() != currLetter) {
            if (row > 0 && !isBoxEmpty(col, row - 1)) {
              return 0;
            }
            if (row < ROWS - 1 && !isBoxEmpty(col, row + 1)) {
              return 0;
            }
          }
          if (letterCount == 1) {
            if (col > 0 && !isBoxEmpty(col - 1, row)) {
              return 0;
            }
          }
          if (letterCount == word.length()) {
            if (col < COLS - 1 && !isBoxEmpty(col + 1, row)) {
              return 0;
            }
          }
        }
      } else {
        if (o == Orientation.DOWN) {
          if (col < COLS - 1 && !isBoxEmpty(col + 1, row)) {
            return 0;
          }
          if (col > 0 && !isBoxEmpty(col - 1, row)) {
            return 0;
          }
          if (letterCount == 1) {
            if (row > 0 && !isBoxEmpty(col, row - 1)) {
              return 0;
            }
          }
          if (letterCount == word.length()) {
            if (row < ROWS - 1 && !isBoxEmpty(col, row + 1)) {
              return 0;
            }
          }
        } else { // ACROSS
          if (row > 0 && !isBoxEmpty(col, row - 1)) {
            return 0;
          }
          if (row < ROWS - 1 && !isBoxEmpty(col, row + 1)) {
            return 0;
          }

          if (letterCount == 1) {
            if (col > 0 && !isBoxEmpty(col - 1, row)) {
              return 0;
            }
          }
          if (letterCount == word.length()) {
            if (col < COLS - 1 && !isBoxEmpty(col + 1, row)) {
              return 0;
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

  /** This method returns whether at the given row and column there is a box or
   * not.
   *
   * @param col the column
   * @param row the row
   * @return a boolean */

  public boolean isBoxEmpty(int col, int row) {
    Box currBox = puzzle[row][col];
    return currBox == null;

  }

  /** This method shuffles and sorts the words according to longest length. */

  public void shuffleAndSortWords() {
    Collections.shuffle(unusedWords);
    Collections.sort(unusedWords,
        (word1, word2) -> (word2.length() - word1.length()));
  }

  /** This method return the final list of words in the crossword puzzle.
   *
   * @return the list of words */

  public List<Word> getFinalList() {
    return finalList;
  }

  /** This method returns the 2D puzzle array of Boxes
   *
   * @return the 2D Box array */

  public Box[][] getArray() {
    return puzzle;
  }

  @Override
  public String toString() {
    StringBuffer toReturn = new StringBuffer();
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLS; j++) {
        Box currBox = puzzle[i][j];
        if (currBox == null) {
          toReturn.append("_ ");
        } else {
          toReturn.append(puzzle[i][j].getLetter() + " ");
        }
      }
      toReturn.append("\n");

    }
    return toReturn.toString();
  }
}
