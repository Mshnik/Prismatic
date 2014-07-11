package models;

import models.Board.Color;

import java.awt.Point;
import java.util.ArrayList;
import util.Util;

/** Represents a single prism */
public class Prism extends Hex{
  
  /** Constructs a Prism
   * @param b - the board this prism belongs to
   * @param l - the location of this prism (x, y) -> (col, row) in board
   */
  public Prism(Board b, Point l) throws IllegalArgumentException{
    super(b, l);
  }
  
  /** The gems of  this prism. Value of colorCircle is the gem on the top */
  private ColorCircle colorCircle;
  
  /** Returns the colors of this prism, clockwise from the current top */
  public Color[] colorArray(){
    return colorCircle.toArray();
  }
  
  /** Rotates this prism once clockwise (moves head back) */
  public void rotate(){
    colorCircle = colorCircle.prev;
  }
  
  /** Rotates this prism once couter clockwise (moves head forward) */
  public void rotateCounter(){
    colorCircle = colorCircle.next;
  }
  
  /** Represents a gem on the prism - one link.
   * Using this instead of built in linked list class because of rotation functionality.
   * @author MPatashnik
   *
   */
  private static class ColorCircle{
    private Color color;
    private ColorCircle prev;
    private ColorCircle next;
    
    /** Constructs a color circle with the given inputs */
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
      
      for(int i = 0; i < l; i++) { temp[i].prev = temp[Util.mod(i-1, l)]; temp[i].next = temp[Util.mod(i+1, l)]; }
      
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
    
  }
  

}
