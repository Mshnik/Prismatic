cpackage models;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import util.*;

public class Spark extends Hex {

  private static final long serialVersionUID = -4488651998928115173L;
  private ColorCircle avaliableColors; //The colors this spark can take on, 
                                       //with avaliableColor.color as the currently selected one
  
  
  /** Constructs a Spark and puts it into board b
   * @param b - the board this spark belongs to
   * @param l - the location of this spark in (row, col) in the board
   * @param colors - the colors of this spark, in clockwise order starting with the top. Can be null, then set later.
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length == 0.
   */
  public Spark(Board b, Location l, Color[] colors) throws IllegalArgumentException{
    super(b, l);
    if(colors == null || colors.length == 0) throw new IllegalArgumentException("Can't set color array of size " );
    setAvaliableColors(colors);
  }
  
  /** Constructs a Spark and puts it into board b
   * @param b - the board this spark belongs to
   * @param row - the row of this spark in board
   * @param col - the col of this spark in board
   * @param colors - the colors of this spark, in clockwise order starting with the top. Can be null, then set later.
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length == 0.
   */
  public Spark(Board b, int row, int col, Color[] colors) throws IllegalArgumentException{
    this(b, new Location(row, col), colors);
  }
  
  /** Constructs a copy of Spark h for board b */
  public Spark(Board b, Hex h) throws IllegalArgumentException{
    super(b, h);
    if(! (h instanceof Spark)) throw new IllegalArgumentException("Can't clone " + h + " as Spark");
    Spark s = (Spark) h;
    setAvaliableColors(s.getAvaliableColors());
  }
  
  /** Returns the current color of this Spark */
  public Color getColor(){
    return avaliableColors.getColor();
  }
  
  /** Makes this spark use the next avaliable color, and redraw */
  public void useNextColor(){
    avaliableColors = avaliableColors.getNext();
    light();
    update();
  }
  
  /** Returns the avaliable colors of this spark */
  public Color[] getAvaliableColors(){
    return avaliableColors.toArray();
  }
  
  /** Allows setting the ColorCircle, but only if it isn't set yet (is null).
   * @throws IllegalArgumentException if the colorCircle is currently non-null
   */
  public void setAvaliableColors(Color[] colors) throws IllegalArgumentException{
    if(avaliableColors != null) throw new IllegalArgumentException("Can't set colorCirle of " + this);
    if(colors != null && colors.length == 0) throw new IllegalArgumentException("Can't set color array of size " + colors.length);
    avaliableColors = ColorCircle.fromArray(colors);
    light();
    update();
  }
  
  @Override
  /** Sparks always find light because they always light themselves. No setting of fields neccesary */
  protected void light() {
    stopProvidingLight();
    provideLight();
  }
  
  @Override
  /** Default behavior for a spark is to switch to the next avaliable color */
  public void click(){
    useNextColor();
  }
  
  @Override
  /** Overrides Hex isLit() because Sparks are always lit */
  public Collection<Color> isLit(){
    LinkedList<Color> l = new LinkedList<Color>();
    l.add(getColor());
    return l;
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
    return super.equals(p);
  }
  
  /** Hashes a Spark based on their locations, boards, and avaliableColors */
  public int hashCode(){
    return Objects.hash(board, location);
  }



}
