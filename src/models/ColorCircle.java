package models;

import java.io.Serializable;
import java.util.ArrayList;
import util.Util;

/** Represents the color of one side of a hex - one link.
 * Using this instead of built in linked list class because of rotation functionality.
 * @author MPatashnik
 *
 */
public class ColorCircle implements Serializable{

  private static final long serialVersionUID = 6519613127863567763L;
  private Color color;
  private ColorCircle prev;
  private ColorCircle next;
  private int size;  //The total size of the circle this ColorCircle is a link in.
  
  /** Constructs a color circle with the given inputs. Should only be used by helper constructing functions */
  private ColorCircle(Color c, ColorCircle p, ColorCircle n){
    color = c;
    prev = p;
    next = n;
  }
  
  /** Creates a circularly linked list of colorCircles from an array of colors.
   * Returns the first colorCircle (head of the circle)
   * Returns null if the input is null or length 0.
   */
  public static ColorCircle fromArray(Color[] colors){
    if (colors == null || colors.length == 0) return null;
    int l = colors.length;
    ColorCircle[] temp = new ColorCircle[l];
    for(int i = 0; i < l; i++) temp[i] = new ColorCircle(colors[i], null, null);
    
    for(int i = 0; i < l; i++) {
      temp[i].size = l; 
      temp[i].prev = temp[Util.mod(i-1, l)]; 
      temp[i].next = temp[Util.mod(i+1, l)]; 
    }
    
    return temp[0];
  }
  
  /** Returns a random color array of a given length. Uses at most maxColors (or 6 if maxColors > 6) colors, never uses NONE.
   * throws IllegalArgumentException if length <= 0 or maxColors <= 0*/
  public static Color[] randomArray(int length, int maxColors) throws IllegalArgumentException{
    if (length <= 0) throw new IllegalArgumentException("Can't make Color Array of length " + length + " for color circle");
    if(maxColors <= 0) throw new IllegalArgumentException("Can't make Color Array of length using at most " + maxColors + " colors");
    Color[] colors = new Color[length];
    for(int i = 0; i < length; i++){
      colors[i] = Color.values()[1 + (int)(Math.random() * (Math.min(maxColors, Color.values().length - 1)))];
    }
    return colors;
  }
  
  /** Creates a random color circle of the given length. Uses at most maxColors (or 6 if maxColors > 6) colors, never uses NONE.
   * throws IllegalArgumentException if length <= 0 or maxColors <= 0*/
   public static ColorCircle random(int length, int maxColors) throws IllegalArgumentException{
     return fromArray(randomArray(length, maxColors));
   }
  
  /** Returns the color of this link of the circle */
  public Color getColor(){
    return color;
  }
  
  /** Returns the next link in this circle (moves clockwise) */
  public ColorCircle getNext(){
    return next;
  }
  
  /** Returns the previous link in this circle (moves counterclockwise) */
  public ColorCircle getPrevious(){
    return prev;
  }
  
  /** Returns the total size of the circle this ColorCirle is a link in */
  public int getSize(){
    return size;
  }
  
  /** Converts this colorCircle (And its companions) into a color array, with this one at index 0. */
  public Color[] toArray(){
    ArrayList<Color> temp = new ArrayList<Color>();
    ColorCircle d = this;
    do{
      temp.add(d.color);
      d = d.next;
    }while(d != this);
    return temp.toArray(new Color[temp.size()]);
  }
  
  /** Returns a string representation of the color of this link in the colorCircle */
  @Override
  public String toString(){
    return color.toString().toLowerCase();
  }
  
  /** Two color circles are equal if their sizes are equal and, for every colorCircle in the chain, the colors are equal */
  @Override
  public boolean equals(Object o){
    if (! (o instanceof ColorCircle)) return false;
    ColorCircle c = (ColorCircle)o;
    ColorCircle d = this;
    if(size != c.size) return false;
    do{
      if(c.color != d.color) return false;
      c = c.next;
      d = d.next;
    }while(d != this);
    return true;
  }
  
  /** Hashes a ColorCircle based on its colors, starting with the head.
   * No collisions so long as the number of colors is <= 8 - (4 bits per color).
   */
  @Override
  public int hashCode(){
    int i = 0;
    int sum = 0;
    ColorCircle c = this;
    do{
      sum += c.color.hashCode() << i;
      i += 4;
      c = c.next;
    }while(c != this);
    return sum;
  }
  
}