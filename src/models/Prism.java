package models;


import java.util.Objects;

import util.*;

/** Represents a single prism */
public class Prism extends Hex{
  
  /** Defines default rotation behavior for all prisms - true for clockwise, false for couterclockwise */
  public static boolean ROTATE_CLOCKWISE = true;
  
  /** The gems of  this prism. Value of colorCircle is the gem on the top */
  private ColorCircle colorCircle;
  
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
  
  /** Returns the colors of this prism, clockwise from the current top */
  public Color[] colorArray(){
    return colorCircle.toArray();
  }
  
  /** Allows setting the ColorCircle, but only if it isn't set yet (is null).
   * @throws IllegalArgumentException if the colorCircle is currently non-null
   */
  public void setColorCircle(Color[] colors) throws IllegalArgumentException{
    if(colorCircle != null) throw new IllegalArgumentException("Can't set colorCirle of " + this);
    if(colors != null && colors.length != SIDES) throw new IllegalArgumentException("Can't set color array of size " + colors.length);
    colorCircle = ColorCircle.fromArray(colors);
  }
  
  /** Rotates this prism once clockwise (moves head back). Also causes the prism to look for light and redraw itself */
  public void rotate(){
    board.moves++;
    colorCircle = colorCircle.getPrevious();
    findLight(true);
  }
  
  /** Rotates this prism once counter clockwise (moves head forward). Also causes the prism to look for light and redraw itself */
  public void rotateCounter(){
    board.moves++;
    colorCircle = colorCircle.getNext();
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
   * Tries to stay the same color of light if multiple are avaliable. Otherwise chooses arbitrarily.
   * Returns the color this is lit at the end of the procedure, false otherwise
   */
  protected Color findLight(boolean thisChanged) {
    Color wasLit = lit;
    //If this was providing light, stop providing light (RECURSION)
    if(lit != Color.NONE){
      lit = Color.NONE;
      stopProvidingLight();
    }
    
    //First try to find a provider of the previous color of light
    findLightProvider(wasLit);
    
    //If that didn't work, try to find any provider of light.
    if(lighter == null) findLightProvider(Color.NONE); 
    
    //Try to give light to neighbors (RECURSION)
    if(isLit() != Color.NONE){
      lit = isLit();
      provideLight();
    }

    //Redraw (post recursion) and return the color this is now lit
    draw();
    return isLit();
  }
  
  @Override
  /** Default behavior for a Prism is to rotate. Rotates clockwise if ROTATE_CLOCKWISE, rotates counterclockwise otherwise. */
  public void click(){
    if(ROTATE_CLOCKWISE) rotate();
    else rotateCounter();
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
