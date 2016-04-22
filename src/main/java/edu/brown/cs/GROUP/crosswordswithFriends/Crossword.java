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
    puzzle = new Box[5][5];
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
    int count = 0;
    while (!fit && count < 1000) {
      if (usedWords.size() == 0) {
        // finalList.add(new Word("", 0, 0, Orientation.ACROSS, "\"Dude, cmon
        // ...,\" in modern lingo")))
        int vertical = 0;
        int row = 0;
        int col = 0;


      }
    }

  }

  private void shuffleWords() {
    Collections.shuffle(words);
  }
}
