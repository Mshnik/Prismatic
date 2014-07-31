##= require ./board
##= require ./colorCircle

### Abstract parent of all tiles ###
class @Hex

  @SIDES = 6; #Sides per hex
  @NEIGHBOR_COORDINATES = [
   [new Loc(-1, 0), new Loc(-1, 1), new Loc(0, 1), new Loc(1, 0), new Loc(0, -1), new Loc(-1, -1)],
   [new Loc(-1, 0), new Loc(0, 1), new Loc(1,1), new Loc(1, 0), new Loc(1, -1), new Loc(0, -1)]
  ] #Location of neighbors in board, if they exist. These are actually vectors.
    #First array is for even col number, second is for odd col number.
    #Both begin with neighbor directly above a hex and go clockwise.

  ### Stuff for communicating with swing version, reading/writing boards ###
  @TYPE_KEY = "\"Type\""
  @LOCATION_KEY = "\"Loc\""
  @COLORS_KEY = "\"Colors\""

  ###Stores Board b and Point p as board and location in this hex.
     Throws IllegalArgumentException if b is null, point p is already occupied on board b,
     Or if the location is out of bounds.###
  constructor : (board, loc) ->
    try
      if(board == null)
        throw ("Can't put hex into null board")
      if(board.getHex(loc) != null) 
        throw ("Board " + board + " already has hex at position " +
            "(" + loc.row + "," + loc.col + "), can't construct new hex there.")
    catch a
      throw ("Can't construct hex in " + board.toString + " at " + loc  + ": " + a.message)

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
    @board.setHex(this, loc)
    @canLight = true ## True if this hex can participate in lighting at all, false otw.
      #Map of location (hex) -> color of hexes that provide this with light
    @lighters = {}
      # True if this changed lighting since the last frame update, false otherwise
    @lightChange = false

  ###Returns the neighbors of this hex, clockwise from above. Will always return an array of lenght SIDES,
     but may contain nulls.
     Spots that this does not have a neighbor (off the board) are stored as null.
     Part of lazy calculation of neighborHexes - only updates if neighborsUpdated is true.
     Otherwise returns the (already calculated) neighborHexes ###
  getNeighborsWithBlanks : () ->
    if(@neighborsUpdated)
      @neighborHexes =
        for l in @neighbors
          try
            @board.getHex(l);
          catch e
            null
      @neighborsUpdated = false
      return @neighborHexes
    else
      return @neighborHexes


  ###Returns the neighbors of this hex, clockwise from above, with nulls removed. 
     Thus no null elements, but resulting array has length 0 <= x <= SIDES ###
  getNeighbors : () ->
    a = @getNeighborsWithBlanks()
    for h in a
      if(h isnt undefined and h isnt null)
        h
      else
        #Do Nothing

  indexLinked : (h) ->
    @board.indexLinked(this, h)

  colorLinked : (h) ->
    @board.colorLinked(this, h)

  ### Looks at all neighbors, and finds a list of neighbors that could be usefully powered by this were they rotated.
      This requires the neighbor to have at least two of color C on it, and that this has the correct color facing that neighbor.
      Shouldn't check if anything requiring this to rotate.
      Also doesn't return any neighbors that are part of this' lighter set.
     
      Only returns prisms.
   
    Does not actually rotate any hexes or modify the board in any way. ###
  children : (c) ->
    for n in getNeighbors()
      if n not in lighterSet(c) and n instanceof Prism and h.asPrism().colorCount(c) >= 2 and c is colorOfSide(indexLinked(n))
        n
      else
        # Add nothing

  ###Turns this hex on or off. Turning it off means it doesn't participate in lighting (provide or take). ###
  turn : (state) ->
    @canLight = state
    return

  ### Returns the colors this is lit. Returns empty if this isn't lit ###
  isLit : () ->
    (val for key, val of @lighters)

  ### Returns the hex that is currently directly providing this with light. Returns null if no such hex for color c ####
  lighter : (c) ->
    for key, val of @lighters 
      if(val == c)
        return @board.getHex(Loc.fromString(key))
    return null

  ### Returns a set of locations (hexes) that all eventually provide light to this of a given color.
      Can be used to prevent cycles from forming. ###
  lighterSet : (c) ->
    arr = []
    for key, val of @lighters
      if (val == c)
        arr.push key
        arr.push k for k in @board.getHex(Loc.fromString(key)).lighterSet (c)
      else
    arr

  ### Fixes light for this hex. Must be implemented in subclasses. ###
  light : () -> throw ("Cannot instantiate Hex Class - light method must be overriden")

  ### Helper method for use in light implementations. Removes lighters that can't light this anymore from lighters map
      Returns true if at least one lighter was removed, false otherwise ###
  pruneLighters : () ->
    oldArr = (y for y, val of @lighters)
    for l in oldArr
      h = @board.getHex(l) # Look up corresponding hex
      c = @colorLinked(h)
      if !isNaN(c)
       c = Color.asString(c)
      if((not h.canLight) || c == Color.asString(Color.NONE) || c not in h.isLit() || this in h.lighterSet(c))
        delete @lighters[l]
    oldArr.length != Object.keys(@lighters).length

  ### Helper method for use in findLight implementations. Tells neighbors this is currently lighting to look elsewhere ###
  stopProvidingLight : () ->
    c = @isLit()
    for i in [0 .. (c.length - 1)] by 1
      if !isNaN(c[i])
        c[i] = Color.asString(c)
    for h in @getNeighbors()
      cond1 = @loc of h.lighters
      cond2 = h.lighters[@loc] not in c
      cond3 = @colorLinked(h) isnt h.lighters[@loc] 
      cond4 = not h.canLight
      if (cond1 and (cond2 or cond3 or cond4))
        h.light()
    return

  ### Helper method for use in findLight implementations. Tells neighbors that this is now lit, 
     maybe get light from this, if not already or this getting light from that.
     If this isn't lit, do nothing. 
     
     Note: Always try to provide light to crystal, never try to provide light to spark. Neither of these recurse, so no trouble.
     Sparks can always provide light, others can only provide light if they have a lighter ###
  provideLight : () ->
    if (@canLight and (this instanceof Spark or (Object.keys(@lighters).length > 0)))
      lit = @isLit()
      for h in @getNeighbors()
        hLit = h.isLit()
        c = @colorLinked(h)
        if !isNaN(c)
          c = Color.asString(c)
        if( ! (h instanceof Spark and lit.length is 0) and ((h instanceof Crystal and hLit.length == 0) or (h instanceof Prism and c in lit and c not in hLit)))
          h.light()
    return

  ### Helper method for use in findLight implementations. Tries to find light among neighbors.
      If a link is found, sets that neighbor as lighter. If no link found, sets lighter to null.
      Only looks for preferred. If preferred is NONE, takes any color. Doesn't take the same color twice. ###
  findLightProviders : (preferred) ->
    for h in @getNeighbors()
      hLit = h.isLit()
      c = @colorLinked(h)
      if !isNaN(c)
        c = Color.asString(c)
      if(h.canLight and c in hLit and (preferred is Color.NONE or preferred is c) and c not in @isLit() and (not h.lighterSet(c)? or this not in h.lighterSet(c)))
        @lighters[h.loc.toString()] = c
    return

  ### Returns the color of side n of this hex (where side 0 is the top).
     IllegalArgumentException if n < 0, n > SIDES - 1 ###
  colorOfSide : (n) -> ("Cannot instantiate Hex Class - colorOfSide method must be overriden")

  ### Perform default behavior for interacting with this hex ###
  click : () -> ("Cannot instantiate Hex Class - click method must be overriden")

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
    if((@board isnt null) and (@board.getGame() isnt null))
      @board.getGame().updateHex(this)
    return
