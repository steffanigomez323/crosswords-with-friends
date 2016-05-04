package edu.brown.cs.GROUP.crosswordswithFriends;

import java.util.ArrayList;
import java.util.List;

public class Box{
  private boolean isBox;
  private char letter;
  private ArrayList<Clue> clues;

  Box(){
    isBox = true;
    letter = '-';
  }

  Box(char l){
    isBox = false;
    letter = Character.toUpperCase(l);
    clues = new ArrayList<Clue>();

  }

  Box(char l, String c, Orientation o, int s){
    isBox = false;
    letter = Character.toUpperCase(l);
    clues = new ArrayList<Clue>();
    clues.add(new Clue(c, o, s));
  }

  public boolean getIsBox(){
    return isBox;
  }

  public boolean checkVal(char check){
    return letter == Character.toUpperCase(check);
  }

  public void addClue(String c2, Orientation o2, int s2) {
    clues.add(new Clue(c2, o2, s2));
  }

  public char getLetter() {
    return this.letter;
  }

  public List<Clue> getClues() {
    return clues;
  }

  @Override
  public String toString() {
    return letter + "";
  }

}