package models;

import util.*;

public class Crystal extends Hex {
  
  /** Constructs a Crystal and puts it into board b
   * @param b - the board this spark belongs to
   * @param l - the location of this crystal in (row, col) in the board
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB.
   */
  public Crystal(Board b, Location l) throws IllegalArgumentException{
    super(b, l);
  }
  
  /** Constructs a Crystal and puts it into board b
   * @param b - the board this spark belongs to
   * @param row - the row of this crystal in board
   * @param col - the col of this crystal in board
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB.
   */
  public Crystal(Board b, int row, int col) throws IllegalArgumentException{
    this(b, new Location(row, col));
  }

  @Override
  /** Try to find light like a prism, but don't ever provide light. Thus only look for a provider, don't need to recurse. */
  protected Color findLight(boolean thisChanged) {
    Color wasLit = lit;
    
    //First try to find a provider of the previous color of light
    findLightProvider(wasLit);
    
    //If that didn't work, try to find any provider of light.
    if(lighter == null) findLightProvider(Color.NONE); 

    //Redraw and return the color this is now lit
    update();
    lit = isLit();
    return isLit();
  }
  
  @Override
  /** Helper method for use in findLight implementations. Tries to find light among neighbors.
   *  Overrides hex findLightProvider so that it can take any color of light, no matter the side color.
   *  Only looks for preferred. If preferred is NONE, takes any color. */
  void findLightProvider(Color preferred){
    lighter = null;
    for(Hex h : getNeighbors()){
      if(h.lit != Color.NONE && (preferred == Color.NONE || h.lit == preferred) && h.colorOfSide(h.indexLink(this)) == h.lit){ 
        lighter = h;
        return;
      }
    }
  }

  @Override
  /** All sides of this crystal are the color of its lighter. (Not that this can provide light) */
  public Color colorOfSide(int n) throws IllegalArgumentException {
    if(n < 0 || n > SIDES - 1) throw new IllegalArgumentException("Can't find color of side " + n + " of " + this);
    return isLit();
  }

  @Override
  /** Interacting with a Crystal does nothing - do nothing here */
  public void click() {}

}
