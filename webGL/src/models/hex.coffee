##= require ./board
##= require ./colorCircle

### Abstract parent of all tiles ###
class Hex

  @SIDES = 6; #Sides per hex
  @NEIGHBOR_COORDINATES = [
   [new Loc(-1, 0), new Loc(-1, 1), new Loc(0, 1), new Loc(1, 0), new Loc(0, -1), new Loc(-1, -1)],
   [new Loc(-1, 0), new Loc(0, 1), new Loc(1,1), new Loc(1, 0), new Loc(1, -1), new Loc(0, -1)]
  ] #Location of neighbors in board, if they exist. These are actually vectors.
    #First array is for even col number, second is for odd col number.
    #Both begin with neighbor directly above a hex and go clockwise.

  ###Stores Board b and Point p as board and location in this hex.
     Throws IllegalArgumentException if b is null, point p is already occupied on board b,
     Or if the location is out of bounds.###
  constructor : (board, loc) ->
    try
      if(board == null)
        throw new IllegalArgumentException("Can't put hex into null board")
      if(b.getHex(loc) != null) 
        throw new IllegalArgumentException("Board " + board + " already has hex at position " +
            "(" + loc.row + "," + loc.col + "), can't construct new hex there.")
    catch a
      throw new IllegalArgumentException("Can't construct hex in " + board + " at " + loc  + ": " + a.getMessage())

    #Passes checks
      # Board this hex belongs to
    @board = board               
      # Location (row, col) of this hex on board                         
    @loc = loc
      # Locations of the neighbors of this hex. Locations never change, but the neighbors might
    @neighbors = 
      (for vec in Hex.NEIGHBOR_COORDINATES[loc.col %% 2]
        new Loc(loc.row + vec.row, loc.col + vec.col))
      # True if neighbors have been changed since last update, false otw
    @neighborsUpdated = true
      # Actual neighbor hexes. Calculated lazily
    @neighborHexes = []
      # Puts this hex in board
    @board.setHex(this, loc.row, loc.col)
      #Map of location (hex) -> color of hexes that provide this with light
    @lighters = []

  ###Returns the neighbors of this hex, clockwise from above. Will always return an array of lenght SIDES,
     but may contain nulls.
     Spots that this does not have a neighbor (off the board) are stored as null.
     Part of lazy calculation of neighborHexes - only updates if neighborsUpdated is true.
     Otherwise returns the (already calculated) neighborHexes ###
  getNeighborsWithBlanks : () ->
    if(neighborsUpdated)
      @neighborHexes =
        for l in @neighbors
          try
            board.getHex(l.row, l.col);
          catch e
            null
      neighborsUpdated = false
      return @neighborHexes
    else
      return @neighborHexes


  ###Returns the neighbors of this hex, clockwise from above, with nulls removed. 
     Thus no null elements, but resulting array has length 0 <= x <= SIDES ###
  getNeighbors : () ->
    a = @getNeighborsWithBlanks
    for h in a
      if( h not null)
        h
      else
        #Do Nothing

  indexLinked : (h) ->
    @board.indexLinked(this, h)

  colorLinked : (h) ->
    @board.colorLinked(this, h)

  ### Returns the colors this is lit. Returns empty if this isn't lit ###
  isLit : () ->
    (val for key, val of @lighters)

  ### Returns a set of locations (hexes) that all eventually provide light to this of a given color.
      Can be used to prevent cycles from forming. ###
  lighterSet : (c) ->
    arr = null
    for key, val of @lighters
      if (val == c)
        arr.push key
        arr.push k for k in key.lighterSet (c)
      else
    arr

  ### Fixes light for this hex. Must be implemented in subclasses. ###
  light : () -> throw new RuntimeException("Cannot instantiate Hex Class - light method must be overriden")

  ### Helper method for use in light implementations. Removes lighters that can't light this anymore from lighters map
      Returns true if at least one lighter was removed, false otherwise ###
  pruneLighters : () ->
    oldArr = (y for y, val of @lighters)
    for l in oldArr
      h = @board.getHex(l) # Look up corresponding hex
      c = @colorLinked(h)
      if( c == Color.NONE || c not in h.isLit())
        delete @lighters.l
    oldArr.length != Object.keys(@lighters).length

  ### Helper method for use in findLight implementations. Tells neighbors this is currently lighting to look elsewhere ###
  stopProvidingLight : () ->
    c = @isLit()
    for h in @getNeighbors()
      cond1 = @loc of h.lighters
      cond2 = h.lighters[@loc] not of c
      cond3 = colorLinked(h) not h.lighters[@loc] 
      if (cond1 and (cond2 or cond3))
        h.light()
    return

  ### Helper method for use in findLight implementations. Tells neighbors that this is now lit, 
     maybe get light from this, if not already or this getting light from that.
     If this isn't lit, do nothing. 
     
     Note: Always try to provide light to crystal, never try to provide light to spark. Neither of these recurse, so no trouble.
     Sparks can always provide light, others can only provide light if they have a lighter ###
  provideLight : () ->
    if (this instanceof Spark or (Object.keys(@lighters).length > 0))
      lit = @isLit()
      for h in getNeighbors()
        hLit = h.isLit()
        c = @colorLinked(h)
        if( ! (h instanceof Spark) and ((h instanceof Crystal and hlit.length == 0) or (h instanceof Prism and c of lit and ! c of hLit)))
          h.light()
    return

  ### Helper method for use in findLight implementations. Tries to find light among neighbors.
      If a link is found, sets that neighbor as lighter. If no link found, sets lighter to null.
      Only looks for preferred. If preferred is NONE, takes any color. ###
  findLightProvider : (preferred) ->
    for h in @getNeighbors()
      hLit = h.isLit()
      c = @colorLinked(h)
      if(c of hLit and (preferred is Color.NONE || preferred is c) and this not in h.lighterSet(c))
        lighters[h.loc] = c
    return

  ### Returns the color of side n of this hex (where side 0 is the top).
     IllegalArgumentException if n < 0, n > SIDES - 1 ###
  colorOfSide : (n) -> throw new RuntimeException("Cannot instantiate Hex Class - colorOfSide method must be overriden")

  ### Perform default behavior for interacting with this hex ###
  click : (n) -> throw new RuntimeException("Cannot instantiate Hex Class - click method must be overriden")

  ### Returns the location of this hex as the string for this hex ###
  toString : () -> @loc.toString()

  ### Two hexes are equal if their boards and their locations are the same ###
  equals : (o) ->
    if (! (o instanceof Hex)) 
      false
    else
      h = (Hex) o
      @board is h.board and @location is h.location


  ### Signifies that this has been changed; tells the game (if any) to update this as necessary. ###
  update : () -> 
    if((@board not null) and (@board.getGame() not null))
      @board.getGame().updateHex(this)
    return
