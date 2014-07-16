package game;

import models.*;
import gui.*;

/** Parent of all Game classes. Define generic game behavior */
public abstract class Game {

  Board board;  //The board that belongs to this game
  GUI gui;      //The GUI that belongs to this game, if any.
  
  /** Constructor for a Game with a gui
   * @param b - the board belonging to this game
   * @param g - the gui belonging to this game, if it is run on the desktop
   */
  public Game(Board b, GUI g){
    board = b;
    board.setGame(this);
    gui = g;
  }
  
  /** Constructor for a Game (no gui)
   * @param b - the board belonging to this game
   */
  public Game(Board b){
    this(b, null);
  }
  
  /** Sets the gui to show this game on - disposes of older gui if any*/
  public void setGUI(GUI g){
    if(gui != null) gui.dispose();
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
  
}
