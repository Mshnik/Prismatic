package models;

import java.util.ArrayList;
import java.util.Objects;

import gui.*;
import models.Board.Color;
import util.*;


/** Abstract parent of all tiles */
public abstract class Hex{

  public static final int SIDES = 6; //Sides per hex
  public static final Location[][] NEIGHBOR_COORDINATES = {
   {new Location(-1, 0), new Location(-1, 1), new Location(0, 1), new Location(1, 0), new Location(0, -1), new Location(-1, -1)},
   {new Location(-1, 0), new Location(0, 1), new Location(1,1), new Location(1, 0), new Location(1, -1), new Location(0, -1)}
  }; //Location of neighbors in board, if they exist. These are actually vectors.
     //First array is for even col number, second is for odd col number.
  
  public final Board board;        //The board this Hex is on
  public final Location location;  //The location of this Hex in the board.
  public final Location[] neighbors;  //The locations of neighboring hexes. Some may be OOB.
  
  protected boolean neighborsUpdated; //True when the neighbors have been changed
                                      //Since neighborHexes were last calculated.
                                      //Managed by board. Also true initially.
  private Hex[] neighborHexes;    //Neighbors of this hex (as hexes). Calculated lazily as necessary
  
  Hex lighter;        //The hex providing this hex with light. null if this is unlit. 
                      //Should be a neighbor. Visible to subclasses, though some may not use it.
  
  /** Returns true iff:
   *    1) The two hexes are neighbors (both non-null)
   *    2) The colors of the adjacent sides are the same
   */
  public static boolean colorLinked(Hex h1, Hex h2){
    if(h1 == null || h2 == null) return false;
    Hex[] h1Neighbors = h1.getNeighborsWithBlanks();
    for(int i = 0; i < SIDES; i++){
      if(h2 == h1Neighbors[i]){
        int j = Util.mod(i+(SIDES/2), SIDES); //side of h2 that is h1.
        return h1.colorOfSide(i) == h2.colorOfSide(j);
      }
    }
    //h2 not a neighbor of h1
    return false;
  }
  
  
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
    neighbors = new Location[SIDES];
    for(int i = 0; i < SIDES; i++){
      Location vec = NEIGHBOR_COORDINATES[Util.mod(location.col, 2)][i];
      neighbors[i] = new Location(location.row + vec.row, location.col + vec.col);        
    }    
    neighborsUpdated = true;
    board.setHex(this, l.row, l.col);
  }
  
  /** @see Hex(Board b, Location (row, col)) */
  public Hex(Board b, int row, int col) throws IllegalArgumentException{
    this(b, new Location(row, col));
  }
  
  /** Returns the neighbors of this hex, clockwise from above. Will always return an array of lenght SIDES,
   * but may contain nulls.
   * Spots that this does not have a neighbor (off the board) are stored as null.
   * Part of lazy calculation of neighborHexes - only updates if neighborsUpdated is true.
   * Otherwise returns the (already calculated) neighborHexes */
  public Hex[] getNeighborsWithBlanks(){
    if(neighborsUpdated){
      neighborHexes = new Hex[SIDES];
      for(int i = 0; i < SIDES; i++){
        try{
          Location l = neighbors[i];
          neighborHexes[i] = board.getHex(l.row, l.col);
        } catch(ArrayIndexOutOfBoundsException e){
          neighborHexes[i] = null;
        }
      }
      neighborsUpdated = false;
      return neighborHexes;
    } else{
      return neighborHexes;
    }
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
  
  /** Returns true if this hex is lit, false otherwise */
  public boolean isLit(){
    return lighter != null;
  }
  
  /** Causes this hex to try to find light among it's neighbors.
   * If it can, set lighter and return true (this remains lit). 
   * Otherwise return false (this becomes unlit).
   * Make sure to prevent recursions in implementations - receiving light from
   * a hex this is lighting.
   * 
   * @param thisChanged - true if this hex underwent a change, false if it was some other hex
   * @return true if this hex is lit after the search, false otherwise.
   */
  abstract protected boolean findLight(boolean thisChanged);
  
  /** Returns the color of side n of this hex (where side 0 is the top).
   * @throws IllegalArgumentException if n < 0, n > 5.
   */
  abstract public Color colorOfSide(int n) throws IllegalArgumentException;
  
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
  
  /** Signifies that this has been changed; tells the gui (if any) to update this. Call this whenever this is changed */
  protected void draw(){
    GUI gui = GUI.getInstance();
    if(gui != null){
      gui.repaint();
    }
  }
  
}
