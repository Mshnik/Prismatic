package test;

import static org.junit.Assert.*;

import alg.*;

import game.Game;
import gui.GUI;

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
  
  @Test
  public void testDFS() {
    //Show for debugging purposes - uncomment and step through rotations to see board.
    Board b = Board.makeBoard(5, 5, 3);
    SimpleGame game = new SimpleGame(null, null);
    game.setGUI(new GUI(game, false));
    b.setGame(game);
    game.setBoard(b);
    game.reset();
    
    System.out.println(Solver.dfs(b, b.getHex(0, 0).asSpark(), b.getHex(4, 4).asCrystal()));
    
  }

}
