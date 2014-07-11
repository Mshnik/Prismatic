package models;

import java.util.ArrayList;
import java.util.Objects;
import util.*;

/** Abstract parent of all tiles */
public abstract class Hex {

  public static final int SIDES = 6; //Sides per hex
  public static final Location[][] NEIGHBOR_COORDINATES = {
   {new Location(-1, 0), new Location(-1, 1), new Location(0, 1), new Location(1, 0), new Location(0, -1), new Location(-1, -1)},
   {new Location(-1, 0), new Location(0, 1), new Location(1,1), new Location(1, 0), new Location(1, -1), new Location(0, -1)}
  }; //Location of neighbors in board, if they exist. These are actually vectors.
     //First array is for even col number, second is for odd col number.
  
  
  public final Board board;        //The board this Hex is on
  public final Location location;  //The location of this Hex in the board.
  
  /** Stores Board b and Point p as board and location in this hex.
   * Throws IllegalArgumentException if b is null, point p is already occupied on board b,
   * Or if the location is out of bounds. */
  public Hex(Board b, Location l) throws IllegalArgumentException{
    try{
      if(b == null)
        throw new IllegalArgumentException("Can't put hex into null board");
      if(b.getHex(l) != null) 
        throw new IllegalArgumentException("Board " + b + " already has hex at position " +
        		"(" + l.row + "," + l.col + "), can't construct new hex there.");
    } catch(ArrayIndexOutOfBoundsException a){
      throw new IllegalArgumentException("Can't construct hex in " + b + " at " + l  + ": " + a.getMessage());
    }
    board = b;
    location = l;
    board.setHex(this, l.row, l.col);
  }
  
  /** @see Hex(Board b, Location (row, col)) */
  public Hex(Board b, int row, int col) throws IllegalArgumentException{
    this(b, new Location(row, col));
  }
  
  /** Returns the neighbors of this hex, clockwise from above. Will always return an array of lenght SIDES,
   * but may contain nulls.
   * Spots that this does not have a neighbor (off the board) are stored as null */
  public Hex[] getNeighborsWithBlanks(){
    int r = location.row;
    int c = location.col;
    Hex[] n = new Hex[SIDES];
    for(int i = 0; i < SIDES; i++){
      try{
        Location l = NEIGHBOR_COORDINATES[Util.mod(c, 2)][i];
        n[i] = board.getHex(r + l.row,c + l.col);
      } catch(ArrayIndexOutOfBoundsException e){
        n[i] = null;
      }
    }
    return n;
  }
  
  /** Returns the neighbors of this hex, clockwise from above, with nulls removed. 
   * Thus no null elements, but resulting array has length 0 <= x <= 6 */
  public Hex[] getNeighbors(){
    Hex[] a = getNeighborsWithBlanks();
    ArrayList<Hex> temp = new ArrayList<Hex>();
    for(Hex h: a){
      if(h != null)
        temp.add(h);
    }
    return temp.toArray(new Hex[temp.size()]);
  }
  
  @Override
  /** The start of a toString for subclasses that is the location's toString */
  public String toString(){
    return location.toString();
  }
  
  @Override
  /** Hexes are equal if their the boards are the same board, and the locations are equal. */
  public boolean equals(Object o){
    if (! (o instanceof Hex)) return false;
    Hex h = (Hex)o;
    return board == h.board && location.equals(h.location);
  }
  
  @Override
  /** Hashes a hex based on its board and its location */
  public int hashCode(){
    return Objects.hash(board, location);
  }
  
}
