package edu.brown.cs.GROUP.words;

import java.util.List;

public interface ClueWord {

  /**
   * Given a length, this method will return a list of all words with that
   * length.
   * @param size the length of the word.
   * @return a list of words
   */

  public List<String> getWord(int size);

  /**
   * Given a word, return the clue of that word.
   * @param word the word
   * @return the clue
   */

  public String getClue(String word);

}
