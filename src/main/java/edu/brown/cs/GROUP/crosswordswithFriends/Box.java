package edu.brown.cs.GROUP.crosswordswithFriends;

import java.util.ArrayList;

public class Box{
  private boolean isBox;
  private char letter;
  private ArrayList<Clue> clues;


  Box(){
    isBox = true;
    clues = new ArrayList<Clue>();
  }

  Box(char l){
    isBox = false;
    letter = Character.toUpperCase(l);
    clues = new ArrayList<Clue>();
  }

  Box(char l, String c, Orientation o){
    isBox = false;
    letter = Character.toUpperCase(l);
    clues = new ArrayList<Clue>();
    clues.add(new Clue(c, o));
  }

  public boolean getIsBox(){
    return isBox;
  }

  public boolean checkVal(char check){
    return letter == Character.toUpperCase(check);
  }

  public void addClue(String c2, Orientation o2) {
    clues.add(new Clue(c2, o2));
  }

  public char getLetter() {
    return this.letter;
  }

  public void printLetter(){
    if (letter == '\u0000'){
      System.out.print("-");
    } else {
      System.out.print(letter);
    }
    //    if (!clues.isEmpty()){
    //      System.out.print(clues.size());
    //    }
    //    System.out.print(" ");
  }

  @Override
  public String toString() {
    return Character.toString(this.letter);
  }

}