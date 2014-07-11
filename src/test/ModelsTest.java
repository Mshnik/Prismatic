package test;

import static org.junit.Assert.*;

import models.*;
import models.Board.Color;
import models.Prism.ColorCircle;

import org.junit.Test;

import util.*;

public class ModelsTest {

  @Test
  public void testContstants() {
    //Good god I hope these never get changed... just in case
    for(Location[] arr : Hex.NEIGHBOR_COORDINATES){
      assertEquals(Hex.SIDES, arr.length);
    }
    assertTrue(Board.DEFAULT_BOARD_SIZE > 0);
  }
  
  @Test
  public void testBoardConstruction(){
    //Test that a board can be populated with hexes correctly
    
    Board b = new Board(2,2);
    Hex one = new Prism(b, 0, 0, null);
    Hex two = new Prism(b, 0, 1, null);
    Hex three = new Prism(b, 1, 0, null);
    Hex four = new Prism(b, 1, 1, null);
    
    assertEquals(b.getHex(0, 0), one);
    assertEquals(b.getHex(0, 1), two);
    assertEquals(b.getHex(1, 0), three);
    assertEquals(b.getHex(1, 1), four);
    
    //Check that errors are thrown at the correct time.
    try{
      new Prism(null, 0, 0, null);
      fail("Successfully constructed prism into null board");
    }catch(IllegalArgumentException e){}
    try{
      new Prism(b, -1, 0, null);
      fail("Successfully constructed prism into Illegal Position");
    }catch(IllegalArgumentException e){}
    try{
      new Prism(b, 0, 0, null);
      fail("Successfully constructed prism into already occupied Position");
    }catch(IllegalArgumentException e){}
    
    //Check preventing illegal board construction
    try{
      new Board(-1, 0);
      fail("Successfully constructed negative length board");
    } catch(IllegalArgumentException e){}
  }
  
  @Test
  public void testHexNeighbors(){
    Board b = new Board(4,4);
    for(int i = 0; i < 4; i++){
      for(int j = 0; j < 4; j++){
        new Prism(b, i,j,null);
      }
    }
    
    //Check odd column neighbors
    Hex[] n = b.getHex(1, 1).getNeighbors();
    assertEquals(6, n.length);
    Location[] l = new Location[n.length];
    for(int i = 0; i < n.length; i++){
      l[i] = n[i].location;
    }
    
    assertEquals(l[0], new Location(0,1));
    assertEquals(l[1], new Location(1,2));
    assertEquals(l[2], new Location(2,2));
    assertEquals(l[3], new Location(2,1));
    assertEquals(l[4], new Location(2,0));
    assertEquals(l[5], new Location(1,0));
    
    //Check even column neighbors
    n = b.getHex(1, 2).getNeighbors();
    assertEquals(6, n.length);
    l = new Location[n.length];
    for(int i = 0; i < n.length; i++){
      l[i] = n[i].location;
    }
    
    assertEquals(l[0], new Location(0,2));
    assertEquals(l[1], new Location(0,3));
    assertEquals(l[2], new Location(1,3));
    assertEquals(l[3], new Location(2,2));
    assertEquals(l[4], new Location(1,1));
    assertEquals(l[5], new Location(0,1));
    
    //Check neighbors when bordering edge, omitting blanks
    n = b.getHex(1,0).getNeighbors();
    assertEquals(4, n.length);
    l = new Location[n.length];
    for(int i = 0; i < n.length; i++){
      l[i] = n[i].location;
    }
    
    assertEquals(l[0], new Location(0,0));
    assertEquals(l[1], new Location(0,1));
    assertEquals(l[2], new Location(1,1));
    assertEquals(l[3], new Location(2,0));
    
    //Check neighbors when bordering edge, not omitting blanks
    n = b.getHex(0,0).getNeighborsWithBlanks();
    assertEquals(6, n.length);
    l = new Location[n.length];
    for(int i = 0; i < n.length; i++){
      if(n[i] != null) l[i] = n[i].location;
      else l[i] = null;
    }
    
    assertEquals(l[0], null);
    assertEquals(l[1], null);
    assertEquals(l[2], new Location(0,1));
    assertEquals(l[3], new Location(1,0));
    assertEquals(l[4], null);
    assertEquals(l[5], null);
    
    //Check two neighbor functions give same output when all should not be null.
    n = b.getHex(2,1).getNeighbors();
    assertEquals(6, n.length);
    Hex[] n2 = b.getHex(2,1).getNeighborsWithBlanks();
    assertEquals(6, n2.length);
    
    Hex[][] n3 = {n, n2};
    for(int i = 0; i < 6; i++){
      assertEquals(n3[0][i], n3[1][i]);
    }
  }
  
  @Test
  public void testColorCircles(){
    //Test creating a colorCircle of length 1
    Color[] c = {Color.RED};
    ColorCircle circle = ColorCircle.fromArray(c);
    assertEquals(Color.RED, circle.getColor());
    assertEquals(circle, circle.getNext());
    assertEquals(circle, circle.getPrevious());
    
    //Test creating a colorCircle of length > 1
    Color[] c2 = {Color.RED, Color.BLUE, Color.GREEN};
    ColorCircle circle2 = ColorCircle.fromArray(c2);
    assertEquals(Color.RED, circle2.getColor());
    assertEquals(Color.BLUE, circle2.getNext().getColor());
    assertEquals(Color.GREEN, circle2.getPrevious().getColor());
    assertEquals(circle2, circle2.getNext().getNext().getNext());
    assertEquals(circle2, circle2.getPrevious().getPrevious().getPrevious());
    
    //Check that invalid inputs return null
    assertEquals(null, ColorCircle.fromArray(null));
    assertEquals(null, ColorCircle.fromArray(new Color[0]));
    
     //Test to array function.
    Color [] c3 = ColorCircle.fromArray(Color.values()).toArray();
    assertEquals(Color.values().length, c3.length);
    for(int i = 0; i < c3.length; i++){
      assertEquals(Color.values()[i], c3[i]);
    } 
    
    //Test equality and hashcodes
    ColorCircle circle3 = ColorCircle.fromArray(c2);
    assertTrue(circle3.equals(circle2));
    assertTrue(circle2.equals(circle3));
    assertTrue(circle3.equals(circle3));
    assertEquals(circle2.hashCode(), circle3.hashCode());
    
    assertFalse(circle.equals(circle2));
    assertFalse(circle3.equals(circle));
    
    //Check size inequality
    Color[] c4 = {Color.RED, Color.RED};
    ColorCircle circle4 = ColorCircle.fromArray(c4);
    assertFalse(circle.equals(circle4));
    assertFalse(circle4.equals(circle));
    
    //Check non-size inequality
    Color[] c5 = Color.values(); 
    c5[4] = Color.NONE;
    ColorCircle circle5 = ColorCircle.fromArray(c5);
    assertFalse(circle3.equals(circle5));
    assertFalse(circle5.equals(circle3));
  }
  
  @Test
  public void testPrismConstructionAndRotation(){
    Board fakeBoard = new Board();
    //Check that color array must be of length 6 to be in a prism constructor or setter.
    try{
      Color[] c = {Color.RED};
      new Prism(fakeBoard, 0, 0, c);
      fail("Constructed prism with bad color array length - too short");
    }catch(IllegalArgumentException e){};
    try{
      Color[] c = Color.values();
      new Prism(fakeBoard, 0, 0, c);
      fail("Constructed prism with bad color array length - too long");
    }catch(IllegalArgumentException e){};
    try{
      Color[] c = {Color.RED};
      Prism p = new Prism(fakeBoard, 0, 0, c);
      p.setColorCircle(c);
      fail("Set prism with bad color array length - too short");
    }catch(IllegalArgumentException e){};
    try{
      Color[] c = Color.values();
      Prism p = new Prism(fakeBoard, 1, 1, null);
      p.setColorCircle(c);
      fail("Set prism with bad color array length - too long");
    }catch(IllegalArgumentException e){};
    
    Board b = new Board(1,1);
    Color[] c = {Color.BLUE, Color.RED, Color.CYAN, Color.ORANGE, Color.YELLOW, Color.PINK};
    Prism p = new Prism(b, 0,0,c);
    
    //Test getting array and individual positions
    for(int i = 0; i < c.length; i++){
      assertEquals(c[i], p.colorArray()[i]);
      assertEquals(c[i], p.colorAt(i));
    }
    
    //Test forward rotation
    p.rotate();
    Color[] cRotated = {Color.PINK, Color.BLUE, Color.RED, Color.CYAN, Color.ORANGE, Color.YELLOW};
    for(int i = 0; i < cRotated.length; i++){
      assertEquals(cRotated[i], p.colorArray()[i]);
      assertEquals(cRotated[i], p.colorAt(i));
    }
    
    //Test backward rotation - color array back to original
    p.rotateCounter();
    //Test getting array and individual positions
    for(int i = 0; i < c.length; i++){
      assertEquals(c[i], p.colorArray()[i]);
      assertEquals(c[i], p.colorAt(i));
    }
  }

}
