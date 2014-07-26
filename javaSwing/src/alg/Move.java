package alg;

import models.*;

/** A move is a storage of a move on a board. It is a wrapper for a hex and a direction (clockwise, counterclockwise)
 * Has a previous move (the move before this). 
 * Comparable based on its index - smaller index is better. */
public class Move {
  
  /** Represents a null move */
  public static final Move NO_MOVE = new Move(null, false, null);

  public final Hex hex;
  public final boolean direction;    //true for clockwise, false for counter
  protected Move prev;            //The previous move
  
  /** Creates a move with the given hex and direction
   * @param h - hex for this move
   * @param direc - true for clockwise, false for counter.
   * @param prev - the previous move. Null if this is the first move
   */
  public Move(Hex h, boolean direc, Move pre){
    hex = h;
    direction = direc;
    prev = pre;
  }
  
  /** Counts the number of moves before this to get this' index in a list of moves.
   * If this is NO_MOVE, returns max val (this is never the right move to pick) */
  public int index(){
    if(this == NO_MOVE)
      return Integer.MAX_VALUE;
    if (prev == null)
      return 0;
    return 1 + prev.index();
  }
  
  @Override
  /** Shows this as a string of its previous moves */
  public String toString(){
    if(this == NO_MOVE)
      return "NO MOVE";
    if(this.hex instanceof Spark)
      return "";
    String s = hex.location.toString() + "-";
    if(direction)
      s += "Cl ; ";
    else
      s += "CCl ; ";
    if(prev != null)
      s += prev.toString();
    return s;
  }
  
}
