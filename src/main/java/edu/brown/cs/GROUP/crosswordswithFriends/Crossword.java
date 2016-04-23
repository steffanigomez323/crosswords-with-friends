package edu.brown.cs.GROUP.crosswordswithFriends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Crossword {

  private Box[][] puzzle;
  private List<String> words;


  private static final int ROWS = 7;
  private static final int COLS = 7;
  private List<String> unusedWords;
  private Set<String> usedWords;
  private List<Word> finalList;

  public Crossword(List<String> words) {
    unusedWords = new ArrayList<String>(words);
    usedWords = new HashSet<String>();
    finalList = new ArrayList<Word>();
    puzzle = new Box[ROWS][COLS];
    this.words = words;
    shuffleWords();


  }

  public void fillPuzzle() {
    List<String> unusedWords = new ArrayList<String>(words);
    Set<String> usedWords = new HashSet<String>();
    long currTime = System.currentTimeMillis();
    long end = currTime + 2000;

    while(System.currentTimeMillis() < end) {
      for (String word : unusedWords) {
        if (!usedWords.contains(word)) {
          fitAndAdd(word);
        }

      }
    }
  }

  public void fitAndAdd(String word) {
    boolean fit = false;
    List<Word> coordList = suggestCoordForWord(word);
    while (!fit) {
      if (usedWords.size() == 0) {
        // finalList.add(new Word("", 0, 0, Orientation.ACROSS, "\"Dude, cmon
        // ...,\" in modern lingo")))
        Orientation o = Orientation.ACROSS;
        int row = 0;
        int col = 0;
        fit = true;
        setWord(row, col, o, word);
      } else {
        for (int i = 0; i < coordList.size(); i++) {
          Word w = coordList.get(i);
          if (w.getScore() > 0) {
            fit = true;
            setWord(w.getYIndex(), w.getXIndex(), w.getOrientation(), w
                .getWord());
          }
        }
      }
    }
  }

  public void setWord(int row, int col, Orientation o, String word) {
    usedWords.add(word);
    // finalList.add(new Word(col, row, ))
    for (int i = 0; i < word.length(); i++) {
      setCell(row, col, word.charAt(i));
      if (o == Orientation.ACROSS) {
        col++;
      } else if (o == Orientation.DOWN) {
        row++;
      }
    }
  }

  public void setCell(int row, int col, char c) {
    puzzle[row][col] = new Box(c);

  }

  public List<Word> suggestCoordForWord(String word) {
    List<Word> coordList = new ArrayList<Word>();
    for (int i = 0; i < word.length(); i++) {
      for (int j = 0; j < ROWS; j++) {
        for (int k = 0; k < COLS; k++) {
          if (Character.toUpperCase(word.charAt(i)) == puzzle[j][k]
              .getLetter()) {
            if (j - i >= 0) { // vertical placement
              if (j - i + word.length() <= ROWS) {
                coordList.add(new Word(word, k, j - i, Orientation.DOWN,
                    1));
              }
            }
            if (k - i >= 0) { // horizontal placement
              if (k - i + word.length() <= COLS) {
                coordList.add(new Word(word, k - i, j, Orientation.ACROSS,
                    1));
              }
            }
          }
        }
      }
    }
    return coordList;
  }

  private void shuffleWords() {
    Collections.shuffle(words);
  }
}
