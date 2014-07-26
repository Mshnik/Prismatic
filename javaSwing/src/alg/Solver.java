package alg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.HashMap;

import models.*;

/** class hosting static methods for solving boards.
 * A solution is a hashmap of List(Color) (each color on board * # crystals) 
 *  -> list of moves to solve for that color combination.*/
public class Solver {
  
  public static HashMap<LinkedList<Color>, LinkedList<Move>> solve(Board board){  
    //Assemble board by type
    Collection<Hex> c = board.allHexesOfClass(Crystal.class);
    Collection<Crystal> crystals = new ArrayList<Crystal>(c.size());
    for(Hex h : c){
      crystals.add((Crystal)h);
    }
    
    Collection<Hex> s = board.allHexesOfClass(Spark.class);
    Collection<Spark> sparks = new ArrayList<Spark>(c.size());
    for(Hex h : s){
      sparks.add((Spark)h);
    }
    
//    Collection<Hex> p = board.allHexesOfClass(Prism.class);
//    Collection<Prism> prisms = new ArrayList<Prism>(p.size());
//    for(Hex h : p){
//      prisms.add((Prism)h);
//    }
    
    //Construct a graph to perform searches on. Definitely only do this once per board, pass around as necessary.
    HexWrapper[][] graph = new HexWrapper[board.getHeight()][board.getWidth()];
    for(Hex h : board.allHexes()){
      graph[h.location.row][h.location.col] = new HexWrapper(h);
    }
    
    HashMap<LinkedList<Color>, LinkedList<Move>> m = new HashMap<LinkedList<Color>, LinkedList<Move>>();
    for(int i = 1; i < crystals.size(); i++){
      m = solve(board, graph, i, crystals, sparks, m);
    }
    
    return m;
  }
  
  /** Solves Recursively. 
   *  In a single iteration, 
   * 
   * 
   */
  private static HashMap<LinkedList<Color>, LinkedList<Move>> 
    solve(Board board, HexWrapper[][] graph, int goalCount, Collection<Crystal> crystals, Collection<Spark> sparks, HashMap<LinkedList<Color>, LinkedList<Move>> m){
     
    
    
    
    
    return m;
  }
  
//  /** Constructs a hex graph from a board. Try to use sparingly */
//  private static HexWrapper[][] graphFromBoard(Board b){
//    HexWrapper[][] graph = new HexWrapper[b.getHeight()][b.getWidth()];
//    for(Hex h : b.allHexes()){
//      graph[h.location.row][h.location.col] = new HexWrapper(h);
//    }
//    return graph;
//  }
  
  /** Perform a dfs on the given board starting at the specified spark. 
   * Tries to reach the specified crystal using the spark's current color.
   * 
   * Returns the shortest list that does this. Ties broken arbitrarily.
   * 
   * Should leave the board in the same state it was in, though moves are made on it
   */
  public static LinkedList<Move> dfs(Board board, Spark spark, Crystal crystal) throws IllegalArgumentException{
    if(spark.board != board || crystal.board != board) 
      throw new IllegalArgumentException("Spark " + spark + " or Crystal " + crystal + " doesn't belong to board " + board);
    
    //Make sure power from other sparks doesn't interfere
    for(Hex h : board.allHexesOfClass(Spark.class)){
      h.asSpark().turn(false);
    }
    spark.turn(true);
    
    //Light for a starting configuration
    board.relight();
    
    HashMap<DoubleHex, Move> opt = new HashMap<DoubleHex, Move>();
    opt.put(DoubleHex.goalKey(crystal), Move.NO_MOVE);  //Store the starting info about the winning move (none known)
    dfsHelper(spark.getColor(), crystal, new Move(spark, true, null), spark, opt);
    
    //Extract the move that caused the crystal to be lit and was the best at doing so
    Move m = opt.get(DoubleHex.goalKey(crystal));
    
    //Compose the finalMove back into a list  by following backpointers. 
    //Don't put the dummy first move in the list.
    LinkedList<Move> list = new LinkedList<Move>();
    while(m != null && m.hex != spark){
      //If no move was encountered, no solution exists.
      if(m == Move.NO_MOVE){
        return null;
      }
      list.push(m);
      m = m.prev;
    }
    for(Move mv : list){
      System.out.println(mv.hex.location + " " + mv.direction);
    }
    return list;
  }
  
  private static final int sleeptime = 200;
  
  /** Helps dfs - returns the best move to get towards the goal.
   * @param c - the color to light
   * @param crystal - the goal to light
   * @param parent - the move that allowed this stack frame
   * @param here - the hex that is here. May not be equal to parent.hex if making the parent move lighted more than itself.
   * @param optimal - optimal results thus far. Memoizing to prevent recalculating the same results over and over.
   *                  hashes from lighter. Best winning move stored with the goalKey. 
   * @return either NO_MOVE if there is no set of moves that include this and its lighter set that solve the game,
   *         or the winning move that does so in the fewest moves.
   * */
  private static Move dfsHelper(Color c, Crystal crystal, Move parent, Hex here, HashMap<DoubleHex, Move> optimal){
    //Base case - map is already completed.
    //No move necessary -> return parent move as the completing move.
    if(crystal.lit() == c){
      optimal.put(DoubleHex.keyFor(parent.hex, c), parent);
      Move previousWinningMove = optimal.get(DoubleHex.goalKey(crystal));
      if(previousWinningMove != null && parent.index() < previousWinningMove.index()){
        optimal.put(DoubleHex.goalKey(crystal), parent);
      }
      return parent;
    }
    
    //Base case - parent's move count is already above a best solution. No point of continuing to recurse
    Move previousWinningMove = optimal.get(DoubleHex.goalKey(crystal));
    if(previousWinningMove != null && parent.index() > previousWinningMove.index()){
        optimal.put(DoubleHex.keyFor(here, c), Move.NO_MOVE);
        return Move.NO_MOVE;
    }
    
    //Recursive case - map not yet completed. 
    //Identify hex(s) that could receive power from parent's hex (that aren't in parent's lighter set -> endless recursion)
    //For each hex that could receive power, in every rotation, recurse.
    //All children are prisms, so the following can be done without worrying about cast. @see Move.children(c).
    Move bestMove = Move.NO_MOVE; //Store the best move as NO_MOVE initially. If no move ever replaces this, return this.
    boolean wasRotating = Prism.ROTATE_CLOCKWISE; //Store the direction prisms were rotating before changing it.
                                                  //Set to this before returning.
    Boolean[] rotationDirections = {true, false};
    for(Hex h : here.children(c)){
      
      //Determine if this child needs to be explored again
      Move bestMoveForChild = Move.NO_MOVE;
      Move previousBestMoveForChild = optimal.get(DoubleHex.keyFor(h, c));
      if(previousBestMoveForChild != null){
        bestMoveForChild = previousBestMoveForChild;
        
        //Going from the child's previous best is correct, but check if the path here was better than the childs.
        //Only apply patch when previousBestMoveForChild isn't NO_MOVE.
        //Go back along the previousBestMoveForChild's moves to find the move that comes before it
        if(previousBestMoveForChild != Move.NO_MOVE){
          Move m = previousBestMoveForChild;
          while(m != null){
            if(m.hex == h && m.prev.hex != h){
              if(parent.index() < m.prev.index()){
                m.prev = parent;
              }
              m = null;
            } else{
              m = m.prev;
            }
          }
        }
      }
      else{
        //Child needs to be explored.
        for(Boolean b : rotationDirections){
          Move p = parent;
          Prism.ROTATE_CLOCKWISE = b;
          //Try half of the rotations
          for(int i = 0; i < Hex.SIDES/2; i++){          
            if(h.lighterSet(c).contains(here) && c == here.colorLinked(h)){
              Move newMove = dfsHelper(c, crystal, p, h, optimal);
              if(newMove.index() < bestMoveForChild.index())
                bestMoveForChild = newMove;
            }
            h.asPrism().click();  //Rotate in the default direction
            try {
              Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            p = new Move(h, Prism.ROTATE_CLOCKWISE, p);
          }
          
          //Undo this half of the rotations
          for(int i = 0; i < Hex.SIDES/2; i++){
            h.asPrism().antiClick();
            try {
              Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
        optimal.put(DoubleHex.keyFor(h, c), bestMoveForChild);
      }
      if(bestMoveForChild.index() < bestMove.index())
        bestMove = bestMoveForChild;
    }
    
    Prism.ROTATE_CLOCKWISE = wasRotating;
    return bestMove;
  }
}
