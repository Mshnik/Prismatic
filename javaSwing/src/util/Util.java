package util;

public class Util {

  /** Correctly mods a by b, returning an int in the range [0, b)  --> Java sucks */
  public static int mod(int a, int b){
    return ((a % b) + b) % b;
  }
  
  /** Surrounds the given string with a pair of quotes. Nice for JSONing.
   * 
   */
  public static String addQ(String s){
    return "\"" + s + "\"";
  }
  
  
  /** Surrounds the given string with a double pair of quotes. Nice for JSONing.
   * 
   */
  public static String add2Q(String s){
    return "\"\\\"" + s + "\\\"\"";
  }
  
}
