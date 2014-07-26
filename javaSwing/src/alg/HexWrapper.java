package alg;

import models.*;

/** Wraps a hex and some other information for alg purposes
 * (Like Dijkstra's and the like)
 * @author MPatashnik
 *
 */
public class HexWrapper {
  protected Hex hex;
  protected HexWrapper prev;
  protected int distance;
  
  /** Constructs a HexWrapper with the given inputs
   */
  public HexWrapper(Hex h, HexWrapper prev, int dist){
    hex = h;
    this.prev = prev;
    distance = dist;
  }
  
  /** Creates a default hexWrapper (null previous, Integer.MAX_VAL distance */
  public HexWrapper(Hex h){
    hex = h;
    prev = null;
    distance = Integer.MAX_VALUE;
  }
  
  /** Resets this' previous to null and distance to infinity */
  protected void reset(){
    prev = null;
    distance = Integer.MAX_VALUE;
  }
  
}
