package com.prismatic.models;

/** Parent of all Game classes. Define generic game behavior */
public abstract class Game {

  Board board;  //The board that belongs to this game
  
  /** Constructor for a Game with a gui
   * @param b - the board belonging to this game
   * @param g - the gui belonging to this game, if it is run on the desktop
   */
  public Game(Board b){
    setBoard(b);
  }
  
  /** Sets the board of this game - disposes of older board, if any. If new board is non-null, make it belong to this game */
  public void setBoard(Board b){
    if(board != null) board.dispose();
    board = b;
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
