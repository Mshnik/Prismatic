package game;

import gui.GUI;

import java.util.LinkedList;
import java.util.Map.Entry;

import alg.Creator;
import models.Color;
import models.Hex;
import util.Location;

public class CreatedGame extends Game{

  private int difficulty;
  
  public CreatedGame(int difficulty) {
    super(null);
    this.difficulty = difficulty;
  }
  
  /** Creates a random board with known solutions. Prints the solution paths in addition to displaying the board */
  public static void main(String[] args){
    Game g = new CreatedGame(3);
    g.gui  = new GUI(g, true);
    g.reset();
  }

  @Override
  public void updateHex(Hex h) {
      if(gui != null){
        gui.updateScoreLabel();
        gui.repaint();
      }
  }

  @Override
  public void reset() {
    Creator.CreatedGame g = Creator.createPuzzle(difficulty, 2, 2, 5, 9, 1);
    int i = 0;
    for(Entry<Color[], LinkedList<LinkedList<Location>>> e : g.solutions.entrySet()){
      System.out.println("Puzzle " + (++i) + ": ");
      System.out.print("\t");
      for(int j = 0; j < e.getKey().length; j++){
        System.out.print(e.getKey()[j] + " ");
      }
      System.out.print(" -> ");
      for(LinkedList<Location> ll : e.getValue()){
        System.out.print("[" + ll + "]  ");
      }
      System.out.println("");
    }
    board = g.board;
    board.setGame(this);
    gui.retile();
    gui.updateScoreLabel();
  }

  @Override
  public int getDifficulty(){
    return difficulty;
  }
}
