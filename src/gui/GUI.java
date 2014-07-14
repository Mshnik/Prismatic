package gui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import models.*;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import util.*;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;


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
    
    centerPanel = new JPanel();
    centerPanel.setBackground(Color.WHITE);
    centerPanel.setLayout(null);
    centerPanel.setSize(new Dimension(10000, 10000));
    
    JScrollPane scrollPane = new JScrollPane(centerPanel);
    scrollPane.setSize(800, 800);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    
    setSize(new Dimension(800, 800));
    
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
  }
  
  /** Closes a gui as if its x was clicked */
  public void close() {
    WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
  }
  
  public void createAndAddHexPanel(Hex h){
    centerPanel.add(new HexPanel(h));
  }
  

  private static final int HEX_RAD = 80;
  private static final int BUFFER = 80;

  /** One hex, as a drawable element */
  public class HexPanel extends JPanel{
    private static final long serialVersionUID = 1L;
    private final Hex h;
    private final int xIndex;
    private final int yIndex;
    private final Point center;
    private final Polygon poly;
    
    public HexPanel(Hex h){
      this.h = h;
      xIndex = h.location.col;
      yIndex = h.location.row;
      int yOffset = 0;
      if(xIndex % 2 == 1) yOffset = HEX_RAD;
      center = new Point(BUFFER + HEX_RAD * 2 * xIndex * 3/4, BUFFER + HEX_RAD * 2 * yIndex + yOffset);
      poly = new Polygon();
      for(int i = 0; i < Hex.SIDES; i++){
        poly.addPoint((int)(HEX_RAD + HEX_RAD * Math.cos((i - 2) * 2 * Math.PI / Hex.SIDES)), 
                      (int)(HEX_RAD + HEX_RAD * Math.sin((i - 2) * 2 * Math.PI / Hex.SIDES)));
      }
      //setBorder(BorderFactory.createLineBorder(Color.BLACK));
      setLayout(null);
      setBounds(center.x - HEX_RAD, center.y - HEX_RAD, HEX_RAD * 2, HEX_RAD * 2);
      addMouseListener(new RotationListener());
    }
    
    private class RotationListener implements MouseListener{

      @Override
      public void mouseClicked(MouseEvent e) {
        if(poly.contains(e.getPoint())){
          HexPanel h = (HexPanel)e.getSource();
          Hex hex = h.h;
          if(hex instanceof Prism){
            Prism p = (Prism)hex;
            p.rotate();
          }
          else if(hex instanceof Spark){
            Spark s = (Spark)hex;
            s.useNextColor();
          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {}

      @Override
      public void mouseReleased(MouseEvent e) {}

      @Override
      public void mouseEntered(MouseEvent e) {}

      @Override
      public void mouseExited(MouseEvent e) {}
      
    }
    
    public void paintComponent(Graphics g){
      g.drawPolygon(poly);
      if(h instanceof Prism){
        Prism p = (Prism)h;
        for(int i = 0; i < Hex.SIDES; i++){
          Polygon triangle = new Polygon();
          triangle.addPoint(HEX_RAD, HEX_RAD);
          for(int k = i; k < i+2; k++){
            triangle.addPoint(poly.xpoints[Util.mod(k, Hex.SIDES)], poly.ypoints[Util.mod(k, Hex.SIDES)]);
          }
          g.drawPolygon(triangle);
          g.setColor(Board.colorFromColor(p.colorOfSide(i)));
          g.fillPolygon(triangle);
        }
        if(p.isLit()){
          g.setColor(Color.YELLOW);
          Graphics2D g2 = (Graphics2D)g;
          g2.setStroke(new BasicStroke(5));
          g.drawPolygon(poly);
        }
      }
      else if(h instanceof Spark){
        Spark s = (Spark)h;
        g.setColor(Board.colorFromColor(s.getColor()));
        g.fillPolygon(poly);
        g.setColor(Color.YELLOW);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(5));
        g.drawPolygon(poly);
      }
    } 
  }
  
  /** Creates a sample gui and allows playing with it */
  public static void main(String[] args){
    Board b = new Board(5,5);
    GUI g = new GUI(b);
    for(int r = 0; r < b.getHeight(); r++){
      for(int c = 0; c < b.getWidth(); c++){
        if(r != 0 || c != 0){
          Prism p = new Prism(b, r, c, ColorCircle.randomArray(Hex.SIDES));
          g.createAndAddHexPanel(p);
        } else{
          Spark s = new Spark(b, r, c, ColorCircle.randomArray(Hex.SIDES));
          g.createAndAddHexPanel(s);
        }
        g.repaint();
      }
    }
    b.relight();
  }
  
}
