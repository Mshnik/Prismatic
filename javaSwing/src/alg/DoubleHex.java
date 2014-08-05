package alg;

import java.util.Objects;

import models.Color;
import models.Crystal;
import models.Hex;

/** Represents a relationship in which the lighter lights the lightee.
 * Signifies that this is possible, not that the board currently reflects this state.
 * 
 * Used for keys in opt matrix.
 * @author MPatashnik
 *
 */
public class DoubleHex {
  public final Hex lighter;
  public final Hex lightee;
  
  /** Constructs a double hex. In order to do this, it must be true -> lighter must currently be lighting lightee.
   * Only exception is the crystal key, with lightee crystal and lighter is null. */
  private DoubleHex(Hex lighter, Hex lightee) throws IllegalArgumentException{
    if(lightee instanceof Crystal && lighter == null){
      this.lighter = null;
      this.lightee = lightee;
      return;
    }
    if(lightee.lighter(lightee.colorLinked(lighter)) != lighter)
      throw new IllegalArgumentException("Can't construct doubleHex where " + lightee + " isn't lit by " + lighter);
    
    this.lighter = lighter;
    this.lightee = lightee;
  }
  
  /** Constructor for a double hex that creates based on the current board state */
  public static DoubleHex keyFor(Hex h, Color c){
    return new DoubleHex(h.lighter(c), h);
  }
  
  /** Constructor for the crystal key - the given crystal with no lighter */
  public static DoubleHex goalKey(Crystal c){
    return new DoubleHex(null, c);
  }
  
  /** Two doubleHexes are equal if they have the same lighter and lightee */
  @Override
  public boolean equals(Object o){
    if ( !(o instanceof DoubleHex))
      return false;
    DoubleHex d = (DoubleHex)o;
    return (lighter == null && d.lighter == null || lighter.equals(d.lighter)) && 
           (lightee == null && d.lightee == null || lightee.equals(d.lightee));
  }
  
  /** Hashes based on the two hexes */
  @Override
  public int hashCode(){
    return Objects.hash(lighter, lightee);
  }
  
  /** Returns a simple representation: lightee <- lighter */
  @Override
  public String toString(){
    return lightee + " <- " + lighter;
  }
}
