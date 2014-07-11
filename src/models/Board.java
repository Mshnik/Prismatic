package models;

/** All things hex representation: www.redblobgames.com/grids/hexagons/ */
/** Board is implemented using the odd-q layout on the above page */


import util.*;

/** Represents the board of hexagonal tiles.
 * Upon construction is empty - to fill with hexes, construct hexes with this as an argument. */
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
  
  /** @See getHex(row, col) */
  public Hex getHex(Location l) throws ArrayIndexOutOfBoundsException{
    return getHex(l.row, l.col);
  }
  
  /** Returns the hext at position (r,c) */
  public Hex getHex(int r, int c) throws ArrayIndexOutOfBoundsException{
    return board[r][c];
  }
  
  /** Sets the hex at position (r,c). 
   * Hex must have this as its board - otherwise throws illegalargexception
   * Used in hex construction, not much elsewhere. */
  protected void setHex(Hex h, int r, int c) throws IllegalArgumentException, ArrayIndexOutOfBoundsException{
    if(h.board != this) throw new IllegalArgumentException("Can't put hex belonging to " + h.board + " in board " + this);
    board[r][c] = h;
  }
  
  /** Constructor for an empty board of size rs*cs */
  public Board(int rs, int cs) throws IllegalArgumentException {
    if (rs < 0 || cs < 0) throw new IllegalArgumentException("Illegal Board Construction for Dimensions " + rs + ", " + cs);
    board = new Hex[rs][cs];
  }
  
  /** Default size for constructing a default (square) board */
  public static final int DEFAULT_BOARD_SIZE = 10;
  
  /** Constructor for an empty default board, of sidze default_board_size ^2 */
  public Board(){
    this(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE);
  }
  
  @Override
  /** Two boards are equivalent if the refer to the same board (not equal boards */
  public boolean equals(Object o){
    if (! (o instanceof Board)) return false;
    Board b = (Board)o;
    return b.board == board;
  }
  
  @Override
  /** Hashes a Board based on its board matrix */
  public int hashCode(){
    return board.hashCode();
  }
  
}
