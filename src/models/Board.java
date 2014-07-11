package models;

/** All things hex representation: www.redblobgames.com/girds/hexagons/ */

/** Represents the board of hexagonal tiles */
public class Board {

  /** Gem colors for prisms, and light colors */
  public enum Color{
    NONE,
    RED,
    BLUE,
    YELLOW,
    GREEN,
    ORANGE,
    PURPLE,
    CYAN,
    PINK
  }
  
  /** The board of hexes for this board. Stored in "odd-q" layout to
   * make storing a hexagonal grid possible in a 2-D matrix.
   * For illustration, see 
   */
  private Hex[][] board;
  
  
  /** Returns the hext at position (r,c) */
  public Hex getHex(int r, int c){
    return board[r][c];
  }
  
  /** Sets the hex at position (r,c). Used in hex construction */
  public void setHex(Hex h, int r, int c){
    board[r][c] = h;
  }
}
