package alg;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import models.*;
import util.*;

/** storage for static methods relating to creating puzzles. */
public class Creator {
  
  /** Represents a single solution - the color of the path, the path starting with spark ending with crystal */
  public static class Solution{
    private Color color;
    private LinkedList<Location> path;
    
    private Solution(Color c, LinkedList<Location> p){
      color = c;
      path = p;
    }
    
    public Color getColor(){
      return color;
    }
    
    public LinkedList<Location> getPath(){
      return path;
    }
  }
  
  /** Wraps a game that has been created by this -- has knowledge of its solutions. */
  public static class CreatedGame{
    public final Board board;
    public final HashMap<Integer, LinkedList<Solution>> solutions;   //Hash of color array to list of solutions, one per color.
    
    public final HashMap<Hex, Color[]> lockedTiles;   //Hexes and their orientations that are guaranteed to be in the solution.
    
    public CreatedGame(Board b, HashMap<Integer, LinkedList<Solution>> solutions, HashMap<Hex, Color[]> lockedTiles){
      this.board = b;
      b.lockedTiles = lockedTiles;
      this.solutions = solutions;
      this.lockedTiles = lockedTiles;
    }
  }
  
  /** Creates a single all full column puzzle */
  public static CreatedGame fullColPuzzle(int colors, int boardHeight, int boardWidth){
    CreatedGame game = prepGame(boardHeight, boardWidth, boardHeight, boardHeight, colors);
    Board b = game.board;
    LinkedList<Hex> availableSparks = (LinkedList<Hex>) b.allHexesOfClass(Spark.class);
    LinkedList<Hex> availableCrystals = (LinkedList<Hex>) b.allHexesOfClass(Crystal.class);
    
    //Put the sparks each on a random color, but try to minimize duplicates - hence the +1
    int r = (int)(Math.random() * (colors-1)) + 1;
    for(int i = 0; i < availableSparks.size(); i++){
      for(int z = 0; z < (i * r) % colors; z++){
        availableSparks.get(i).asSpark().useNextColor();
      }
    }
    
    //Pair the sparks with crystals by shuffling the crystals, then matching by index from here on out
    Collections.shuffle(availableCrystals);
    
    LinkedList<Solution> solutions = new LinkedList<Solution>();
    
    int p = 0;
    while(p < availableSparks.size()){
      //For this spark-crystal pair, create a random walk of the spark's color. Paint it and add it to the board
      Spark spark = availableSparks.get(p).asSpark();
      Crystal crystal = availableCrystals.get(p).asCrystal();
      Color color = spark.getColor();
      try{
        Solution s = new Solution(color, b.randomWalk(spark.location, crystal.location, color));
        addPath(s.color, b, s.path);
        solutions.add(s);
        p++;  //Puzzle went well, go to next one
      } catch(RuntimeException e){
        //Couldn't make a path of this color, try the same spark-crystal with a new color
        spark.useNextColor();
      } 
    }
    
    //Assemble the colors that were used in the end
    Color[] usedColors = new Color[boardHeight];
    for(int i = 0; i < availableSparks.size(); i++){
      usedColors[i] = availableSparks.get(i).asSpark().getColor();
    }
    
    game.solutions.put(Colors.hashArray(usedColors), solutions);
    lock(game, Math.max(game.board.getHeight(), game.board.getWidth() - 2));
    scramble(game);
    branch(game, 3);
    fuzzify(game);
    scramble(game);
    standardize(game);
    
    return game;
  }
  
  /** Creates a puzzle with the given number of colors, crystals, sparks.
   * Still too random for the time being - needs to be spruced up before it is useful */
  public static CreatedGame createPuzzle(int colors, int crystals, int sparks, int boardHeight, int boardWidth, int puzzles){
    CreatedGame game = prepGame(boardHeight, boardWidth, sparks, crystals, colors);
    Board b = game.board;
    
    int p = 0;
    LinkedList<Hex> availableSparks = (LinkedList<Hex>) b.allHexesOfClass(Spark.class);
    LinkedList<Hex> availableCrystals = (LinkedList<Hex>) b.allHexesOfClass(Crystal.class);

    while(p < puzzles){
      //Number of paths in this puzzle - weight towards lower number. In range [1 .. sparks]. Cap at the number of crystals.
      int r = Math.min((int)(Math.pow(Math.random(), 1.0) * sparks-1) + 1, crystals); 
      //Pick a random color from each spark.
      Color[] colorsForPuzzle = new Color[r];
      int s = 0;
      for(Hex spark : availableSparks) {
        Color[] avaliableColors = spark.asSpark().getAvaliableColors();
        int r2 = (int) (Math.random() * new Double(avaliableColors.length));
        colorsForPuzzle[s] = avaliableColors[r2];
        s++;
        if(s == r) break;
      }
      
      LinkedList<Solution> solutions = new LinkedList<Solution>();
      
      //Shuffle crystals so the spark-crystal pairings are random
      Collections.shuffle(availableCrystals);
      boolean pathsOk = true;
      for(int z = 0; z < r; z++){
        try{
          while(availableSparks.get(z).asSpark().getColor() != colorsForPuzzle[z])
            availableSparks.get(z).asSpark().useNextColor();
          
          solutions.add(new Solution(colorsForPuzzle[z], b.randomWalk(availableSparks.get(z).location, availableCrystals.get(z).location, colorsForPuzzle[z])));
        } catch(RuntimeException e){
          pathsOk = false;
          break;
        }
      }
      
      if(pathsOk && (! game.solutions.keySet().contains(Colors.hashArray(colorsForPuzzle)))){
        for(Solution sol : solutions){
          addPath(sol.color, b, sol.path);
        }
        game.solutions.put(Colors.hashArray(colorsForPuzzle), solutions);
        p++;
      }
    }
    
    fuzzify(game);
    scramble(game);
    
    return game;
  }
  
  /** Changes the prisms along the given path such that the whole path is linked by color c.
   * throws a runtime exception if this would change a color that isn't color.any. */
  public static void addPath(Color c, Board board, LinkedList<Location> path) throws RuntimeException{
    if(path.size() <= 1)
      return; //No changes to make on length 1, 0 path.
    //Alter prisms along the way to make this path all the given color.
    int i = 0;
    Hex here = board.getHex(path.get(0));
    Hex after = board.getHex(path.get(1));
    while(i < path.size()){
      //Link here to before and after, as necessary
      //If here isn't a prism (perhaps a spark in the middle), skip it.
      if(here instanceof Prism && after != null){
          fixHex(here, after, c);
      }
      if(after instanceof Prism && here != null){
        fixHex(after, here, c);
      }
      //Move references down
      i++;
      here = after;
      if(i + 1 < path.size())
        after = board.getHex(path.get(i + 1));
      else
        after = null;
    }
  }
  
  /** Preps a craetedGame for creating puzzles */
  private static CreatedGame prepGame(int boardHeight, int boardWidth, int sparks, int crystals, int colors){
    Board b = new Board(boardHeight, boardWidth);
    CreatedGame game = new CreatedGame(b, new HashMap<Integer, LinkedList<Solution>>(), new HashMap<Hex, Color[]>());
    //Place sparks and crystals on sides of board.
    int i = 0;
    while(i < crystals){
      try{
        new Crystal(b, new Location(i, boardWidth - 1));
      }catch(IllegalArgumentException e){
        i--;  //Retry this iteration
      } finally{
        i++;
      }
    }
    Color[] availableColors = Colors.subValues(colors);
    i = 0;
    while(i < sparks){
      try{
        new Spark(b, new Location((boardHeight - 1) - i, 0), availableColors);
      }catch(IllegalArgumentException e){
        i--;  //Retry this iteration
      } finally{
        i++;
      }
    }
    
    //Fill rest of board with wild card prisms - any on all sides
    for(int r = 0; r < boardHeight; r++){
      for(int c = 0; c < boardWidth; c++){
        try{
          new Prism(b, new Location(r,c), Colors.fill(Hex.SIDES, Color.ANY));
        } catch(IllegalArgumentException e) {}
      }
    }
    return game;
  }
  
  /** Records the orientation of the given number of hexes in game
   * 
   */
  private static void lock(CreatedGame game, int count){ 
    LinkedList<Hex> prisms = (LinkedList<Hex>)game.board.allHexesOfClass(Prism.class);
    Set<Location> importantHexes = new HashSet<Location>();
    for(LinkedList<Solution> sol : game.solutions.values()){
      for (Solution s : sol){
        importantHexes.addAll(s.path);
      }
    }
    while(count > 0){
      //Pick a random prism that hasn't been picked yet but is important.
      Prism p = null;
      while(p == null || game.lockedTiles.containsKey(p) || !importantHexes.contains(p.location)){
        p = prisms.get((int)(Math.random() * prisms.size())).asPrism();
      }
      game.lockedTiles.put(p, p.colorArray());
      count--;
    }
  }
  
  /** Fills in board by making new random paths. Tries to do this in a way that doesn't change the solution set
   * Does this recursively. Adds one branch, calls self 
   * Keep going until lives hits 0*/
  private static void branch(CreatedGame game, int lives){
    //Base case - 0 lives remaining.
    if(lives <= 0)
      return;
    
    LinkedList<Prism> remainingPrisms = (LinkedList<Prism>)game.board.allPrismsWithAny();  //Remaining prisms
    
    //Pick a random color by statistics - pick the least represented one
    Color c = null;
    int min = Integer.MAX_VALUE;
    for(Entry<Color, Integer> e : game.board.colorCount().entrySet()){
      if(e.getValue() < min && e.getValue() > 0 && Colors.isRegularColor(e.getKey())){
        min = e.getValue();
        c = e.getKey();
      }
    }
    
    //Pick two random prisms that have an any remaining
    Prism p1 = remainingPrisms.get((int)(Math.random() * remainingPrisms.size()));
    Prism p2 = remainingPrisms.get((int)(Math.random() * remainingPrisms.size()));
    
    try{
      LinkedList<Location> walk = game.board.randomWalk(p1.location, p2.location, c);
      addPath(c, game.board, walk);
    } catch(RuntimeException e){
      lives--;
    } finally{
      branch(game, lives);
    }
  }
  
  //TODO
  /** Fills in the rest of the board. Should do this without adding to possible solutions.
   * Fix later to make sure it doesn't add solutions - just doing it randomly for now 
   * Tries to balance tiles when possible, avoid making single colortiles.*/
  private static void fuzzify(CreatedGame game){
    Set<Color> colors = game.board.colorsPresent();
    for(Hex prism : game.board.allHexesOfClass(Prism.class)){
      int[] count = new int[colors.size()];
      int col = 0;
      for(Color c : colors){
        count[col] = prism.asPrism().colorCount(c);
        col++;
      }
      Color[] colorArr = prism.asPrism().colorArray();
      for(int i = 0; i < colorArr.length; i++){
        if(colorArr[i] == Color.ANY){
          //Find min
          int min = Integer.MAX_VALUE;
          int k = -1;
          int j = 0;
          Color c = null;
          for(Color co : colors){
            if(count[j] < min){
              min = count[j];
              k = j;
              c = co;
            }
            j++;
          }
          colorArr[i] = c;
          count[k]++;
        }
      }
      prism.asPrism().setColorCircle(colorArr);
    }
  }
  
  /** Standardizes the game for displaying - sets all sparks to the same color */
  private static void standardize(CreatedGame game){
    Color c = game.board.allColors().iterator().next(); //Aribtrarily picks an initial color
    for(Hex h : game.board.allHexesOfClass(Spark.class)){
      Spark s = h.asSpark();
      while(s.getColor() != c)
        s.useNextColor();
    }
    game.board.relight();
  }
  
  //TODO
  /** Scrambles the board. Later fix to count the screw-upishness to important tiles, to keep track of optimal solution */
  private static void scramble(CreatedGame game){
    for(Hex prism : game.board.allHexesOfClass(Prism.class)){
      int r = (int)(Math.random() * Hex.SIDES);
      for(int i = 0; i< r; i++){
        prism.asPrism().rotate();
      }
    }
    game.board.resetMoveCount();
  }
  
  /** Helper for addPath Changes here to have color c on the side facing there 
   * @throws RuntimeException if that side of here is already a color other than c or Color.ANY */
  private static void fixHex(Hex here, Hex there, Color c) throws RuntimeException{
    Color[] colorArr = here.asPrism().colorArray();
    int s = here.indexLinked(there);
    if(colorArr[s] != c && colorArr[s] != Color.ANY)
      throw new RuntimeException("Can't Set side " + s + " of hex " + here + " to " + c + " because it is already " + colorArr[s]);
    colorArr[s] = c;
    here.asPrism().setColorCircle(colorArr);
  }
  
}
