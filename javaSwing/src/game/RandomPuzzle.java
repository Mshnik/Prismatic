package game;

import gui.*;
import models.*;

public class RandomPuzzle extends Game {

  private int difficulty;
  
  /** Constructs a new randompuzzle and shows on gui */
  public RandomPuzzle(int difficulty) {
    super(null, null);
    this.difficulty = difficulty;
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
    board = Board.makeBoard(4,9,difficulty);
    board.setGame(this);
    gui.retile();
    gui.updateScoreLabel();
  }
  
  @Override
  public int getDifficulty(){
    return difficulty;
  }
  
  /** Creates a sample gui and allows playing with it */
  public static void main(String[] args){
    Game g = new RandomPuzzle(3);
    g.gui  = new GUI(g, true);
    g.reset();
  }

}
