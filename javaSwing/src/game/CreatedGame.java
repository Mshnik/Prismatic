package game;

import gui.GUI;

import java.util.LinkedList;

import alg.Creator;
import alg.Creator.Solution;
import models.Color;
import models.Hex;

public class CreatedGame extends Game{

  private Creator.CreatedGame innerGame;
  private int difficulty;

  public CreatedGame(int difficulty) {
    super(null);
    this.difficulty = difficulty;
  }

  /** Creates a random board with known solutions. Prints the solution paths in addition to displaying the board */
  public static void main(String[] args){
//    Game g = new CreatedGame(3);
//    g.gui  = new GUI(g, true);
//    g.reset();
    makeManyBoards();
  }

  @Override
  public void updateHex(Hex h) {
    if(gui != null){
      gui.updateScoreLabel();
      gui.repaint();
    }
  }

  /** Make a buncha boards. */
  public static void makeManyBoards(){
    int i = 17;
    while(i <= 50){
      try{
        CreatedGame g = new CreatedGame(3 + (i / 15));
        g.reset(4 + (i/15), 6 + (i/15) + (i % 15 / 5));
        String n = i + "";
        if (i < 10)
          n = "0" + i;
        g.saveBoard("board" + n);
        System.out.println("Saved board " + (i));
        i++;
      } catch(Exception e){}
    }
  }
  
  @Override
  public void reset() {
    reset(6,10);
  }
  
  public void reset(int rs, int cs){
    innerGame = Creator.fullColPuzzle(difficulty, rs, cs);
    board = innerGame.board;
    board.setGame(this);
    board.relight();
    if(gui != null){
      gui.retile();
      gui.updateScoreLabel();
      gui.updatePuzzleLabel();
    }
  }

  @Override
  public int getDifficulty(){
    return difficulty;
  }

  /** Returns a string of the puzzle in one line */
  public String puzzleString(){
    String str = "";
    for(Color c : innerGame.board.colorsPresent()){
      int count = 0;
      for(LinkedList<Solution> lst : innerGame.solutions.values()){
        for(Solution s : lst){
          if(s.getColor() == c){
            count ++;
          }
        }
      }
      str += (c.toString() + " x" + count + " ");
    }
    return str;
  }

  /** Prints the puzzle as a number of crystals per color */
  private void printPuzzle(){
    System.out.println("\n\nPuzzle: ");
    for(Color c : innerGame.board.colorsPresent()){
      int count = 0;
      for(LinkedList<Solution> lst : innerGame.solutions.values()){
        for(Solution s : lst){
          if(s.getColor() == c){
            count ++;
          }
        }
      }
      System.out.println(c + " x" + count);
    }
  }

  /** Prints the solutions as spark colors and path pairings */
  private void printSolutions(Creator.CreatedGame g){
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
  }
}
