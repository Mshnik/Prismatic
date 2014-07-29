package util;

import models.Color;

/** Utilities for dealing with Color. Stupid Java.... */
public class Colors {
  
  /** Returns the Java awt color corresponding to this color enum value */
  public static java.awt.Color colorFromColor(Color c){
    switch(c){
      case BLUE: return java.awt.Color.BLUE;
      case CYAN: return java.awt.Color.CYAN;
      case GREEN:return java.awt.Color.GREEN;
      case NONE: return new java.awt.Color(0, 0, 0, 1); //clear
      case ORANGE: return java.awt.Color.ORANGE;
      case PINK: return java.awt.Color.PINK;
      case PURPLE: return java.awt.Color.MAGENTA;
      case RED: return java.awt.Color.RED;
      case YELLOW: return java.awt.Color.YELLOW;
      default: return java.awt.Color.white;
    }
  }

  /** Number of special colors in the Color enum. After this many, the rest are regular colors */
  public static final int SPECIAL_OFFSET = 2;
  
  /** Returns true if this color is a regular color, false if it s a special color */
  public static boolean isRegularColor(Color c){
    return c != Color.NONE && c != Color.ANY;
  }
  
  /** Returns the regular colors */
  public static Color[] regularColors(){
    return subValues(Integer.MAX_VALUE);
  }
  
  /** Returns a subArray of REGULAR colors, starting at color n and giving l colors. Caps at the available number of regular colors */
  public static Color[] subValues(int n){
    int len = Math.min(Color.values().length - SPECIAL_OFFSET, n);
    Color[] c = new Color[len];
    for(int i = SPECIAL_OFFSET; i < len + SPECIAL_OFFSET ; i++){
      c[i - SPECIAL_OFFSET] = Color.values()[i];
    }
    return c;
  }

  /** Returns an array of length l filled with the given color */
  public static Color[] fill(int length, Color col){
    Color[] c = new Color[length];
    for(int i = 0; i < length; i++){
      c[i] = col;
    }
    return c;
  }
  
  /** Hashes a color array based on the sum of its hashes
   * This is done so order doesn't matter -> {green, red}.hash == {red, green}.hash
   */
  public static Integer hashArray(Color[] arr){
    int i = 0;
    for(Color c : arr){
      i += c.hashCode();
    }
    return i;
  }

}
