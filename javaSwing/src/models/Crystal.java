package models;

import java.util.Collection;

import util.*;

public class Crystal extends Hex {
  
  private static final long serialVersionUID = -1502067965351067014L;

  private Color lit;  //Single color this is lit. Try to stay this color when recalculating light.
  
  /** Constructs a Crystal and puts it into board b
   * @param b - the board this spark belongs to
   * @param l - the location of this crystal in (row, col) in the board
   * @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB.
   */
  public Crystal(Board b, Location l) throws IllegalArgumentException{
    super(b, l);
    lit = Color.NONE;
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
  
  /** Constructs a copy of Crystal c for board b */
  public Crystal(Board b, Hex h) throws IllegalArgumentException{
    super(b, h);
    if(! (h instanceof Crystal)) throw new IllegalArgumentException("Can't clone " + h + " as Crystal");
  }

  /** Returns the single color this is lit */
  public Color lit(){
    return lit;
  }
  
  @Override
  /** Try to find light like a prism, but don't ever provide light. Thus only look for a provider, don't need to recurse.
   * Only find single light color. */
  protected void light() {
    boolean lighterChanged = pruneLighters();
    //First try to find a provider of the previous color of light
    if(lighterChanged){
      findLightProviders(lit);
      if(isLit().size() == 0){
        findLightProviders(Color.NONE);
      }
    }
    
    //Update the color this is lit
    if(isLit().isEmpty()){
      lit = Color.NONE;
    } else {
      lit = lighters.values().iterator().next();  //Get next (only) color
    }

    //Redraw and return the color this is now lit
    update();
  }
  
  @Override
  /** Helper method for use in findLight implementations. Tries to find light among neighbors.
   *  Overrides hex findLightProvider so that it can take any color of light, no matter the side color.
   *  Only looks for preferred. If preferred is NONE, takes any color. Only takes on e color */
  void findLightProviders(Color preferred){
    for(Hex h : getNeighbors()){
      Collection<Color> hLit = h.isLit();
      if(hLit.size() > 0 && (preferred == Color.NONE || hLit.contains(preferred)) && hLit.contains(h.colorOfSide(h.indexLink(this)))){ 
        lighters.put(h, h.colorOfSide(h.indexLink(this)));
        return;
      }
    }
  }

  @Override
  /** All sides of this crystal are the color of its lighter. (Not that this can provide light) */
  public Color colorOfSide(int n) throws IllegalArgumentException {
    if(n < 0 || n > SIDES - 1) throw new IllegalArgumentException("Can't find color of side " + n + " of " + this);
    return lit;
  }

  @Override
  /** Interacting with a Crystal does nothing - do nothing here */
  public void click() {}

}
