package gui;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import models.*;
import game.*;
import util.*;

import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.util.HashMap;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.BoxLayout;


/** GUI for testing purposes - shows board and allows for mutation */
public class GUI extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 5860367452991049874L;

  private Game game;
  private static GUI instance = null; //Currently open instance. (only construct one of these at a time)
  private boolean interactive;
  
  private HashMap<Color, Boolean> colorEnabled;
  
  //JFrame stuff
  private JPanel centerPanel;
  private JLabel scoreLabel;
  private JLabel puzzleLabel;
  
  /** Returns the currently open GUI instance */
  public static GUI getInstance(){
    return instance;
  }
  
  /** Returns the game of this GUI */
  public Game getGame(){
    return game;
  }
  
  /** Constructs a gui to display game g. Interactive if interactive. */
  public GUI(Game g, boolean interactive){
    setAlwaysOnTop(true);
    game = g;
    this.interactive = interactive;
    g.setGUI(this);
    instance = this;
    getContentPane().setLayout(new BorderLayout(0, 0));
    
    centerPanel = new JPanel();
    centerPanel.setBackground(java.awt.Color.WHITE);
    centerPanel.setLayout(null);
    centerPanel.setSize(new Dimension(10000, 10000));
    
    JScrollPane scrollPane = new JScrollPane(centerPanel);
    scrollPane.setSize(800, 800);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.NORTH);
    panel.setLayout(new BorderLayout(0, 0));
      
      JPanel panel_2 = new JPanel();
      panel.add(panel_2, BorderLayout.EAST);
      
      scoreLabel = new JLabel("Moves: 0");
      panel_2.add(scoreLabel);
      
      JPanel panel_1 = new JPanel();
      panel.add(panel_1, BorderLayout.CENTER);
      panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
      
      puzzleLabel = new JLabel("  Goal:");
      panel_1.add(puzzleLabel);
      
      if(interactive){
        JButton btnNewButton_1 = new JButton("Reset Game");
        panel_2.add(btnNewButton_1);
        btnNewButton_1.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            GUI self = GUI.instance;
            self.game.reset();
          }
        });
      }

      
    setMinimumSize(new Dimension(1400, 1200));
    
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    
    if(interactive){
      JMenu mnFile = new JMenu("File");
      menuBar.add(mnFile);
      
      JMenuItem mntmSaveBoard = new JMenuItem("Save Board");
      mntmSaveBoard.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          GUI self = GUI.instance;
          self.game.saveBoard();
        }
      });
      mnFile.add(mntmSaveBoard);
      
      JMenuItem mntmLoadBoard = new JMenuItem("Load Board");
      mntmLoadBoard.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          GUI self = GUI.instance;
          if(self.game instanceof LoadedGame){
            LoadedGame g = (LoadedGame)self.game;
            g.load();
          }
        }
      });
      mnFile.add(mntmLoadBoard);
    }
    
    JMenu mnNewMenu = new JMenu("Color Filter");
    menuBar.add(mnNewMenu);

    
    colorEnabled = new HashMap<Color, Boolean>();
    colorEnabled.put(Color.NONE, false);
    colorEnabled.put(Color.ANY, false);
    for(int i = Colors.SPECIAL_OFFSET; i < Colors.SPECIAL_OFFSET + game.getDifficulty(); i++){
      JCheckBoxMenuItem ckb = new JCheckBoxMenuItem(Color.values()[i].toString());
      mnNewMenu.add(ckb);
      colorEnabled.put(Color.values()[i], true);
      ckb.setSelected(true);
      ckb.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JCheckBoxMenuItem c = (JCheckBoxMenuItem)e.getSource();
        colorEnabled.put(Color.valueOf(c.getText()), c.isSelected());
        retile();
      }
    });
    }
    
    if(interactive){
      JMenuItem mntmRotationClockwise = new JMenuItem("Rotation: Clockwise");
      Prism.ROTATE_CLOCKWISE = true;
      mntmRotationClockwise.setHorizontalAlignment(SwingConstants.LEFT);
      mntmRotationClockwise.addMouseListener(new MouseAdapter(){
        @Override
        public void mouseClicked(MouseEvent e){
          JMenuItem source = (JMenuItem)e.getSource();
          if(Prism.ROTATE_CLOCKWISE){
            source.setText("Rotation: Counter Clockwise");
            Prism.ROTATE_CLOCKWISE = false;
          } else{
            source.setText("Rotation: Clockwise");
            Prism.ROTATE_CLOCKWISE = true;
          }
        }
      });
      menuBar.add(mntmRotationClockwise);
    }
    
    setVisible(true);
    setAlwaysOnTop(false);
  }
  
  /** Updates the puzzle label */
  public void updatePuzzleLabel(){
    if(game instanceof CreatedGame){
      CreatedGame game2 = (CreatedGame)game;
      puzzleLabel.setText("  Goal: " + game2.puzzleString());
    }
  }
  
  /** Updates the score label */
  public void updateScoreLabel(){
    if(game.getBoard()!= null) scoreLabel.setText("Moves: " + game.getBoard().getMoves());
  }
  
  /** Creates a hex panel for hex h - draws h */
  public void createAndAddHexPanel(Hex h){
    centerPanel.add(new HexPanel(h, this));
  }
  
  /** Removes all hexpanels (perhaps so they can be redrawn) */
  public void removeAllHexPanels(){
    centerPanel.removeAll();
  }
  
  /** Repopulates this Gui with the board */
  public void retile(){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        removeAllHexPanels();
        if(game.getBoard() != null){
          for(Hex h : game.getBoard().allHexes()){
            createAndAddHexPanel(h);
          }
        }
        repaint();
      }
    });
  }
  

  private static final int HEX_RAD = 80;
  private static final int SPACE = 10;
  private static final int BUFFER = 80;

  /** One hex, as a drawable element */
  public class HexPanel extends JPanel{
    private static final long serialVersionUID = 1L;
    private final Hex h;
    private final int xIndex;
    private final int yIndex;
    private final Point center;
    private final Polygon poly;
    private boolean interactive;
    
    public HexPanel(Hex h, GUI g){
      this.h = h;
      interactive = g.interactive;
      xIndex = h.location.col;
      yIndex = h.location.row;
      int yOffset = 0;
      if(xIndex % 2 == 1) yOffset = (HEX_RAD);
      center = new Point(BUFFER + (HEX_RAD + SPACE) * 2 * xIndex * 3/4, BUFFER + (HEX_RAD) * 2 * yIndex + yOffset);
      poly = new Polygon();
      for(int i = 0; i < Hex.SIDES; i++){
        poly.addPoint((int)(HEX_RAD + HEX_RAD * Math.cos((i - 2) * 2 * Math.PI / Hex.SIDES)), 
                      (int)(HEX_RAD + HEX_RAD * Math.sin((i - 2) * 2 * Math.PI / Hex.SIDES)));
      }
      //setBorder(BorderFactory.createLineBorder(Color.BLACK));
      setLayout(null);
      setBounds(center.x - HEX_RAD, center.y - HEX_RAD, HEX_RAD * 2, HEX_RAD * 2);
      addMouseListener(new RotationListener(interactive));
    }
    
    private class RotationListener implements MouseListener{

      private boolean interactive;
      public RotationListener(boolean b){
        interactive = b;
      }
      
      @Override
      public void mouseClicked(MouseEvent e) {
        if(poly.contains(e.getPoint()) && interactive){
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
          g.setColor(java.awt.Color.BLACK);
          g.drawPolygon(triangle);
          if(colorEnabled.get(p.colorOfSide(i))){
            java.awt.Color c = Colors.colorFromColor(p.colorOfSide(i));
            g.setColor(c);
            g.fillPolygon(triangle);
          }
        }
        if(! p.isLit().isEmpty()){
          Color[] lit = p.isLit().toArray(new Color[0]);
          Graphics2D g2 = (Graphics2D)g;
          for(Color c : lit){
            for(int s = 0; s < Hex.SIDES; s++){
              if(p.colorOfSide(s) == c){
                Point out = new Point((int)(HEX_RAD + (HEX_RAD + SPACE * 1.9) * Math.cos((s - 2) * 2 * Math.PI / Hex.SIDES + Math.PI/6)), 
                    (int)(HEX_RAD + (HEX_RAD + SPACE*1.9) * Math.sin((s - 2) * 2 * Math.PI / Hex.SIDES + Math.PI/6)));
                g2.setStroke(new BasicStroke(13));
                g.setColor(java.awt.Color.YELLOW);
                g2.drawLine(HEX_RAD, HEX_RAD, out.x, out.y);
                g2.setStroke(new BasicStroke(8));
                g.setColor(Colors.colorFromColor(c));
                g2.drawLine(HEX_RAD, HEX_RAD, out.x, out.y);
                g.setColor(java.awt.Color.YELLOW);
                g2.setStroke(new BasicStroke(20));
                g2.drawOval(HEX_RAD-10, HEX_RAD-10, 20, 20);
              }
            }
          }
//          for(int i = 0; i < Hex.SIDES; i++){
//            g.setColor(Colors.colorFromColor(lit[i % lit.length]));
//            g.drawLine(poly.xpoints[i], poly.ypoints[i], poly.xpoints[(i+1)%Hex.SIDES], poly.ypoints[(i+1)%Hex.SIDES]);
//          }
        }
      }
      else if(h instanceof Spark){
        Spark s = (Spark)h;
        g.setColor(Colors.colorFromColor(s.getColor()));
        g.fillPolygon(poly);
        g.setColor(java.awt.Color.YELLOW);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(5));
        g.drawPolygon(poly);
      }
      else if (h instanceof Crystal){
        Crystal c = (Crystal)h;
        g.setColor(Colors.colorFromColor(c.lit()));
        g.fillPolygon(poly);
        g.setColor(java.awt.Color.LIGHT_GRAY);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(5));
        g.drawPolygon(poly);
      }
    } 
  }
}
