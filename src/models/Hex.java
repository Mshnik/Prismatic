package models;

import java.util.ArrayList;
import java.awt.Point;

/** Abstract parent of all tiles */
public abstract class Hex {

  public static final int SIDES = 6; //Sides per hex
  public static final Point[] NEIGHBOR_COORDINATES = {
   new Point(0,-1), new Point(1,0), new Point(1,1), new Point(0,1), new Point(-1, 1), new Point(-1, 0)
  }; //Location of neighbors in board, if they exist. These are actually vectors.
  
  
  public final Board board; //The board this Hex is on
  public final Point location;  //The location of this Hex in the board. 
                                //Note that this is a point, and is actually thus (x, y) -> (col, row).
  
  /** Stores Board b and Point p as board and location in this hex 
   * Throws illegalargumentexception if point p is already occupied on board b. */
  public Hex(Board b, Point p) throws IllegalArgumentException{
    if(b.getHex(p.y, p.x) != null) 
      throw new IllegalArgumentException("Board " + b + " already has hex at position " +
      		"(" + p.y + "," + p.x + "), can't construct new hex there.");
    board = b;
    location = p;
    board.setHex(this, p.y, p.x);
  }
  
  /** Returns the neighbors of this hex, clockwise from above.
   * Spots that this does not have a neighbor (off the board) are stored as null */
  public Hex[] getNeighborsWithBlanks(){
    int r = location.y;
    int c = location.x;
    Hex[] n = new Hex[SIDES];
    for(int i = 0; i < SIDES; i++){
      try{
        Point p = NEIGHBOR_COORDINATES[i];
        n[i] = board.getHex(r + p.y,c + p.x);
      } catch(ArrayIndexOutOfBoundsException e){
        n[i] = null;
      }
    }
    return n;
  }
  
  /** Returns the neighbors of this hex, clockwise from above, with nulls removed */
  public Hex[] getNeighbors(){
    Hex[] a = getNeighborsWithBlanks();
    ArrayList<Hex> temp = new ArrayList<Hex>();
    for(Hex h: a){
      if(h != null)
        temp.add(h);
    }
    return temp.toArray(new Hex[temp.size()]);
  }
  
}
