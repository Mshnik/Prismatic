package models;

import models.Board.Color;

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
    setColorCircle(colors);
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
  
  /** Allows setting the ColorCircle, but only if it isn't set yet (is null).
   * Also causes the prism to look for light
   * @throws IllegalArgumentException if the colorCircle is currently non-null
   */
  public void setColorCircle(Color[] colors) throws IllegalArgumentException{
    if(colorCircle != null) throw new IllegalArgumentException("Can't set colorCirle of " + this);
    if(colors != null && colors.length != SIDES) throw new IllegalArgumentException("Can't set color array of size " + colors.length);
    colorCircle = ColorCircle.fromArray(colors);
    findLight(true);
  }
  
  /** Rotates this prism once clockwise (moves head back). Also causes the prism to look for light and redraw itself */
  public void rotate(){
    colorCircle = colorCircle.prev;
    findLight(true);
  }
  
  /** Rotates this prism once counter clockwise (moves head forward). Also causes the prism to look for light and redraw itself */
  public void rotateCounter(){
    colorCircle = colorCircle.next;
    findLight(true);
  }
  
  @Override
  /** Returns the colorCircle at the correct index for the color of a given side */
  public Color colorOfSide(int n) throws IllegalArgumentException{
    if(n < 0 || n > SIDES - 1) throw new IllegalArgumentException ("Illegal Side Number " + n);
    return colorCircle.toArray()[n];
  }
  
  @Override
  /** Tries to find light by looking at all neighbor hexes that this isn't providing light to
   * Returns true if this is lit at the end of the procedure, false otherwise
   */
  protected boolean findLight(boolean thisChanged) {
    //Check if old light provider can still provide light after this rotated (changed)
    if(thisChanged && ! Hex.colorLinked(this, lighter)){
      // If not, unlight all hexes this was lighting before re-lighting anything.
      lighter = null;
      for(Hex h : getNeighbors()){
        h.findLight(false);
      }
    }

    boolean wasLit = isLit();
    boolean nowLit = false;
    for(Hex h : getNeighbors()){
      if(Hex.colorLinked(this, h) && h.lighter != this){
        lighter = h;
        nowLit = true;
        break;
      }
    }
    //If couldn't find light, set lighter to null
    if(!nowLit)
      lighter = null;
    
    //If lighting status changed because of this call, let neighbors know, though it wasn't their change
    if(nowLit != wasLit){
      for(Hex h : getNeighbors()){
        h.findLight(false);
      }
    }
    
    //Redraw this (happens post recursion)
    draw();
    return nowLit;
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
}
