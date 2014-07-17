package game;

import util.ObjectIO;
import gui.GUI;
import models.Board;
import models.Hex;

public class LoadedGame extends Game {

  private Board initialBoard; //a copy of the board that is loaded from memory
  
  /** Initializes a blank game. Need to load. */
  public LoadedGame() {
    super(null, null);
  }

  @Override
  public void updateHex(Hex h) {
    if(gui != null){
      gui.updateScoreLabel();
      gui.repaint();
    }
  }

  @Override
  /** Resets the game by resetting to the initial board */
  public void reset() {
    if(board != null) board.dispose();
    board = initialBoard;
    gui.retile();
    gui.updateScoreLabel();
  }

  @Override
  /** TODO. */
  public int getDifficulty() {
    return 7;
  }
  
  /** Allows loading a game from memory */
  public void load(){
    Board b = (Board)ObjectIO.load(Board.class, "Sample Maps");
    b.setGame(this);
    initialBoard = b;
    gui = new GUI(this);
    reset();
  }

  /** Creates a sample gui and allows playing with it */
  public static void main(String[] args){
    Game g = new LoadedGame();
    g.gui  = new GUI(g);
    g.reset();
  }
  
}
