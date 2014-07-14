package models;

/** All things hex representation: www.redblobgames.com/grids/hexagons/ */
/** Board is implemented using the odd-q layout on the above page */


import java.util.ArrayList;
import java.util.Collection;

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
  
  /** Returns the Java awt color corresponding to this color enum value */
  public static java.awt.Color colorFromColor(Color c){
    switch(c){
      case BLUE: return java.awt.Color.BLUE;
      case CYAN: return java.awt.Color.CYAN;
      case GREEN:return java.awt.Color.GREEN;
      case NONE: return new java.awt.Color(0, 0, 0, 1); //clear
      case ORANGE: return java.awt.Color.ORANGE;
      case PINK: return java.awt.Color.PINK;
      case PURPLE: return java.awt.Color.MAGENTA;
      case RED: return java.awt.Color.RED;
      case YELLOW: return java.awt.Color.YELLOW;
      default: return java.awt.Color.white;
    }
  }
  
  /** The board of hexes for this board. Stored in "odd-q" layout to
   * make storing a hexagonal grid possible in a 2-D matrix.
   * For illustration, see 
   */
  private Hex[][] board;
  
  /** Returns the width of the board */
  public int getWidth(){
    return board[0].length;
  }
  
  /** returns the height of the board */
  public int getHeight(){
    return board.length;
  }
  
  /** @See getHex(row, col) */
  public Hex getHex(Location l) throws ArrayIndexOutOfBoundsException{
    return getHex(l.row, l.col);
  }
  
  /** Returns the hext at position (r,c) */
  public Hex getHex(int r, int c) throws ArrayIndexOutOfBoundsException{
    return board[r][c];
  }
  
  /** Returns all hexes in no particular order - useful for collection purposes. */
  public Collection<Hex> allHexes(){
    ArrayList<Hex> h = new ArrayList<Hex>();
    for(Hex[] hRow : board){
      for(Hex hex : hRow){
        h.add(hex);
      }
    }
    return h;
  }
  
  /** Sets the hex at position (r,c). Also sets all neighbor hexes as needing a neighbor update.
   * Hex must have this as its board - otherwise throws illegalargexception
   * Used in hex construction, not much elsewhere. */
  protected void setHex(Hex h, int r, int c) throws IllegalArgumentException, ArrayIndexOutOfBoundsException{
    if(h.board != this) throw new IllegalArgumentException("Can't put hex belonging to " + h.board + " in board " + this);
    
    board[r][c] = h; //ArrayIndexOutOfBounds may be thrown here
    
    //For each neighbor of this hex, tell it that it's neighbors have changed.
    for(Location l : h.neighbors){
      try{
        Hex hNeighbor = getHex(l);
        if(hNeighbor != null) hNeighbor.neighborsUpdated = true;
      } catch(ArrayIndexOutOfBoundsException e){}
    }
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
  
  /** Called by a Hex when it changes (rotates, changes light color, etc).
   * Tells each of the hex's neighbors that it has 
   */
  
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
