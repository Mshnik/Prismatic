package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import models.Hex;

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
  /** Constructs a location corresponding to row r, col c. */
  public Location(int r, int c){
    row = r;
    col = c;
  }
  
  /** Returns true if this is out of bounds, that is has row/col < 0, row/col > boundsR, bounds C.
   * 
   */
  public boolean isOOB(int maxR, int maxC){
    return row < 0 || col < 0 || row > maxR || col > maxC;
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
      int d1 = (here.row - o1.row)^2 + (here.col - o1.col)^2;
      int d2 = (here.row - o2.row)^2 + (here.col - o2.col)^2;
      return d1 - d2;
    }
    
  }
}
