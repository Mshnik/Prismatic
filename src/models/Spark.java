package models;

import java.util.Objects;

import models.Board.Color;
import util.*;

public class Spark extends Hex {

  private ColorCircle avaliableColors; //The colors this spark can take on, 
                                       //with avaliableColor.color as the currently selected one
  
  
  /** Constructs a Spark and puts it into board b
   * @param b - the board this spark belongs to
   * @param l - the location of this spark in (row, col) in the board
   * @param colors - the colors of this spark, in clockwise order starting with the top. Can be null, then set later.
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length != SIDES.
   */
  public Spark(Board b, Location l, Color[] colors) throws IllegalArgumentException{
    super(b, l);
    if(colors != null && colors.length != SIDES) throw new IllegalArgumentException("Can't set color array of size " + colors.length);
    avaliableColors = ColorCircle.fromArray(colors);
    draw();
  }
  
  /** Constructs a Spark and puts it into board b
   * @param b - the board this spark belongs to
   * @param row - the row of this spark in board
   * @param col - the col of this spark in board
   * @param colors - the colors of this spark, in clockwise order starting with the top. Can be null, then set later.
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length != 6.
   */
  public Spark(Board b, int row, int col, Color[] colors) throws IllegalArgumentException{
    this(b, new Location(row, col), colors);
  }
  
  /** Returns the current color of this Spark */
  public Color getColor(){
    return avaliableColors.getColor();
  }
  
  /** Makes this spark use the next avaliable color, and redraw */
  public void useNextColor(){
    avaliableColors = avaliableColors.getNext();
    for(Hex h : getNeighbors()){
      h.findLight(false);
    }
    draw();
  }
  
  /** Returns the avaliable colors of this spark */
  public Color[] getAvaliableColors(){
    return avaliableColors.toArray();
  }
  
  @Override
  /** Sparks always find light because they always light themselves. No setting of fields neccesary */
  protected boolean findLight(boolean thischanged) {
    return true;
  }
  
  @Override
  /** Overrides Hex isLit() because Sparks are always lit */
  public boolean isLit(){
    return true;
  }
  
  @Override
  /** Returns the currently selected color as the color for all sides */
  public Color colorOfSide(int n) throws IllegalArgumentException{
    if(n < 0 || n > SIDES - 1) throw new IllegalArgumentException ("Illegal Side Number " + n);
    return getColor();
  }
  
  /** Two sparks are equal if they are equal as hexes and if their two avaliableColors are equal */
  public boolean equals(Object o){
    if (! (o instanceof Spark)) return false;
    Spark p = (Spark)o;
    return super.equals(p) && (avaliableColors == null && p.avaliableColors == null || avaliableColors.equals(p.avaliableColors));
  }
  
  /** Hashes a Spark based on their locations, boards, and avaliableColors */
  public int hashCode(){
    return Objects.hash(board, location, avaliableColors);
  }



}
