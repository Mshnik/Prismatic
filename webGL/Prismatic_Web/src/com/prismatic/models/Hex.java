package com.prismatic.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.prismatic.util.*;



/** Abstract parent of all tiles */
public abstract class Hex implements Serializable{

  private static final long serialVersionUID = 8566479292297887732L;
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
  Color lit = Color.NONE;  //Used to remember the color this was lit while changing liters. identical to lighter.isLit outside of light changing process.
                           //Hexes can change the lighter of other prisms, but should leave changing lit to the hex it belongs to
  
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
  
  /** Creates a copy of a hex, but bound to a new board */
  public Hex(Board b, Hex h) throws IllegalArgumentException{
    this(b, h.location);
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
  
  /** @See Hex.colorLinked(this, h) */
  public Color colorLinked(Hex h){
    return board.colorLinked(this, h);
  }

  /** @See indexLink(this, h) */
  public int indexLink(Hex h){
    return board.indexLink(this, h);
  }
  
  /** Returns the color this hex is lit, NONE otherwise */
  public Color isLit(){
    if(lighter != null)
      return lighter.isLit();
    else
      return Color.NONE;
  }
  
  /** Returns the chain of hexes providing this light */
  public List<Hex> lighterList(){
    Hex l = lighter;
    List<Hex> lst = new LinkedList<Hex>();
    while(l != null){
      lst.add(l);
      l = l.lighter;
    }
    return lst;
  }
  
  /** Causes this hex to try to find light among it's neighbors.
   * If it can, set lighter and return true (this remains lit). 
   * Otherwise return false (this becomes unlit).
   * Make sure to prevent recursions in implementations - receiving light from
   * a hex this is lighting.
   * 
   * @param thisChanged - true if this hex underwent a change, false if it was some other hex
   * @return The color this is lit after the change
   */
  abstract protected Color findLight(boolean thisChanged);
  
  /** Helper method for use in findLight implementations. Tells neighbors this is currently lighting to look elsewhere */
  void stopProvidingLight(){
    for(Hex h : getNeighbors()){
      if(h.lighter == this)
        h.findLight(false);
    }
  }
  
  /** Helper method for use in findLight implementations. Tells neighbors that this is now lit, 
   * maybe get light from this, if not already or this getting light from that.
   * If this isn't lit, do nothing. 
   * 
   * Note: Always try to provide light to crystal, never try to provide light to spark. Neither of these recurse, so no trouble*/
  void provideLight(){
    if(isLit() != Color.NONE){
      for(Hex h : getNeighbors()){
        if((h instanceof Crystal) || !(h instanceof Spark) && colorLinked(h) == isLit() && lighter != h && h.lighter != this && lit != h.lit)
          h.findLight(false);
      }
    }
  }
  
  /** Helper method for use in findLight implementations. Tries to find light among neighbors.
   *  If a link is found, sets that neighbor as lighter. If no link found, sets lighter to null.
   *  Only looks for preferred. If preferred is NONE, takes any color. */
  void findLightProvider(Color preferred){
    lighter = null;
    for(Hex h : getNeighbors()){
      if(h.lighter != this && h.lit != Color.NONE && colorLinked(h) == h.lit && (preferred == Color.NONE || h.lit == preferred) && !h.lighterList().contains(this)){ 
        lighter = h;
        return;
      }
    }
  }
  
  /** Returns the color of side n of this hex (where side 0 is the top).
   * @throws IllegalArgumentException if n < 0, n > 5.
   */
  abstract public Color colorOfSide(int n) throws IllegalArgumentException;
   
  /** Perform default behavior for 'interacting' (clicking) this hex */
  abstract public void click();
  
  /** Returns this as a prism, if the cast is allowed. Throws runtimeexception otherwise */
  public Prism asPrism() throws RuntimeException{
    if(! (this instanceof Prism)) throw new RuntimeException("Can't cast " + this + " to a Prism");
    return (Prism)this;
  }
  
  /** Returns this as a Spark, if the cast is allowed. Throws runtimeexception otherwise */
  public Spark asSpark() throws RuntimeException{
    if(! (this instanceof Spark)) throw new RuntimeException("Can't cast " + this + " to a Spark");
    return (Spark)this;
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
  
  /** Signifies that this has been changed; tells the game (if any) to update this, as necessary. Call this whenever this is changed */
  protected void update(){
    if(board != null && board.getGame() != null)
      board.getGame().updateHex(this);
  }
  
}
