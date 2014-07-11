package models;

import models.Board.Color;

import java.util.ArrayList;
import java.util.Objects;

import util.*;

/** Represents a single prism */
public class Prism extends Hex{
  
  /** Constructs a Prism and puts it into board b
   * @param b - the board this prism belongs to
   * @param l - the location of this prism in (row, col) in the board
   * @param colors - the colors of this prism, in clockwise order starting with the top. Can be null, then set later.
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length != SIDES.
   */
  public Prism(Board b, Location l, Color[] colors) throws IllegalArgumentException{
    super(b, l);
    if(colors != null && colors.length != SIDES) throw new IllegalArgumentException("Can't set color array of size " + colors.length);
    colorCircle = ColorCircle.fromArray(colors);
  }
  
  /** Constructs a Prism and puts it into board b
   * @param b - the board this prism belongs to
   * @param row - the row of this prism in board
   * @param col - the col of this prism in board
   * @param colors - the colors of this prism, in clockwise order starting with the top. Can be null, then set later.
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length != 6.
   */
  public Prism(Board b, int row, int col, Color[] colors) throws IllegalArgumentException{
    this(b, new Location(row, col), colors);
  }
  
  /** The gems of  this prism. Value of colorCircle is the gem on the top */
  private ColorCircle colorCircle;
  
  /** Returns the colors of this prism, clockwise from the current top */
  public Color[] colorArray(){
    return colorCircle.toArray();
  }
  
  /** Returns the color at position, where 0 is the top, going clockwise.
   * @throws ArrayIndexOutOfBoundsException
   */
  public Color colorAt(int position) throws ArrayIndexOutOfBoundsException{
    return colorCircle.toArray()[position];
  }
  
  /** Allows setting the ColorCircle, but only if it isn't set yet (is null).
   * @throws IllegalArgumentException if the colorcircle is currently non-null
   */
  public void setColorCircle(Color[] colors) throws IllegalArgumentException{
    if(colorCircle != null) throw new IllegalArgumentException("Can't set colorCirle of " + this);
    if(colors != null && colors.length != SIDES) throw new IllegalArgumentException("Can't set color array of size " + colors.length);
    colorCircle = ColorCircle.fromArray(colors);
  }
  
  /** Rotates this prism once clockwise (moves head back) */
  public void rotate(){
    colorCircle = colorCircle.prev;
  }
  
  /** Rotates this prism once couter clockwise (moves head forward) */
  public void rotateCounter(){
    colorCircle = colorCircle.next;
  }
  
  /** Two prisms are equal if they are equal as hexes and if their two colorCircles are equal */
  public boolean equals(Object o){
    if (! (o instanceof Prism)) return false;
    Prism p = (Prism)o;
    return super.equals(p) && (colorCircle == null && p.colorCircle == null || colorCircle.equals(p.colorCircle));
  }
  
  /** Hashes a prism based on their locations, boards, and colorCircles */
  public int hashCode(){
    return Objects.hash(board, location, colorCircle);
  }
  
  /** Represents a gem on the prism - one link.
   * Using this instead of built in linked list class because of rotation functionality.
   * @author MPatashnik
   *
   */
  public static class ColorCircle{
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
  

}
