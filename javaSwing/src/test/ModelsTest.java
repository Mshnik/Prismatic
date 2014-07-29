package test;

import static org.junit.Assert.*;

import game.Game;
import gui.GUI;

import java.util.Collection;
import java.util.HashSet;

import models.*;
import models.Hex.SimpleHex;

import org.junit.Test;

import util.*;

public class ModelsTest {

  /** A direct subclass of Hex with minimum additional functionality (only as required)
   * Used to allow testing of the Hex class - not to be used outside of testing
   * @author MPatashnik
   *
   */
  
  /** A direct subclass of Game with minimum additional functionality (only as required)
   * Used to allow testing of the Game class - not to be used outside of testing
   * @author MPatashnik
   *
   */
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
      return Color.values().length -Colors.SPECIAL_OFFSET;
    }
  }
  
  
  @Test
  public void testContstants() {
    //Good god I hope these never get changed... just in case
    for(Location[] arr : Hex.NEIGHBOR_COORDINATES){
      assertEquals(Hex.SIDES, arr.length);
    }
    assertTrue(Board.DEFAULT_BOARD_SIZE > 0);
  }
  
  @Test
  public void testLocations(){
    //Test conversion to and from cube coordinates.
    Location l1 = new Location(3,1);
    int[] cube1 = l1.cubeCoordinates();
    assertEquals(1, cube1[0]);    //x
    assertEquals(3, cube1[2]);    //z
    assertEquals(-4, cube1[1]);   //y
    
    //Test conversion back
    Location l2 = Location.fromCubeCoordinates(l1.cubeCoordinates());
    assertEquals(l1.row, l2.row);
    assertEquals(l1.col, l2.col);
    
    Location l3 = new Location(5, 7);
    int cube3[] = l3.cubeCoordinates();
    assertEquals(7, cube3[0]);
    assertEquals(2, cube3[2]);
    assertEquals(-9, cube3[1]);
    
    Location l4 = Location.fromCubeCoordinates(l3.cubeCoordinates());
    assertEquals(l3.row, l4.row);
    assertEquals(l3.col, l4.col);
    
    //Try a buncha random locations, check the sum of the cube coordinates is always 0 (invariant)
    int TESTS = 15;
    for(int i = 0; i < TESTS; i++){
      Location l = new Location((int)(Math.random() * i), (int)(Math.random() * i));
      int[] c = l.cubeCoordinates();
      assertEquals(0, c[0] + c[1] + c[2]);
    }
    
    //Test neighbors for odd column
    Location l5 = new Location(2,1);
    Location[] n5 = l5.neighbors();
    for(int i = 0; i < Hex.SIDES; i++){
      assertTrue(l5.isAdjacentTo(n5[i]));
      Location vec = Location.vec(l5, n5[i]);
      assertEquals(Hex.NEIGHBOR_COORDINATES[1][i], vec);
    }
    
    //Test neighbors for even column
    Location l6 = new Location(2,2);
    Location[] n6 = l6.neighbors();
    for(int i = 0; i < Hex.SIDES; i++){
      assertTrue(l6.isAdjacentTo(n6[i]));
      Location vec = Location.vec(l6, n6[i]);
      assertEquals(Hex.NEIGHBOR_COORDINATES[0][i], vec);
    }
    
    
  }
  
  @Test
  public void testHexAndBoardConstruction(){
    Board b0 = new Board(1, 3);
    assertEquals(b0.getHeight(), 1);
    assertEquals(b0.getWidth(), 3);
    
    //Test that a board can be populated with hexes correctly
    
    Board b = new Board(2,2);
    Hex one = new SimpleHex(b, 0, 0);
    Hex two = new SimpleHex(b, 0, 1);
    Hex three = new SimpleHex(b, 1, 0);
    Hex four = new SimpleHex(b, 1, 1);
    
    assertEquals(b.getHeight(), 2);
    assertEquals(b.getWidth(), 2);
    assertEquals(b.getHex(0, 0), one);
    assertEquals(b.getHex(0, 1), two);
    assertEquals(b.getHex(1, 0), three);
    assertEquals(b.getHex(1, 1), four);
    
    //Check that errors are thrown at the correct time.
    try{
      new SimpleHex(null, 0, 0);
      fail("Successfully constructed hex into null board");
    }catch(IllegalArgumentException e){}
    try{
      new SimpleHex(b, -1, 0);
      fail("Successfully constructed hex into Illegal Position");
    }catch(IllegalArgumentException e){}
    try{
      new SimpleHex(b, 0, 0);
      fail("Successfully constructed hex into already occupied Position");
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
        new SimpleHex(b, i,j);
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
    
    for(Location loc : l){
      assertTrue(loc.isAdjacentTo(new Location(1,1)));
    }
    
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
    
    for(Location loc : l){
      assertTrue(loc.isAdjacentTo(new Location(1,2)));
    }
    
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
    
    //Check other assorted location.isadjacent
    assertFalse(new Location(0,0).isAdjacentTo(new Location(2,0)));
    assertFalse(new Location(0,0).isAdjacentTo(new Location(0,0)));
    assertFalse(new Location(0,0).isAdjacentTo(Location.NOWHERE));
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
    
    //Test Random colorCircle functions
    Color[] c6 = ColorCircle.randomArray(5, 8);
    assertEquals(5, c6.length);
    Color[] c7 = ColorCircle.random(5, 8).toArray();
    assertEquals(5, c7.length);
    Color[] c8 = ColorCircle.random(8, 2).toArray();
    HashSet<Color> distinctColors = new HashSet<Color>();
    for(Color col : c8)
      distinctColors.add(col);
    assertTrue(2>=distinctColors.size());
    
    try{
      ColorCircle.randomArray(0, 9);
      fail("Created Size 0 Color Array");
    }catch(IllegalArgumentException e){}
    try{
      ColorCircle.random(0,9 );
      fail("Created Size 0 Color Circle");
    }catch(IllegalArgumentException e){}
    try{
      ColorCircle.randomArray(-2, 9);
      fail("Created negative size Color Array");
    }catch(IllegalArgumentException e){}
    try{
      ColorCircle.random(-2, 9);
      fail("Created negative size Color Circle");
    }catch(IllegalArgumentException e){}
    try{
      ColorCircle.random(7, 0);
      fail("Created color circle with at most 0 colors");
    }catch(IllegalArgumentException e){}
    try{
      ColorCircle.random(7, -2);
      fail("Created color circle with at most -2 colors");
    }catch(IllegalArgumentException e){}
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
      assertEquals(c[i], p.colorOfSide(i));
    }
    
    //Test forward rotation
    p.rotate();
    Color[] cRotated = {Color.PINK, Color.BLUE, Color.RED, Color.CYAN, Color.ORANGE, Color.YELLOW};
    for(int i = 0; i < cRotated.length; i++){
      assertEquals(cRotated[i], p.colorArray()[i]);
      assertEquals(cRotated[i], p.colorOfSide(i));
    }
    
    //Test backward rotation - color array back to original
    p.rotateCounter();
    //Test getting array and individual positions
    for(int i = 0; i < c.length; i++){
      assertEquals(c[i], p.colorArray()[i]);
      assertEquals(c[i], p.colorOfSide(i));
    }
  }
  
  /** Checks if two hexes are linked with the expected color, both ways */
  private void helpLink(Hex one, Hex two, Color expected){
    if(one != null) assertEquals(one + " and " + two + " colorLinked", expected, one.colorLinked(two));
    if(two != null) assertEquals(two + " and " + one + " colorLinked", expected, two.colorLinked(one));
  }
  
  @Test
  public void testSparkConstructionAndColorSwap(){
    Board b = new Board();
    
    //Test spark construction with unset colors
    Color[] c = {Color.RED, Color.BLUE};
    Spark one = new Spark(b, 0, 0, c);
    assertEquals(one, b.getHex(0, 0));
    Color[] c2 = one.getAvaliableColors();
    for(int i = 0; i < c.length; i++){
      assertEquals(c[i], c2[i]);
    }
    //Test getting current color
    assertEquals(Color.RED, one.getColor());
    one.useNextColor();
    assertEquals(Color.BLUE, one.getColor());
    one.useNextColor();
    assertEquals(Color.RED, one.getColor());
    
    //Check always-lit
    assertEquals(Color.RED, one.isLit().iterator().next());
    
    //Check in-bounds colorOfSide
    for(int i = 0; i < Hex.SIDES; i++){
      assertEquals(Color.RED, one.colorOfSide(i));
    }
    
    //Check illegal color setting
    try{
      new Spark(b, 2, 2, null);
      fail("Created spark with null color circle");
    }catch(IllegalArgumentException e){}
    try{
      new Spark(b, 1, 0, new Color[0]);
      fail("Created spark with length 0 color circle");
    }catch(IllegalArgumentException e){}
    try{
      one.setAvaliableColors(c);
      fail("Reset colorCircle of already set spark");
    }catch(IllegalArgumentException e){}
  }
  
  @Test
  public void testColorLinked(){
    Board b = new Board(3,3);
    Color[] cOne = {Color.NONE, Color.NONE, Color.BLUE, Color.ORANGE, Color.NONE, Color.NONE};
    Prism one = new Prism(b, 0, 0, cOne);
    Color[] cTwo = {Color.NONE, Color.RED, Color.NONE, Color.NONE, Color.NONE, Color.BLUE};
    Prism two = new Prism(b, 0, 1, cTwo);
    Color[] cThree = {Color.GREEN, Color.RED, Color.NONE, Color.NONE, Color.NONE, Color.BLUE};
    Prism three = new Prism(b, 1, 0, cThree); 
    Color[] cFour = {Color.NONE, Color.RED, Color.NONE, Color.GREEN, Color.NONE, Color.BLUE};
    Prism four = new Prism(b, 1, 2, cFour);
    
    //Check correct colorLink
    helpLink(one, two, Color.BLUE);
    
    //Check link to null
    helpLink(two, b.getHex(new Location(0,2)), Color.NONE);
    
    //Check incorrect colorLink
    helpLink(one, three, Color.NONE);
    
    //Check non-neighbors
    helpLink(one, four, Color.NONE);
    
    Color[] cFive = {Color.GREEN};
    Spark s = new Spark(b, 2, 2, cFive);
    
    //Check with Spark
    helpLink(four, s, Color.GREEN);
  }
 
  /** Checks if col contains each element in arr
   * @param <T> - type of elements in containsEach
   * 
   */
  public static <T> boolean containsEach(Collection<T> col, T[] arr){
    for(T t : arr){
      if(! col.contains(t)) return false;
    }
    return true;
  }
  
  /** Checks if each hex in b is lit the corresponding color of lit. Empty board spaces should be null */
  public static void helpLight(Board b, Color[][][] lit){
    for(int i = 0; i < b.getHeight(); i++){
      for(int j = 0; j < b.getWidth(); j++){
        Hex h = b.getHex(i,j);
        if(h == null)
          assertEquals("No hex at (" + i + "," + j + ")", lit[i][j], h);
        else{
          Collection<Color> c = h.isLit();
          assertTrue("Hex at (" + i + "," + j + ") lit ", lit[i][j].length == c.size() && containsEach(c, lit[i][j]));
        }
          
      }
    }
  }
  
  @Test
  public void testLighting(){
    Board b = new Board(4,3);
    Color[][] colors = { Colors.fill(Hex.SIDES, Color.NONE),                                      //(0,0)
                         {Color.NONE, Color.NONE, Color.NONE, Color.RED, Color.NONE, Color.RED},  //(0,1)
                         {Color.NONE, Color.NONE, Color.RED, Color.RED, Color.NONE, Color.NONE},  //(0,2)
                         {Color.RED, Color.BLUE},                                                 //(1,0)
                         {Color.RED, Color.NONE, Color.RED, Color.NONE, Color.BLUE, Color.BLUE},  //(1,1)
                         {Color.RED, Color.RED, Color.RED, Color.RED, Color.RED, Color.RED},      //(1,2)
                         {Color.RED, Color.BLUE, Color.BLUE, Color.NONE, Color.NONE, Color.NONE}, //(2,0)
                         {Color.BLUE, Color.NONE, Color.NONE, Color.GREEN, Color.BLUE, Color.BLUE},//(2,1)
                         {Color.BLUE, Color.GREEN},                                               //(2,2)
                         Colors.fill(Hex.SIDES, Color.NONE),                                       //(3,0)
                         Colors.fill(Hex.SIDES, Color.NONE),                                      //(3,1)
                         {Color.GREEN},                                                           //(3,2)
                       };
    new Prism(b, 0, 0, colors[0]);
    new Prism(b, 0, 1, colors[1]);
    new Prism(b, 0, 2, colors[2]);
    new Spark(b, 1, 0, colors[3]);
    new Prism(b, 1, 1, colors[4]);
    new Prism(b, 1, 2, colors[5]);
    new Prism(b, 2, 0, colors[6]);
    new Prism(b, 2, 1, colors[7]);
    new Spark(b, 2, 2, colors[8]);
    new Prism(b, 3, 0, colors[9]);
    new Prism(b, 3, 1, colors[10]);
    new Spark(b, 3, 2, colors[11]);
   
    //Show for debugging purposes - uncomment and step through rotations to see board.
//    SimpleGame game = new SimpleGame(null, null);
//    game.setGUI(new GUI(game, false));
//    b.setGame(game);
//    game.setBoard(b);
//    game.reset();
    
    //Set initial lighting
    b.relight();
    
    Color[][][] lighting = {{ {}, {}, {}},{{Color.RED}, {}, {}},{{Color.RED}, {}, {Color.BLUE}}, {{}, {}, {Color.GREEN}, {}}};
    helpLight(b, lighting);
    
    //Test rotating a prism propigates light
    b.getHex(1, 1).asPrism().rotateCounter();
    Color[][][] lightingTwo = {{{}, {}, {Color.RED}},{{Color.RED}, {Color.RED}, {Color.RED}},{{Color.RED}, {}, {Color.BLUE}}, {{}, {}, {Color.GREEN}, {}}};
    helpLight(b, lightingTwo);
    
    //Test undoing rotation removes light
    b.getHex(1,1).asPrism().rotate();
    helpLight(b, lighting);
    
    //Test a merging branch can find light from one fork if the other leaves
    
    //Create two paths
    b.getHex(0, 1).asPrism().rotateCounter();
    b.getHex(1, 1).asPrism().rotateCounter();
    
    Color[][][] lightingThree = {{{}, {Color.RED}, {Color.RED}},{{Color.RED}, {Color.RED}, {Color.RED}},{{Color.RED}, {}, {Color.BLUE}}, {{}, {}, {Color.GREEN}, {}}};
    helpLight(b, lightingThree);
    
    //Remove top path
    b.getHex(0, 1).asPrism().rotate();
    
    helpLight(b, lightingTwo);
    
    //Test Rotating a prism that still merges doesn't remove light
    b.getHex(1,2).asPrism().rotate();
    helpLight(b, lightingTwo);
    
    //Test adding second color of light adds to currently lit prism
    b.getHex(2, 1).asPrism().rotate();
    Color[][][] lightingFour = {{{}, {}, {Color.RED}},{{Color.RED}, {Color.RED, Color.BLUE}, {Color.RED}},{{Color.RED, Color.BLUE}, {Color.BLUE}, {Color.BLUE}}, {{}, {}, {Color.GREEN}, {}}};
    helpLight(b, lightingFour);

    
    //Test turning off Spark removes light
    b.getHex(1,0).asSpark().useNextColor();
    Color[][][] lightingFive = {{{}, {}, {}},{{Color.BLUE}, {Color.BLUE}, {}},{{Color.BLUE}, {Color.BLUE}, {Color.BLUE}}, {{}, {}, {Color.GREEN}, {}}};
    helpLight(b, lightingFive);
    
    //Test turning off spark removes light even from cycle.
    b.getHex(2,2).asSpark().useNextColor();
    Color[][][] lightingSix = {{{}, {}, {}}, {{Color.BLUE}, {}, {}}, {{}, {}, {Color.GREEN}}, {{}, {}, {Color.GREEN}, {}}};
    helpLight(b, lightingSix);
  }
  
  @Test
  public void testCrystalConstructionandLighting(){
    Board b = new Board(2,4);
    
    Color[][] colors = { {Color.RED}, 
                         {Color.RED, Color.NONE, Color.NONE, Color.NONE, Color.RED, Color.NONE},
                         {},
                         {Color.BLUE},
                         {Color.BLUE, Color.NONE, Color.NONE, Color.NONE, Color.BLUE, Color.NONE},
                         {Color.BLUE, Color.NONE, Color.NONE, Color.NONE, Color.BLUE, Color.NONE}};
    
    new Spark(b, 0, 0, colors[0]);
    new Prism(b, 0, 1, colors[1]);
    new Crystal(b, 0, 2);
    new Crystal(b, 0, 3);
    new Spark(b, 1, 0, colors[3]);
    new Prism(b, 1, 1, colors[4]);
    new Prism(b, 1, 2, colors[5]);
    new Crystal(b, 1, 3);
    
    //Show for debugging purposes - uncomment and step through rotations to see board.
//    SimpleGame game = new SimpleGame(null, null);
//    game.setGUI(new GUI(game, false));
//    b.setGame(game);
//    game.setBoard(b);
//    game.reset();
    
    //Set initial lighting
    b.relight();                
    
    Color[][][] lighting = { {{Color.RED}, {}, {}, {}}, {{Color.BLUE}, {}, {}, {}}};
    helpLight(b, lighting);
    
    //Light the crystal - adjacent crystal should not be lit.
    b.getHex(0,1).asPrism().rotate();
    
    Color[][][] lightingTwo = {{{Color.RED}, {Color.RED}, {Color.RED}, {}},  {{Color.BLUE}, {}, {}, {}}};
    helpLight(b, lightingTwo);
    
    //Add an input of a different color - crystal shouldn't change color.
    b.getHex(1,1).asPrism().rotate();
    Color[][][] lightingThree = {{{Color.RED}, {Color.RED}, {Color.RED}, {}}, {{Color.BLUE}, {Color.BLUE}, {Color.BLUE}, {}}};
    helpLight(b, lightingThree);
    
    //Remove first light - crystal should pick up new color.
    b.getHex(0,1).asPrism().rotate();
    Color[][][] lightingFour = {{{Color.RED}, {}, {Color.BLUE}, {}}, {{Color.BLUE}, {Color.BLUE}, {Color.BLUE}, {}}};
    helpLight(b,lightingFour); 
  }


}
