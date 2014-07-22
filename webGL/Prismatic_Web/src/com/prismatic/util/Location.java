package com.prismatic.util;

import java.io.Serializable;
import java.util.Objects;

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
  
  /** Simple string representation of a Location in (row, col) form */
  @Override
  public String toString(){
    return "(" + row + "," + col + ")";
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
}
