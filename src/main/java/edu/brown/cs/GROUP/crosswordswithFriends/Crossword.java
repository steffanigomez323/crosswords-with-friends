package edu.brown.cs.GROUP.crosswordswithFriends;

import java.util.Arrays;
import java.util.List;

public class Crossword {

  private Box[][] puzzle;
  private List<String> words;

  public Crossword(List<String> words) {
    puzzle = new Box[5][5];
    this.words = words;

  }

  public Box[][] fillPuzzle() {
    String firstWord = words.get(0);
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        puzzle[i][j] = new Box(firstWord.charAt(j));

      }
    }
    System.out.println(Arrays.deepToString(puzzle));
    return puzzle;

  }
}
