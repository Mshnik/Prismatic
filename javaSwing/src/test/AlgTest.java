package test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import alg.*;

import game.Game;
import gui.GUI;

import util.*;
import models.*;

import org.junit.Test;

public class AlgTest {

  @SuppressWarnings("unused")
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
    System.out.println("Fix Closeness comparator in Location because of hex distance");
    
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
      
      LinkedList<Location> path = Creator.randomWalk(start, end, boardHeight, boardWidth);
      
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
      Creator.randomWalk(new Location(0,0), new Location(1,1), boardHeight, -1);
      fail("Did random walk with maxCol -1");
    }catch(IllegalArgumentException e){}
    try{
      Creator.randomWalk(new Location(0,0), new Location(1,1), -1, boardWidth);
      fail("Did random walk with maxRow -1");
    }catch(IllegalArgumentException e){}
    try{
      Creator.randomWalk(new Location(0,0), new Location(0,0), boardHeight, boardWidth);
      fail("Did random walk with equal start and end locations");
    }catch(IllegalArgumentException e){}
    try{
      Creator.randomWalk(new Location(-1,0), new Location(0,0), boardHeight, boardWidth);
      fail("Did random walk with an illegal start");
    }catch(IllegalArgumentException e){}
    try{
      Creator.randomWalk(new Location(0,0), new Location(-1,0), boardHeight, boardWidth);
      fail("Did random walk with an illegal end");
    }catch(IllegalArgumentException e){}
    try{
      Creator.randomWalk(new Location(boardHeight + 1,0), new Location(0,0), boardHeight, boardWidth);
      fail("Did random walk with an illegal start");
    }catch(IllegalArgumentException e){}
    try{
      Creator.randomWalk(new Location(0,0), new Location(0,boardWidth + 1), boardHeight, boardWidth);
      fail("Did random walk with an illegal start");
    }catch(IllegalArgumentException e){}
  }

}
