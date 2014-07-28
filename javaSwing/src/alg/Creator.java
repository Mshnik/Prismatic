package alg;

import java.util.LinkedList;

import models.*;
import util.*;

/** storage for static methods relating to creating puzzles. */
public class Creator {

  /** Creates a random-ish path from start to end (return[0] = start, return[last] = end),
   * that stays within [0..maxRow][0..maxCol].
   * Assumes Hex.NEIGHBOR_COORDINATES for adjacent nodes.
   * end must be reachable by some path from start of the method will never terminate.
   * 
   * Slight probabilistic weighting towards moving towards the exit.
   * 
   * @param start - the start location of the random walk
   * @param end   - the end location of the random walk
   * @param maxRow - the cap for location row in the random walk.
   * @param maxCol - the cap for location col in the random walk.
   * @return - a random path from (including) start to end. Will not have repeated nodes - a simple path.
   * @throws IllegalArgumentException if maxRow < 0 or maxCol < 0 or start == end
   */
  public static LinkedList<Location> randomWalk(Location start, Location end, int maxRow, int maxCol) throws IllegalArgumentException{
    if(maxRow < 0 || maxCol < 0) throw new IllegalArgumentException("Can't do random walk with maxRow " + maxRow + ", maxCol " + maxCol);
    if(start.equals(end)) throw new IllegalArgumentException("Can't do random walk where start == end (" + start + ")");
    if(start.row < 0 || start.row > maxRow || start.col < 0 || start.col > maxCol)
      throw new IllegalArgumentException("Start location " + start + " out of bounds");
    if(end.row < 0 || end.row > maxRow || end.col < 0 || end.col > maxCol)
      throw new IllegalArgumentException("End location " + end + " out of bounds");
    LinkedList<Location> l = new LinkedList<Location>();
    
    Location here = start;
    do{
      //Determine if we have been here before. If so, remove newly formed cycle
      int i = l.indexOf(here);
      if(i != -1){
        int len = l.size();
        for(int j = i; j < len; j++){
          l.remove(i);      //Repeatedly removing the same index (which is filled by later and later nodes)
                            //Will ultimately remove i ... len-1 (rest of the list).
        }
      }
      //Add new here to path
      l.add(here);
      
      //Pick a random neighbor
      Location neighbor;
      do{
        //Picks a random direction, weighting choosing a direction towards the end.
        int r = (int)(Math.pow(Math.random(),2.0) * Hex.SIDES) ;
        neighbor = end.orderByCloseness(here.neighbors(), maxRow, maxCol)[r];
      }while(neighbor.row > maxRow || neighbor.col > maxCol || neighbor.row < 0 || neighbor.col < 0);
      
      //Go there, set here to there.
      here = new Location(neighbor.row, neighbor.col);
    }while(! (here.equals(end)));
    l.add(end);
    return l;
  }
  
}
