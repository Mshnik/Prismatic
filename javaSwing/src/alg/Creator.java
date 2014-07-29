package alg;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

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
    
    public CreatedGame(Board b, HashMap<Integer, LinkedList<Solution>> solutions){
      this.board = b;
      this.solutions = solutions;
    }
  }
  
  /** Creates a puzzle with the given number of colors, crystals, sparks */
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
    
    fuzzify(game, Colors.subValues(colors));
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
    CreatedGame game = new CreatedGame(b, new HashMap<Integer, LinkedList<Solution>>());
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
  
  //TODO
  /** Fills in the rest of the board. Should do this without adding to possible solutions.
   * Fix later to make sure it doesn't add solutions - just doing it randomly for now */
  private static void fuzzify(CreatedGame game, Color[] availableColors){
    for(Hex prism : game.board.allHexesOfClass(Prism.class)){
      Color[] colorArr = prism.asPrism().colorArray();
      for(int i = 0; i < colorArr.length; i++){
        if(colorArr[i] == Color.ANY){
          colorArr[i] = availableColors[(int)(Math.random() * availableColors.length)];
        }
      }
      prism.asPrism().setColorCircle(colorArr);
    }
  }
  
  //TODO
  /** Scrambles the board. Later fix to count the screw-upishness to important tiles, to keep track of optimal solution */
  public static void scramble(CreatedGame game){
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
