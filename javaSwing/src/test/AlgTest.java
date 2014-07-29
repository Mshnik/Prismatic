package test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import alg.*;

import game.Game;
import gui.GUI;

import util.*;
import models.*;

import org.junit.Before;
import org.junit.Test;

public class AlgTest {

  private Board anyBoard;
  private static final int boardHeight = 10;
  private static final int boardWidth = 10;

  
  @Before
  public void resetBoard(){
    anyBoard = Board.anyBoard(boardHeight, boardWidth);
  }
  
  private static class SimpleGame extends Game{
    private SimpleGame(Board b, GUI g){
      super(b,g);
    }
    
    @Override
    public void updateHex(Hex h) {
      if(gui != null){
        gui.repaint();
      }
    }

    @Override
    public void reset() {
      gui.retile();
    }

    @Override
    public int getDifficulty() {
      return Color.values().length -Colors.SPECIAL_OFFSET;
    }
  }
  
  //@Test
  public void testDFS() {
    //Show for debugging purposes - uncomment and step through rotations to see board.
    Board b = Board.makeBoard(5, 5, 3);
    SimpleGame game = new SimpleGame(null, null);
    GUI g = new GUI(game, false);
    game.setGUI(g);
    b.setGame(game);
    game.setBoard(b);
    game.reset();
    
    System.out.println(Solver.dfs(b, b.getHex(0, 0).asSpark(), b.getHex(4, 4).asCrystal()));
    System.out.println("Done");
    
  }
  
  @Test
  public void testRandomWalk(){    
    //Test random walks...
    // 1) have the start as the first location and end as the last location
    // 2) Are unique
    // 3) only contain valid neighbor paths
    // 4) throw correct errors
    int TESTS = 25;
    for(int i = 0; i < TESTS; i++){
      Location start = new Location((int)(Math.random() * boardHeight), (int)(Math.random() * boardWidth));
      Location end;
      do{
        end = new Location((int)(Math.random() * boardHeight), (int)(Math.random() * boardWidth));
      }while(end.equals(start));
      
      LinkedList<Location> path = anyBoard.randomWalk(start, end, Color.NONE);
      
      assertEquals("Front of path is start", path.getFirst(), start);
      assertEquals("Last of path is end", path.getLast(), end);
      for(Location l : path){
        assertTrue("Location " + l + " occurs once in path", path.indexOf(l) == path.lastIndexOf(l));
      }
      
      for(int j = 0; j < path.size() - 1; j++){
        Location here = path.get(j);
        Location there = path.get(j+1);
        assertTrue("Location " + here + " is adjacent to the next node " + there, here.isAdjacentTo(there));
      }
    }
    
    //Check correct errors
    try{
      anyBoard.randomWalk(new Location(0,0), new Location(0,0),  Color.NONE);
      fail("Did random walk with equal start and end locations");
    }catch(IllegalArgumentException e){}
    try{
      anyBoard.randomWalk(new Location(-1,0), new Location(0,0), Color.NONE);
      fail("Did random walk with an illegal start");
    }catch(IllegalArgumentException e){}
    try{
      anyBoard.randomWalk(new Location(0,0), new Location(-1,0), Color.NONE);
      fail("Did random walk with an illegal end");
    }catch(IllegalArgumentException e){}
    try{
      anyBoard.randomWalk(new Location(boardHeight + 1,0), new Location(0,0),  Color.NONE);
      fail("Did random walk with an illegal start");
    }catch(IllegalArgumentException e){}
    try{
      anyBoard.randomWalk(new Location(0,0), new Location(0,boardWidth + 1), Color.NONE);
      fail("Did random walk with an illegal start");
    }catch(IllegalArgumentException e){}
    try{
      anyBoard.randomWalk(new Location(0,0), new Location(0,5), null);
      fail("Did random walk with an illegal color");
    }catch(IllegalArgumentException e){}
  }
  
  @Test
  public void testShortestPath(){
    //Test random walks...
    // 1) have the start as the first location and end as the last location
    // 2) Are unique
    // 3) only contain valid neighbor paths
    // 4) throw correct errors
    int TESTS = 10;
    int boardHeight = 10;
    int boardWidth = 10;
    for(int i = 0; i < TESTS; i++){
      Location start = new Location((int)(Math.random() * boardHeight), (int)(Math.random() * boardWidth));
      Location end;
      do{
        end = new Location((int)(Math.random() * boardHeight), (int)(Math.random() * boardWidth));
      }while(end.equals(start));
      
      LinkedList<Location> path = anyBoard.shortestPath(start, end, Color.NONE);
      
      assertEquals("Front of path is start", path.getFirst(), start);
      assertEquals("Last of path is end", path.getLast(), end);
      for(Location l : path){
        assertTrue("Location " + l + " occurs once in path", path.indexOf(l) == path.lastIndexOf(l));
      }
      
      for(int j = 0; j < path.size() - 1; j++){
        Location here = path.get(j);
        Location there = path.get(j+1);
        assertTrue("Location " + here + " is adjacent to the next node " + there, here.isAdjacentTo(there));
      }
      
      //Test the shortest path is shorter than any random path - hopefully this will fail eventually if the shortest path alg is wrong
      for(int k = 0; k < TESTS; k++){
        LinkedList<Location> rand = anyBoard.randomWalk(start, end, Color.NONE);
        assertTrue(rand.size() >= path.size());
      }
    }
    
    //Check correct errors
    try{
      anyBoard.shortestPath(new Location(-1,0), new Location(0,0), Color.NONE);
      fail("Did shortest walk with an illegal start");
    }catch(IllegalArgumentException e){}
    try{
      anyBoard.shortestPath(new Location(0,0), new Location(-1,0), Color.NONE);
      fail("Did shortest walk with an illegal end");
    }catch(IllegalArgumentException e){}
    try{
      anyBoard.shortestPath(new Location(boardHeight + 1,0), new Location(0,0), Color.NONE);
      fail("Did shortest walk with an illegal start");
    }catch(IllegalArgumentException e){}
    try{
      anyBoard.shortestPath(new Location(0,0), new Location(0,boardWidth + 1), Color.NONE);
      fail("Did shortest walk with an illegal start");
    }catch(IllegalArgumentException e){}   
    try{
      anyBoard.shortestPath(new Location(0,0), new Location(0,5),  null);
      fail("Did shortest walk with an illegal color");
    }catch(IllegalArgumentException e){}
  }
  
  private void pathAndCheck(Color c, Location start, Location end, Board b, Game game){
    LinkedList<Location> path = b.shortestPath(start, end, c);
    Creator.addPath(c, b, path);
    if(game != null)
      game.reset();
    for(int i = 1; i < path.size() - 1; i++){
      assertTrue(b.getHex(path.get(i)).asPrism().colorCount(c) >= 2);
    }
  }
  
  @Test
  public void testAddPath(){
    SimpleGame game = null;
    //For displaying this test
    game = new SimpleGame(null, null);
    GUI g = new GUI(game, false);
    game.setGUI(g);
    anyBoard = Board.anyBoard(5, 9);
    anyBoard.setGame(game);
    game.setBoard(anyBoard);
    game.reset();
    
    // Test adding a path that crosses the board
    pathAndCheck(Color.RED, new Location(0,0), new Location(4,8), anyBoard, game);
    
    // Test adding a path that crosses that path of the same color (should work)
    pathAndCheck(Color.RED, new Location(0,8), new Location(4,0), anyBoard, game);
    
    // Test adding a path that crosses that path of the a different color (should still work - find a cross)
    pathAndCheck(Color.BLUE, new Location(0,7), new Location(3,0), anyBoard, game);
    
    // Add a path that completes cutting off the board
    pathAndCheck(Color.GREEN, new Location(0,4), new Location(4,4), anyBoard, game);
    
    //Ditto above
    pathAndCheck(Color.PINK, new Location(0,4), new Location(4,3), anyBoard, game);
    
    //Test a path can piggyback off a earlier path
    pathAndCheck(Color.BLUE, new Location(2,0), new Location(1,8), anyBoard, game);
    
    //Test an impossible path - end point is filled in with non-valid color.
    try{
      pathAndCheck(Color.ORANGE, new Location(0,0), new Location(2,4), anyBoard, game);
      fail("Created an impossible path and it passed checks. WTF?");
    }catch(RuntimeException e){}
    
    //Test a very roundabout path - make sure A* isn't broken.
    pathAndCheck(Color.YELLOW, new Location(0,3), new Location(0,4), anyBoard, game);
   
  }

}
