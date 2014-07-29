package game;

import java.io.IOException;

import models.*;
import gui.*;
import util.TextIO;

/** Parent of all Game classes. Define generic game behavior */
public abstract class Game {

  protected Board board;  //The board that belongs to this game
  protected GUI gui;      //The GUI that belongs to this game, if any.
  
  /** Constructor for a Game with a gui
   * @param b - the board belonging to this game
   * @param g - the gui belonging to this game, if it is run on the desktop
   */
  public Game(Board b, GUI g){
    setBoard(b);
    setGUI(g);
  }
  
  /** Constructor for a Game (no gui)
   * @param b - the board belonging to this game
   */
  public Game(Board b){
    this(b, null);
  }
  
  /** Sets the board of this game - disposes of older board, if any. If new board is non-null, make it belong to this game */
  public void setBoard(Board b){
    if(board != null && board != b) board.dispose();
    board = b;
  }
  
  /** Sets the gui to show this game on - disposes of older gui if any*/
  public void setGUI(GUI g){
    if(gui != null && g != gui) gui.dispose();
    gui = g;
  }
  
  /** Returns true if this has a gui, false otherwise */
  public boolean hasGUI(){
    return gui != null;
  }
  
  /** Returns the board belonging to this */
  public Board getBoard(){
    return board;
  }
  
  /** Called when a hex alters itself on the board - should repaint as necessary so the view reflects the model */
  public abstract void updateHex(Hex h);
  
  /** Resets this game - should reset back to initial layout */
  public abstract void reset();
  
  /** Returns the difficulty of this game */
  public abstract int getDifficulty();
  
  /** Saves the current board w/ random name*/
  public void saveBoard(){
    Board b = new Board(board);
    try {
      TextIO.writeJSON("Sample Maps/" + b.hashCode() + ".json", b);
    } catch (IllegalArgumentException | IOException e) {
      e.printStackTrace();
    }
  }
}
