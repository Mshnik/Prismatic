package game;

import gui.GUI;

import java.util.LinkedList;

import alg.Creator;
import alg.Creator.Solution;
import models.Hex;

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
    Creator.CreatedGame g = Creator.createPuzzle(difficulty, 2, 2, 5, 9, 3);
    int i = 0;
    for(LinkedList<Solution> lst : g.solutions.values()){
      System.out.println("Puzzle " + (++i) + ": ");
      System.out.print("\t");
      for(Solution s : lst){
        System.out.print(s.getColor() + " ");
      }
      System.out.print(" -> ");
      for(Solution s : lst){
        System.out.print("[" + s.getPath() + "]  ");
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
