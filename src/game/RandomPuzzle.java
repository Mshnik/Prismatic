package game;

import util.Colors;
import gui.*;
import models.Board;
import models.ColorCircle;
import models.Crystal;
import models.Hex;
import models.Prism;
import models.Spark;

public class RandomPuzzle extends Game {

  private int difficulty;
  
  /** Constructs a new randompuzzle and shows on gui */
  public RandomPuzzle(int difficulty) {
    super(makeBoard(difficulty), null);
    this.difficulty = difficulty;
  }
  
  

  private static Board makeBoard(int difficulty){
    Board b = new Board(4,9);
    for(int r = 0; r < b.getHeight(); r++){
      for(int c = 0; c < b.getWidth(); c++){
        if(r == 0 && c == 0){
          new Spark(b, r, c, Colors.subValues(1, difficulty));
        } else if(r == 3 && c == 8){
          new Crystal(b, r, c);
        } else{
          new Prism(b, r, c, ColorCircle.randomArray(Hex.SIDES, difficulty));
        }
      }
    }
    b.relight();
    return b;
  }
  
  @Override
  public void updateHex(Hex h) {
      if(gui != null){
        gui.updateScoreLabel();
        gui.repaint();
      }
  }

  @Override
  public void reset() {
    board = makeBoard(difficulty);
    board.setGame(this);
    gui.retile();
    gui.updateScoreLabel();
  }
  
  @Override
  public int getDifficulty(){
    return difficulty;
  }
  
  /** Creates a sample gui and allows playing with it */
  public static void main(String[] args){
    Game g = new RandomPuzzle(3);
    GUI gui = new GUI(g);
    gui.retile();
  }

}
