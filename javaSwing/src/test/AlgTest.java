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
      return Color.values().length -1;
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
  
  @Test
  public void testAddPath(){
    //For displaying this test
//    SimpleGame game = new SimpleGame(null, null);
//    GUI g = new GUI(game, false);
//    game.setGUI(g);
//    anyBoard.setGame(game);
//    game.setBoard(anyBoard);
//    game.reset();
    
    // Test adding a path that crosses the board
    LinkedList<Location> path1 = anyBoard.shortestPath(new Location(0,0), new Location(8,8), Color.RED);
    Creator.addPath(Color.RED, anyBoard, path1);
//    game.reset();
    
    //Check that every hex in the middle of that path has at least two red sides
    for(int i = 1; i < path1.size() - 1; i++){
      assertTrue(anyBoard.getHex(path1.get(i)).asPrism().colorCount(Color.RED) >= 2);
    }
    
    // Test adding a path that crosses that path of the same color (should work)
    LinkedList<Location> path2 = anyBoard.shortestPath(new Location(0,8), new Location(8,0), Color.RED);
    Creator.addPath(Color.RED, anyBoard, path2);
//    game.reset();
    
    for(int i = 1; i < path2.size() - 1; i++){
      assertTrue(anyBoard.getHex(path2.get(i)).asPrism().colorCount(Color.RED) >= 2);
    }
    
  }

}
