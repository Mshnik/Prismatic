
###A simple location class - nice int tuple.
 Using this over the Point class because constantly shifting from (x,y) to (col,row) is confusing
###
class @Loc

  #Constructs a location corresponding to row r, col c.
  constructor: (row, col) ->
    @row = row
    @col = col

    ## For running A*. May be changed without warning during running the algorithm.
    @prev = null        
    @dist = Number.MAX_VALUE

  ### Resets the fields used by A* ###
  reset : () ->
    @prev = null
    @dist = Number.MAX_VALUE

  ### Returns true if this is OOB. Row or col < 0 or greater than resepctive max ###
  isOOB : (maxR, maxC) ->
    @row < 0 or @col < 0 or @row > maxR or @col > maxC

  ### Returns a object representing this location in cube coordinates -> (x,y,z). ###
  cubeCoordinates : () ->
    x = @col
    z = @row - (@col - (@col & 1))/2
    y = -x - z
    o = {}
    o[x] = x
    o[y] = y
    o[z] = z
    return o

  ### Returns the distance from this to dest using the cube coordinate distance ###
  distance : (dest) ->
    c1 = @cubeCoordinates()
    c2 = dest.cubeCoordinates()
    return (Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y) + Math.abs(c1.z - c2.z))/2

  ### Returns true if this is adjacent to l, false otherwise ###
  isAdjacentTo : (l) ->
    for vec in Hex.NEIGHBOR_COORDINATES[@col %% 2]
      if l.row is @row + vec.row and l.col is @col + vec.col
        return true
    return false

  #Two locations are equal if their rows and cols are equivalent
  equals: (o) ->
    if(not o instanceof Loc) 
      false
    else
      l = (Loc) o
      @row == l.row and @col == l.col

  #A simple string representation of a location
  toString: () ->
    "(" + @row + "," + @col + ")"

  @fromString : (s) ->
    i = s.indexOf(",")
    s1 = s.substring(1,i)
    s2 = s.substring(i+1, s.length - 1)
    return new Loc(parseInt(s1), parseInt(s2))

  ### Returns a new Location that represents a vector from l1 to l2 ###
  @vec : (l1, l2) ->
    return new Loc(l2.row - l1.row, l2.col - l1.col)

  ### Constructs a new Random location in the range [0 ... maxR), [0 ... maxC ###
  @random : (maxR, maxC) ->
    return new Loc(Math.floor(Math.random() * maxR), Math.floor(Math.random() * maxC))

  #A default location corresponding to nowhere
  @NOWHERE = new Loc(-9999, -9999)

