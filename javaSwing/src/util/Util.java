package util;

public class Util {

  /** Correctly mods a by b, returning an int in the range [0, b)  --> Java sucks */
  public static int mod(int a, int b){
    return ((a % b) + b) % b;
  }
  
  
}
