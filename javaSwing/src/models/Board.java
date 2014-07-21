package models;

/** All things hex representation: www.redblobgames.com/grids/hexagons/ */
/** Board is implemented using the odd-q layout on the above page */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import game.Game;

import util.*;

/** Represents the board of hexagonal tiles.
 * Upon construction is empty - to fill with hexes, construct hexes with this as an argument. */
public class Board implements Serializable{

  private static final long serialVersionUID = -5927877006439609670L;

  /** The board of hexes for this board. Stored in "odd-q" layout to
   * make storing a hexagonal grid possible in a 2-D matrix.
   * For illustration, see 
   */
  private Hex[][] board;
  
  /** The game this board belongs to */
  private Game game;
  
  /** The number of moves made on this board. A move is a prism rotation. */
  protected int moves = 0;
  
  /** Returns the number of moves on this board */
  public int getMoves(){
    return moves;
  }
  
  /** Returns the width of the board */
  public int getWidth(){
    return board[0].length;
  }
  
  /** returns the height of the board */
  public int getHeight(){
    return board.length;
  }
  
  /** Returns the game this board belongs to */
  public Game getGame(){
    return game;
  }
  
  /** Sets the game this board belongs to. Throws a runtime exception if the game has already been set */
  public void setGame(Game g) throws RuntimeException{
    if(game != null) throw new RuntimeException("Can't set Game of " + this + " to " + g + " because it is already " + game);
    game = g;
  }
  
  /** Signifies that this board is no longer used - sets the game and board this belongs to to null */
  public void dispose(){
    game = null;
    board = null;
  }
  
  /** Returns the index (0 ... SIDES - 1) of the side of h1 that is facing h2. Returns -1 if the two are not neighbors or h==null */
  public int indexLink(Hex h1, Hex h2){
    if(h1 == null || h2 == null) return -1;
    Hex[] h1Neighbors = h1.getNeighborsWithBlanks();
    for(int i = 0; i < Hex.SIDES; i++){
      if(h2 == h1Neighbors[i]){
        return i;
      }
    }
    //h2 not a neighbor of h1
    return -1;
  }
  
  /** Returns the color that links h1 and h2:
   *    1) The two hexes are neighbors (both non-null), otherwise returns none
   *    2) The colors of the adjacent sides are the same
   */
  public Color colorLinked(Hex h1, Hex h2){
    if(h1 == null || h2 == null) return Color.NONE;
    Hex[] h1Neighbors = h1.getNeighborsWithBlanks();
    for(int i = 0; i < Hex.SIDES; i++){
      if(h2 == h1Neighbors[i]){
        int j = Util.mod(i+(Hex.SIDES/2), Hex.SIDES); //side of h2 that is h1.
        if(h1.colorOfSide(i) == h2.colorOfSide(j)) return h1.colorOfSide(i);
        else return Color.NONE;
      }
    }
    //h2 not a neighbor of h1
    return Color.NONE;
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
  
  /** Re-calcualtes light on whole board */
  public void relight(){
    for(Hex h : allHexes()){
      h.findLight(false);
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
  
  /** Constructor for a board that mirrors board b, but isn't linked to a game and has 0 moves on it */
  public Board(Board b){
    board = new Hex[b.getHeight()][b.getWidth()];
    for(Hex h : b.allHexes()){
      if(h instanceof Prism){
        new Prism(this, h);
      }
      else if(h instanceof Spark){
        new Spark(this, h);
      }
      else if(h instanceof Crystal){
        new Crystal(this, h);
      }
    }
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
