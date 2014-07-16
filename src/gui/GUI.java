package gui;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import util.*;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.util.HashMap;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


/** GUI for testing purposes - shows board and allows for mutation */
public class GUI extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 5860367452991049874L;

  private Board board;
  private static GUI instance = null; //Currently open instance. (only construct one of these at a time)
  
  private HashMap<Board.Color, Boolean> colorEnabled;
  
  //JFrame stuff
  private JPanel centerPanel;
  private JLabel scoreLabel;
  
  /** Returns the currently open GUI instance */
  public static GUI getInstance(){
    return instance;
  }
  
  /** Returns the board of this GUI */
  public Board getBoard(){
    return board;
  }
  
  /** Constructs a gui to display board b */
  public GUI(Board b){
    setAlwaysOnTop(true);
    board = b;
    instance = this;
    getContentPane().setLayout(new BorderLayout(0, 0));
    
    centerPanel = new JPanel();
    centerPanel.setBackground(Color.WHITE);
    centerPanel.setLayout(null);
    centerPanel.setSize(new Dimension(10000, 10000));
    
    JScrollPane scrollPane = new JScrollPane(centerPanel);
    scrollPane.setSize(800, 800);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.NORTH);
    panel.setLayout(new BorderLayout(0, 0));
    
    JPanel panel_1 = new JPanel();
    panel.add(panel_1, BorderLayout.WEST);
     
     JLabel lblRotationDirection = new JLabel("  Rotation Direction:");
     panel_1.add(lblRotationDirection);
     
      JButton btnNewButton = new JButton("Clockwise");
      panel_1.add(btnNewButton);
      btnNewButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          JButton source = (JButton)e.getSource();
          if(Prism.ROTATE_CLOCKWISE){
            Prism.ROTATE_CLOCKWISE = false;
            source.setText("Counter Clockwise");
          } else{
            Prism.ROTATE_CLOCKWISE = true;
            source.setText("Clockwise");
          }
        }
      });
      
      JPanel panel_2 = new JPanel();
      panel.add(panel_2, BorderLayout.EAST);
      
      scoreLabel = new JLabel("Moves: 0");
      panel_2.add(scoreLabel);
      
      JButton btnNewButton_1 = new JButton("Reset Game");
      panel_2.add(btnNewButton_1);
      btnNewButton_1.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          GUI self = GUI.instance;
          GUI g = new GUI(makeBoard());
          g.retile();
          self.dispose();
          self.setVisible(false);
        }
      });

      colorEnabled = new HashMap<Board.Color, Boolean>();
      
      JPanel panel_3 = new JPanel();
      panel.add(panel_3, BorderLayout.CENTER);
      panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));
      
      JLabel lblNewLabel = new JLabel("  Show Prism Sides of Color: ");
      panel_3.add(lblNewLabel);
      for(int i = 1; i < 1 + DIFFICULTY; i++){
        JCheckBox ckb = new JCheckBox(Board.Color.values()[i].toString());
        panel_3.add(ckb);
        colorEnabled.put(Board.Color.values()[i], true);
        ckb.setSelected(true);
        ckb.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          JCheckBox c = (JCheckBox)e.getSource();
          colorEnabled.put(Board.Color.valueOf(c.getText()), c.isSelected());
          retile();
        }
      });
      }
    setSize(new Dimension(1200, 1000));
    
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
  }
  
  /** Updates the score label */
  public void updateScoreLabel(){
    scoreLabel.setText("Moves: " + board.getMoves());
  }
  
  /** Creates a hex panel for hex h - draws h */
  public void createAndAddHexPanel(Hex h){
    centerPanel.add(new HexPanel(h));
  }
  
  /** Removes all hexpanels (perhaps so they can be redrawn) */
  public void removeAllHexPanels(){
    centerPanel.removeAll();
  }
  
  /** Repopulates this Gui with the board */
  public void retile(){
    removeAllHexPanels();
    for(Hex h : board.allHexes()){
      createAndAddHexPanel(h);
    }
    repaint();
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
          h.h.click();
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
          g.setColor(Color.BLACK);
          g.drawPolygon(triangle);
          if(colorEnabled.get(p.colorOfSide(i))){
            g.setColor(Board.colorFromColor(p.colorOfSide(i)));
            g.fillPolygon(triangle);
          }
        }
        if(p.isLit() != Board.Color.NONE){
          g.setColor(Board.colorFromColor(p.isLit()));
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
      else if (h instanceof Crystal){
        Crystal c = (Crystal)h;
        g.setColor(Board.colorFromColor(c.isLit()));
        g.fillPolygon(poly);
        g.setColor(Color.LIGHT_GRAY);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(5));
        g.drawPolygon(poly);
      }
    } 
  }
  
  private static Board makeBoard(){
    Board b = new Board(4,9);
    for(int r = 0; r < b.getHeight(); r++){
      for(int c = 0; c < b.getWidth(); c++){
        if(r == 0 && c == 0){
          new Spark(b, r, c, Board.subValues(1, DIFFICULTY));
        } else if(r == 3 && c == 8){
          new Crystal(b, r, c);
        } else{
          new Prism(b, r, c, ColorCircle.randomArray(Hex.SIDES, DIFFICULTY));
        }
      }
    }
    b.relight();
    return b;
  }
  
  public static final int DIFFICULTY = 3;
  
  /** Creates a sample gui and allows playing with it */
  public static void main(String[] args){
    GUI g = new GUI(makeBoard());
    g.retile();
  }
}
