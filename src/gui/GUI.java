package gui;

import javax.swing.JFrame;
import models.*;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import util.*;


/** GUI for testing purposes - shows board and allows for mutation */
public class GUI extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 5860367452991049874L;

  private final Board board;
  private static GUI instance = null; //Currently open instance. (only construct one of these at a time)
  
  //JFrame stuff
  private JPanel centerPanel;
  
  /** Returns the currently open GUI instance */
  public static GUI getInstance(){
    return instance;
  }
  
  /** Constructs a gui to display board b */
  public GUI(Board b){
    setAlwaysOnTop(true);
    board = b;
    if(instance != null) instance.close();
    instance = this;
    getContentPane().setLayout(new BorderLayout(0, 0));
    
    centerPanel = new HexPanel();
    centerPanel.setBackground(Color.WHITE);
    getContentPane().add(centerPanel, BorderLayout.CENTER);
    
    setSize(new Dimension(800, 800));
    
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
  }
  
  /** Closes a gui as if its x was clicked */
  public void close() {
    WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
  }
  
  private static final int BUFFER = 50;
  private static final int HEX_RAD = 100;
  
  
  public class HexPanel extends JPanel{
    private static final long serialVersionUID = 1L;

    public void paintComponent(Graphics g){
      
      //Paint hexes
      for(Hex h : board.allHexes()){
        drawHex(h, g);
      }
    }
    
    /** Tells the gui how to draw a hex - draws as a polygon */
    private void drawHex(Hex h, Graphics g){
      int x = h.location.col;
      int y = h.location.row;
      Polygon poly = new Polygon();
      
      int yOffset = 0;
      if(x % 2 == 1) yOffset = HEX_RAD/2;
      
      Point center = new Point(BUFFER + HEX_RAD * x * 3/4, BUFFER + HEX_RAD * y + yOffset); //Center of poly
            
      for(int i = 0; i < Hex.SIDES; i++){
        poly.addPoint((int)(center.x + HEX_RAD/2 * Math.cos(i * 2 * Math.PI / Hex.SIDES)), 
                      (int)(center.y + HEX_RAD/2 * Math.sin(i * 2 * Math.PI / Hex.SIDES)));
      }
      g.drawPolygon(poly);
      if(h instanceof Prism){
        Prism p = (Prism)h;
        for(int i = 0; i < Hex.SIDES; i++){
          Polygon triangle = new Polygon();
          triangle.addPoint(center.x, center.y);
          for(int k = i; k < i+2; k++){
            triangle.addPoint(poly.xpoints[Util.mod(k, Hex.SIDES)], poly.ypoints[Util.mod(k, Hex.SIDES)]);
          }
          g.drawPolygon(triangle);
          g.setColor(Board.colorFromColor(p.colorOfSide(i)));
          g.fillPolygon(triangle);
        }
      }
      else if(h instanceof Spark){
        Spark s = (Spark)h;
        g.setColor(Board.colorFromColor(s.getColor()));
        g.fillPolygon(poly);
      }
    }
  }
  
  /** Creates a sample gui and allows playing with it */
  public static void main(String[] args){
    Board b = new Board(5,5);
    for(int r = 0; r < b.getHeight(); r++){
      for(int c = 0; c < b.getWidth(); c++){
        new Prism(b, r, c, ColorCircle.randomArray(Hex.SIDES));
      }
    }
    
    new GUI(b);
  }
  
}
