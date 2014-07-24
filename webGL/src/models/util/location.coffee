
###A simple location class - nice int tuple.
 Using this over the Point class because constantly shifting from (x,y) to (col,row) is confusing
###
class @Loc

  #Constructs a location corresponding to row r, col c.
  constructor: (row, col) ->
    @row = row
    @col = col

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

  #A default location corresponding to nowhere
  @NOWHERE = new Loc(-9999, -9999)