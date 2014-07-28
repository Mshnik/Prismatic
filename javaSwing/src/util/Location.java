package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import models.*;

/** A simple location class - honestly a Tuple of ints, but java doesn't have that :/
 * Using this over the Point class because constantly shifting from (x,y) to (col,row) is confusing
 * @author MPatashnik
 */
public class Location implements Serializable{
  
  private static final long serialVersionUID = 998107612631009984L;

  /** A default instance of Location that represents nowhere */
  public static final Location NOWHERE = new Location(Integer.MIN_VALUE, Integer.MIN_VALUE);

  public final int row;
  public final int col;
  
  //Used internally for A* running. May be altered by outside classes
  public Location prev;
  public int dist;
  
  /** Constructs a location corresponding to row r, col c. */
  public Location(int r, int c){
    row = r;
    col = c;
    prev = null;
    dist = Integer.MAX_VALUE;
  }
  
  /** Constructs a location from cube coordinates 
   * @coord - a length three array of {x,y,z} coordinates of this in the cube coordinate system */
  public static Location fromCubeCoordinates(int[] coord) throws IllegalArgumentException{
    if(coord == null || coord.length != 3)
      throw new IllegalArgumentException("Can't create location from cube coordinates: " + coord + "; should have length 3");
    
    int c = coord[0];
    int r = coord[2] + (c - (c&1))/2;
    return new Location(r,c);
  }
  
  /** Constructs a location as the vector from from to to */
  public static Location vec(Location from, Location to){
    return new Location(to.row - from.row, to.col - from.col);
  }
 
  /** Constructs a random location in the range [0 ... maxR), [0 ... maxC) */
  public static Location random(int maxR, int maxC){
    return new Location((int)(Math.random() * maxR), (int)(Math.random() * maxC));
  }
  
  /** Returns true if this is out of bounds, that is has row/col < 0, row/col > boundsR, bounds C.
   * 
   */
  public boolean isOOB(int maxR, int maxC){
    return row < 0 || col < 0 || row > maxR || col > maxC;
  }
  
  /** Returns the cube coorinates (x,y,z) for this location.
   * Always returns the array of length 3: {x,y,z} */
  public int[] cubeCoordinates(){
    int[] i = new int[3];
    i[0] = col;
    i[2] = row - (col - (col & 1))/2;
    i[1] = -i[0]-i[2];
    return i;
  }
  
  /** Returns the distance from this to dest using cube coordinate distance */
  public int distance(Location dest){
    int[] c1 = cubeCoordinates();
    int[] c2 = dest.cubeCoordinates();
    return (Math.abs(c1[0] - c2[0]) + Math.abs(c1[1] - c2[1]) + Math.abs(c1[2] - c2[2])) / 2;
  }
  
  /** Returns the given locations in order of closeness to this.
   * Ties are broken arbitrarily.
   * 
   * Out of bounds locations always go at end.
   */
  public Location[] orderByCloseness(Location[] loc, int maxR, int maxC){
    ArrayList<Location> loc2 = new ArrayList<Location>();
    for(Location l : loc){
      loc2.add(l);
    }
    Collections.sort(loc2, new ClosenessComparator(this, maxR, maxC));
    return loc2.toArray(new Location[loc.length]);
  }
  
  /** Returns the neighbors of this using the Hex.NEIGHBOR_COORDINATES for vectors */
  public Location[] neighbors(){
    Location[] l = new Location[Hex.SIDES];
    for(int i = 0; i < Hex.SIDES; i++){
      l[i] = new Location(row + Hex.NEIGHBOR_COORDINATES[col % 2][i].row, col + Hex.NEIGHBOR_COORDINATES[col % 2][i].col);
    }
    return l;
  }
  
  /** Returns true if this is adjacent to the given location, 
   * using the Hex.NEIGHBOR_COORDAINTES for neighbor vectors */
  public boolean isAdjacentTo(Location l){
    for(Location vec : Hex.NEIGHBOR_COORDINATES[col % 2]){
      if(l.row == row + vec.row && l.col == col + vec.col)
        return true;
    }
    return false;
  }
  
  /** Simple string representation of a Location in (row, col) form */
  @Override
  public String toString(){
    return "(" + row + "," + col + ")";
  }
  
  /** Returns the location object stored in string s. Assumes that s follows the toString pattern in the location class **/
  public static Location fromString(String s){
    int i = s.indexOf(",");
    return new Location(Integer.parseInt(s.substring(1,i)), Integer.parseInt(s.substring(i+1, s.length() - 1)));
  }
  
  /** Compares equality of Locations by their coordinates */
  @Override
  public boolean equals(Object o){
    if(! (o instanceof Location)) return false;
    Location l = (Location)o;
    return row == l.row && col == l.col;
  }
  
  /** Hashes Location based on its row and col. */
  @Override
  public int hashCode(){
    return Objects.hash(row, col);
  }
  
  /** Comparator for sorting locations by closeness. */
  public static class ClosenessComparator implements Comparator<Location>{

    private Location here;
    private int maxR;
    private int maxC;
    
    public ClosenessComparator(Location here, int maxR, int maxC){
      this.here = here;
      this.maxR = maxR;
      this.maxC = maxC;
    }
    
    @Override
    public int compare(Location o1, Location o2) {
      if(o1.isOOB(maxR, maxC) && o2.isOOB(maxR, maxC))
        return 0;
      else if(o1.isOOB(maxR, maxC))
        return 1;
      else if(o2.isOOB(maxR, maxC))
        return -1;
      
      return here.distance(o1) - here.distance(o2);
    } 
  }
  
  /** Comparator for sorting locations by their distance field plus a heuristic to goal (for A*) */
  public static class DistanceComparator implements Comparator<Location>{
    
    private Location goal;
    
    public DistanceComparator(Location goal){
      this.goal = goal;
    }
    
    @Override
    public int compare(Location o1, Location o2) {
      return (o1.dist + o1.distance(goal)) - (o2.dist + o2.distance(goal));
    }    
  }
  
  
  /** Resets this location for running dijkstra's */
  public void reset(){
    prev = null;
    dist = Integer.MAX_VALUE;
  }
  
  /** Retrieve's this' neighbors in the board
   * Allows A* data to persist between neighbor calls.
   * Uses board to check for color linking. - allows neighbors of color c or color.any. 
   * If Color.NONE passed in, doesn't check color. If color.any passed in, only allows wild cards.
   * Doesn't add oob neighbors -- adds null in place.
   * 
   * Sparks and crystals always added to neighbors, in case they are wanted. If they are not, filter out later.
   */
  public Location[] neighborsInGraph(Board b, Color c){
    Hex here = b.getHex(row, col);
    Location[] l = new Location[Hex.SIDES];
    int i = 0;
    for(Hex n : here.getNeighborsWithBlanks()){
      if(n instanceof Crystal || n instanceof Spark || 
          (n != null && (c == Color.NONE || here.colorLinked(n) == c || here.colorLinked(n) == Color.ANY))){
         l[i] = n.location;
       }
      else{
        l[i] = null;
      }
      i++;
    }
    return l;
  }

  
}
