package com.prismatic.util;


import com.prismatic.models.Color;

/** Utilities for dealing with Color. Stupid Java.... */
public class Colors {
  
//Not Supported by GWT.
  
//  /** Returns the Java awt color corresponding to this color enum value */
//  public static java.awt.Color colorFromColor(Color c){
//    switch(c){
//      case BLUE: return java.awt.Color.BLUE;
//      case CYAN: return java.awt.Color.CYAN;
//      case GREEN:return java.awt.Color.GREEN;
//      case NONE: return new java.awt.Color(0, 0, 0, 1); //clear
//      case ORANGE: return java.awt.Color.ORANGE;
//      case PINK: return java.awt.Color.PINK;
//      case PURPLE: return java.awt.Color.MAGENTA;
//      case RED: return java.awt.Color.RED;
//      case YELLOW: return java.awt.Color.YELLOW;
//      default: return java.awt.Color.white;
//    }
//  }

  /** Returns a subArray of colors, starting at color n and giving l colors. Loops back around as nessary.
   * (Thus giving n > Color.values().length with have duplicates in the return) */
  public static Color[] subValues(int offset, int n){
    Color[] c = new Color[n];
    for(int i = offset; i < n + offset; i++){
      c[i - offset] = Color.values()[Util.mod(i, Color.values().length)];
    }
    return c;
  }

  /** Returns an array of length l filled with Color.NONE */
  public static Color[] noneArray(int length){
    Color[] c = new Color[length];
    for(int i = 0; i < length; i++){
      c[i] = Color.NONE;
    }
    return c;
  }
}
