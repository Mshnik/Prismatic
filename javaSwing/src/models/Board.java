package models;

/** All things hex representation: www.redblobgames.com/grids/hexagons/ */
/** Board is implemented using the odd-q layout on the above page */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.json.*;

import game.Game;

import util.*;
import util.Location.DistanceComparator;

/** Represents the board of hexagonal tiles.
 * Upon construction is empty - to fill with hexes, construct hexes with this as an argument. */
public class Board implements JSONString{
  /** The board of hexes for this board. Stored in "odd-q" layout to
   * make storing a hexagonal grid possible in a 2-D matrix.
   * For illustration, see 
   */
  private Hex[][] board;
  
  /** Collections of all hexes in this board by their class.
   * Calculated and added to lazily. If an entry is not present, specifies that it needs to be recalculated
   */
  private HashMap<Class<? extends Hex>, Collection<Hex>> allHexesByClass;
  
  /** The game this board belongs to */
  private Game game;
  
  /** The number of moves made on this board. A move is a prism rotation. */
  protected int moves = 0;
  
  /** A string representing the puzzle for this board */
  public String puzzle = "";
  
  /** Returns the number of moves on this board */
  public int getMoves(){
    return moves;
  }
  
  /** Returns the width of the board */
  public int getWidth(){
    return board[0].length;
  }
  
  /** returns the height of the board */
  public int getHeight(){
    return board.length;
  }
  
  /** Returns the game this board belongs to */
  public Game getGame(){
    return game;
  }
  
  /** Sets the game this board belongs to. Throws a runtime exception if the game has already been set */
  public void setGame(Game g) throws RuntimeException{
    if(game != null) throw new RuntimeException("Can't set Game of " + this + " to " + g + " because it is already " + game);
    game = g;
  }
  
  /** Signifies that this board is no longer used - sets the game and board this belongs to to null */
  public void dispose(){
    game = null;
    board = null;
  }
  
  /** Resets this's move count */
  public void resetMoveCount(){
    moves = 0;
  }
  
  /** Returns the index (0 ... SIDES - 1) of the side of h1 that is facing h2. Returns -1 if the two are not neighbors or h==null */
  public int indexLinked(Hex h1, Hex h2){
    if(h1 == null || h2 == null) return -1;
    Hex[] h1Neighbors = h1.getNeighborsWithBlanks();
    for(int i = 0; i < Hex.SIDES; i++){
      if(h2 == h1Neighbors[i]){
        return i;
      }
    }
    //h2 not a neighbor of h1
    return -1;
  }
  
  /** Returns the color that links h1 and h2:
   *    1) The two hexes are neighbors (both non-null), otherwise returns none
   *    2) The colors of the adjacent sides are the same. Treats Color.any as a wild card.
   *    If one is color.any and the other isn't, returns the more specific one.
   */
  public Color colorLinked(Hex h1, Hex h2){
    int index = indexLinked(h1, h2);
    if(index == -1)
      return Color.NONE;
    Color c1 = h1.colorOfSide(index);
    Color c2 = h2.colorOfSide(Util.mod(index + Hex.SIDES/2, Hex.SIDES));
    if(c1 == Color.ANY && c2 == Color.ANY)
      return Color.ANY;
    if(c1 == Color.ANY)
      return c2;
    if(c2 == Color.ANY)
      return c1;
    if(c1 == c2)
      return c1;
    else
      return Color.NONE;
  }
  
  /** @See getHex(row, col) */
  public Hex getHex(Location l) throws ArrayIndexOutOfBoundsException{
    return getHex(l.row, l.col);
  }
  
  /** Returns the hext at position (r,c) */
  public Hex getHex(int r, int c) throws ArrayIndexOutOfBoundsException{
    return board[r][c];
  }
  
  /** Returns all colors on this board.
   * Does this by looking at all sparks and finding the union of their colors */
  public Set<Color> allColors(){
    HashSet<Color> s = new HashSet<Color>();
    for(Hex h : allHexes()){
      if(h instanceof Spark){
        for(Color c : h.asSpark().getAvaliableColors()){
          s.add(c);
        }
      }
    }
    return s;
  }
  
  /** Returns all hexes in no particular order - useful for collection purposes. */
  public Collection<Hex> allHexes(){
    ArrayList<Hex> a = new ArrayList<Hex>();
    for(Collection<Hex> c : allHexesByClass.values()){
      a.addAll(c);
    }
    return a;
  }
  
  /** Returns all hexes in no particular order */
  public Collection<Hex> allHexesOfClass(Class<? extends Hex> t){
    return allHexesByClass.get(t);
  }
  
  /** Returns the set of colors that are present on this map. Only includes regular colors */
  public Set<Color> colorsPresent(){
    HashSet<Color> s = new HashSet<Color>();
    for(Hex h : allHexesOfClass(Spark.class)){
      for(Color c : h.asSpark().getAvaliableColors()){
        if(Colors.isRegularColor(c))
          s.add(c);
      }
    }
    return s;
  }
  
  /** Returns a hashmap of color -> int, that is the count of the sides of prisms that have that color on this board */
  public HashMap<Color, Integer> colorCount(){
    HashMap<Color, Integer> m = new HashMap<Color, Integer>();
    for(Hex h : allHexesOfClass(Prism.class)){
      for(Color c : Color.values()){
        if(! m.containsKey(c)){
          m.put(c, h.asPrism().colorCount(c));
        }
        else{
          m.put(c, m.get(c) + h.asPrism().colorCount(c));
        }
      }
    }
    return m;
  }
  
  /** Returns all prisms that have at least one side with Color.ANY on it. Useful for finding a part of the board not yet finished */
  public Collection<Prism> allPrismsWithAny(){
    Collection<Hex> prisms = allHexesOfClass(Prism.class);
    Collection<Prism> p = new LinkedList<Prism>();
    for(Hex h : prisms){
      if(h.asPrism().colorCount(Color.ANY) >= 1)
        p.add(h.asPrism());
    }
    return p;
  }
  
  /** Sets the hex at position (r,c). Also sets all neighbor hexes as needing a neighbor update.
   * Hex must have this as its board - otherwise throws illegalargexception
   * Used in hex construction, not much elsewhere. */
  protected void setHex(Hex h, int r, int c) throws IllegalArgumentException, ArrayIndexOutOfBoundsException{
    if(h.board != this) throw new IllegalArgumentException("Can't put hex belonging to " + h.board + " in board " + this);
    
    board[r][c] = h; //ArrayIndexOutOfBounds may be thrown here
    Collection<Hex> collec = allHexesByClass.get(h.getClass());  //Add h to the correct collection in allHexesByClass.
    if(collec == null){
      collec = new LinkedList<Hex>();
      allHexesByClass.put(h.getClass(), collec);
    }
    collec.add(h);
      
    
    //For each neighbor of this hex, tell it that it's neighbors have changed.
    for(Location l : h.neighbors){
      try{
        Hex hNeighbor = getHex(l);
        if(hNeighbor != null) hNeighbor.neighborsUpdated = true;
      } catch(ArrayIndexOutOfBoundsException e){}
    }
  }
  
  /** Re-calcualtes light on whole board. Doesn't change whether or not a hex can light. */
  public void relight(){
    for(Hex h : allHexes()){
      h.lighters.clear(); // Remove all lighters from board
    }
    for(Hex h : allHexes()){
      if(h instanceof Spark){
        h.light();    //Make sparks relight. By recursion relights the board.
      }
    }
  }
  
  /** Constructor for an empty board of size rs*cs */
  public Board(int rs, int cs) throws IllegalArgumentException {
    if (rs < 0 || cs < 0) throw new IllegalArgumentException("Illegal Board Construction for Dimensions " + rs + ", " + cs);
    board = new Hex[rs][cs];
    allHexesByClass = new HashMap<Class<? extends Hex>, Collection<Hex>>();
  }
  
  /** Default size for constructing a default (square) board */
  public static final int DEFAULT_BOARD_SIZE = 10;
  
  /** Constructor for an empty default board, of sidze default_board_size ^2 */
  public Board(){
    this(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE);
  }
  
  /** Constructor for a board that mirrors board b, but isn't linked to a game and has 0 moves on it */
  public Board(Board b){
    this(b.getHeight(), b.getWidth());
    for(Hex h : b.allHexes()){
      if(h instanceof Prism){
        new Prism(this, h);
      }
      else if(h instanceof Spark){
        new Spark(this, h);
      }
      else if(h instanceof Crystal){
        new Crystal(this, h);
      }
    }
  }
  
  /** Makes a random board using difficulty colors. Puts sparks/crystals at opposing corners */
  public static Board makeBoard(int rs, int cs, int difficulty){
    Board b = new Board(rs,cs);
    for(int r = 0; r < b.getHeight(); r++){
      for(int c = 0; c < b.getWidth(); c++){
        if(r == 0 && c == 0 || r == b.getHeight() - 1 && c == 0){
          new Spark(b, r, c, Colors.subValues(difficulty));
        } else if(r == b.getHeight()-1 && c == b.getWidth()-1 || r == 0 && c == b.getWidth()-1){
          new Crystal(b, r, c);
        } else{
          new Prism(b, r, c, ColorCircle.randomArray(Hex.SIDES, difficulty));
        }
      }
    }
    b.relight();
    return b;
  }
  
  /** Makes a board filled entirely with any prisms - good for testing path algorithms / starting level editor stuff */
  public static Board anyBoard(int rs, int cs){
    Board b = new Board(rs, cs);
    for(int i = 0; i < rs; i++){
      for(int j = 0; j < cs; j++){
        new Prism(b, new Location(i,j), Colors.fill(Hex.SIDES, Color.ANY));
      }
    }
    return b;
  }
  
  @Override
  /** Two boards are equivalent if the refer to the same board (not equal boards */
  public boolean equals(Object o){
    if (! (o instanceof Board)) return false;
    Board b = (Board)o;
    return b.board == board;
  }
  
  @Override
  /** Hashes a Board based on its board matrix */
  public int hashCode(){
    return board.hashCode();
  }
  
  /** Returns this board as a json object, fully representing the most basic part of the board - the hexes with locations and colors. */
  @Override
  public String toJSONString() {
    String s = "{";
    for(Hex h : allHexes()){
      s += "\n" + Util.addQ(h.location.toString()) + ":" + h.toJSONString() + ",";
    }
    s += "\n" + Util.addQ("Height") + ":" + getHeight() + ",";
    s += "\n" + Util.addQ("Width") + ":" + getWidth() + ",";
    s += "\n" + Util.addQ("Puzzle") + ":" + Util.addQ(puzzle);     //Lack of comma here is very important - not valid JSON otherwise.
    return s + "\n}";
  }
  
  
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Algorithm related things for location
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  
  /** Creates a random-ish path from start to end (return[0] = start, return[last] = end),
   * that stays within [0..maxRow)[0..maxCol).
   * Assumes Hex.NEIGHBOR_COORDINATES for adjacent nodes.
   * end must be reachable by some path from start of the method will never terminate.
   * 
   * Slight probabilistic weighting towards moving towards the exit, to improve running time.
   * 
   * @param start - the start location of the random walk
   * @param end   - the end location of the random walk
   * @param c -  the color of links of the generated walk must be. If color.NONE, no color checking.
   *                If anything but Color.NONE, checks if this even has a path of the given color.
   * @return - a random path from (including) start to end. Will not have repeated nodes - a simple path.
   * @throws IllegalArgumentException if maxRow < 0 or maxCol < 0 or start == end
   */
  public LinkedList<Location> randomWalk(Location start, Location end, Color c) throws RuntimeException{
    int maxRow = getHeight();
    int maxCol = getWidth();
    if(maxRow < 0 || maxCol < 0) throw new IllegalArgumentException("Can't do random walk with maxRow " + maxRow + ", maxCol " + maxCol);
    if(start.equals(end)) throw new IllegalArgumentException("Can't do random walk where start == end (" + start + ")");
    if(start.row < 0 || start.row >= maxRow || start.col < 0 || start.col >= maxCol)
      throw new IllegalArgumentException("Start location " + start + " out of bounds");
    if(end.row < 0 || end.row >= maxRow || end.col < 0 || end.col >= maxCol)
      throw new IllegalArgumentException("End location " + end + " out of bounds");
    if(c == null || c == Color.ANY)
       throw new IllegalArgumentException("Don't know how to find a randomWalk using color " + c  + ". If you want no color check, use Color.NONE");
    
    //Runs the shortest path to check if such a path is even possible. If not, throws runtimeexception
    try{
      LinkedList<Location> path = shortestPath(start, end, c);
      if(path.size() <= 2) //If path is trivial, just return it - no point of making random path.
        return path;
    } catch(RuntimeException e){
      throw e;
    }
    
    LinkedList<Location> l = new LinkedList<Location>();
    
    Location here = start;
    do{
      //Determine if we have been here before. If so, remove newly formed cycle
      int i = l.indexOf(here);
      if(i != -1){
        int len = l.size();
        for(int j = i; j < len; j++){
          l.remove(i);      //Repeatedly removing the same index (which is filled by later and later nodes)
                            //Will ultimately remove i ... len-1 (rest of the list).
        }
      }
      //Add new here to path
      l.add(here);
      Hex hereHex = getHex(here.row, here.col);
      
      //Pick a random neighbor
      Location neighbor;
      do{
        //Picks a random direction, weighting choosing a direction towards the end.
        int r = (int)(Math.pow(Math.random(),1.0) * Hex.SIDES) ;  //EVEN WEIGHT. towards picking an element earlier in the array. 
                                                                  //Increase the power to increase the weight.
        neighbor = end.orderByCloseness(here.neighbors(), maxRow, maxCol)[r];
        try{
          Hex nH = getHex(neighbor.row, neighbor.col);
          Color link = hereHex.colorLinked(nH);
          if(nH instanceof Spark || (nH instanceof Crystal && (! neighbor.equals(end)))){
            neighbor = Location.NOWHERE;  //Not a valid color link - redo random.
          }
          else if(! (nH instanceof Crystal) && c != Color.NONE &&  link != c && link != Color.ANY)
            neighbor = Location.NOWHERE;  //Not a valid color link - redo random.
        } catch(ArrayIndexOutOfBoundsException e){} //Exception handled by looping again.
      }while(neighbor.row >= maxRow || neighbor.col >= maxCol || neighbor.row < 0 || neighbor.col < 0);
      
      //Go there, set here to there.
      here = new Location(neighbor.row, neighbor.col);
    }while(! (here.equals(end)));
    l.add(end);
    return l;
  }
  
  /** Finds the shortest path from start to end. Uses A*. Woo!
   * @param start - the start location of the shortest path 
   * @param end   - the end location of the shortest path
   * @param c -  the color of links of the generated path must be. If color.NONE, no color checking.
   *  @return the shortest path from start to end. return[first] = start, return[last] = end. If start == end, returns a list of length 1.
   **/
  public LinkedList<Location> shortestPath(Location start, Location end, Color c) throws RuntimeException{ // 
    int maxRow = getHeight();
    int maxCol = getWidth();
    if(maxRow < 0 || maxCol < 0) throw new IllegalArgumentException("Can't do random walk with maxRow " + maxRow + ", maxCol " + maxCol);
    if(start.row < 0 || start.row > maxRow || start.col < 0 || start.col > maxCol)
      throw new IllegalArgumentException("Start location " + start + " out of bounds");
    if(end.row < 0 || end.row > maxRow || end.col < 0 || end.col > maxCol)
      throw new IllegalArgumentException("End location " + end + " out of bounds");
    if(c == null || c == Color.ANY)
      throw new IllegalArgumentException("Don't know how to find a randomWalk using color " + c  + ". If you want no color check, use Color.NONE");
    
    //Initialize
    for(Hex h : allHexes()){
      h.location.reset();
    }
    start.dist = 0;
    DistanceComparator dstComp = new DistanceComparator(end);
    LinkedList<Location> frontier = new LinkedList<Location>();
    frontier.add(start);
    
    //Iterate.
    //Terminating conditions - next closest node is end, frontier is empty
    while(! frontier.isEmpty() && ! frontier.peek().equals(end)){
      //Identify next closest frontier node to explore
      Location here = frontier.poll();
      Hex hereHex = getHex(here.row, here.col);
      //For every neighbor, if that neighbor exists and has a higher distance than here + 1 (constant visit cost)
      for(Location n : here.neighborsInGraph(this, c)){
        if(n != null){
          Hex nH = getHex(n.row, n.col);
          Color link = hereHex.colorLinked(nH);
          //Make sure crystals and sparks don't sneak in unless they're end points - spark should never be added except at start.
          if( nH instanceof Prism || (nH instanceof Crystal && n.equals(end))){
            if(nH instanceof Crystal || (c == Color.NONE ||  link == c || link == Color.ANY) && n.dist > here.dist + 1){
              n.prev = here;
              n.dist = here.dist + 1;
              if(! frontier.contains(n))
                frontier.add(n);
            }
          }
        }
      }
      //Move closest node to front. This is O(n), as opposed O(n log n ) of fully sorting.
      Location closest = Collections.min(frontier, dstComp);
      frontier.remove(closest);
      frontier.push(closest);
    }
    
    //If frontier is empty, no path exists
    if(frontier.isEmpty()){
      throw new RuntimeException("No path exists from " + start + " to " + end);
    }
    
    //Assemble path by following backpointers
    LinkedList<Location> path = new LinkedList<Location>();
    Location p = frontier.peek();
    while(p != null){
      path.push(p);
      p = p.prev;
    }
    
    return path;
  }
  
}
