class @Board

  ### Constructor for an empty board of size rs*cs ###
  constructor : (rs, cs) ->
    if (rs < 0 || cs < 0) 
      throw ("Illegal Board Construction for Dimensions " + rs + ", " + cs)
    @height = rs
    @width = cs
    @board = [] # Board as a matrix of hexes
    for r in [0 .. rs - 1] by 1
      @board.push([])
      for c in [0 .. cs - 1] by 1
        @board[r].push(null)
    @allHexesByClass = {} ## Lists of hexes by their class (a string)
    @game = null # Game this board belongs to

  ### Returns the height of this board ###
  getHeight : () -> @height

  ### Returns the width of this board ###
  getWidth : () -> @width

  ### Returns the game this board belongs to (if any) ###
  getGame : () -> @game

  ### Sets the game this board belongs to. Throws a runtime exception if game is already set ###
  setGame : (g) -> 
    if(@game != null)
      throw ("Can't set Game of " + this + " to " + g + " because it is already " + game)
    @game = g
    return

  ### Gets rid of this board - signifies that it is no longer used ###
  dispose : () ->
    @game = null
    @board = null
    return 

  ### Returns the index (0 ... Hex.SIDES - 1) of the side of h1 that is facing h2. 
      Returns -1 if either are null or are not neighbors ###
  indexLinked : (h1, h2) ->
    if(h1 is null or h2 is null)
      return -1

    h1Neighbors = h1.getNeighborsWithBlanks()
    for i in [0 .. Hex.SIDES] by 1
      if (h2 is h1Neighbors[i])
        return i

    return -1


  ### Returns the color that links h1 and h2. 
      1) The two hexes are neighbors (both non-null), otherwise returns none
      2) The colors of the adjacent sides are the same. Treats Color.any as a wild card.
      If one is color.any and the other isn't, returns the more specific one.
  ###
  colorLinked : (h1, h2) ->
    index = @indexLinked(h1, h2)
    if (index is -1)
      return Color.NONE 
    c1 = h1.colorOfSide(index)
    c2 = h2.colorOfSide( (index + Hex.SIDES / 2) %% Hex.SIDES)
    if c1 is Color.ANY and c2 is Color.ANY
      return Color.ANY
    else if c1 is Color.ANY
      return c2
    else if c2 is Color.ANY
      return c1
    else if c1 is c2
      return c1
    else
      return Color.NONE

  ### Returns the hex at the given location ###
  getHex : (loc) ->
    try
      @board[loc.row][loc.col]
    catch e ## Maybe wrapped in string?
      loc = Loc.fromString(loc)
      @board[loc.row][loc.col]

  ### Returns a flattened version of the board - all hexes in no particular order ###
  allHexes : () ->
    arr = []
    for key, value in @allHexesByClass
      for h in value
        arr.push(h)

    return arr

  ### Returns all hexes in the board that are of a particular class. Returns an empty array if no such class ###
  allHexesOfClass : (cl) ->
    for key, value in @allHexesByClass
      if key.toLowerCase is cl.toLowerCase
        return value
    return []

  ### Returns the colors present on this board, looking at the sparks. Only includes regular colors ###
  colorsPresent : () ->
    arr = []
    for spark in allHexesOfClass("Spark")
      for c in spark.getAvailableColors()
        if c not in arr and Color.isRegularColor(c)
          arr.push(c)
    return arr

  ### Returns a map of color -> int that is the sides of prisms that are the color ###
  colorCount : () ->
    o = {}
    for prism in allHexesOfClass("Prism")
      for c in Color.values()
        if(c not of o)
          o[c] = prism.colorCount(c)
        else
          o[c] = o[c] + prism.colorCount(c)
    return o

  ### Returns all prisms that have at least one side with color.ANY on it. Useful for finding part of the board not finished ###
  allPrismsWithAny : () ->
    for prism in allHexesOfClass("Prism")
      if prism.colorCount(Color.ANY) >= 1
        prism
      else
        #Do Nothing


  ### Sets the hex at position (r,c). Also sets all neighbor hexes as needing a neighbor update.
      Hex must have this as its board. Used in hex construciton, not much elsewhere ###
  setHex : (h, l) ->
    if(h.board isnt this)
      throw ("Can't put hex belonging to " + h.board + " in board " + this)

    @board[l.row][l.col] = h

    ## Add to correct collection by class
    cl = 
    if h instanceof Prism 
      "Prism"
    else if h instanceof Spark
      "Spark"
    else if h instanceof Crystal
      "Crystal"
    else
      "Unknown"

    if cl not of @allHexesByClass
      @allHexesByClass[cl] = []

    @allHexesByClass[cl].push(h)

    for n in h.getNeighbors()
      n.neighborsUpdated = true

    return

  ### Re-calculates light on whole board - tells each spark to light itself and give light out.
     This should hit the whole board. ###
  relight : () ->
    for h in @allHexes()
      h.lighters = []
    for h in @allHexesOfClass("Spark")
      h.light()
    return

  ### Two boards are equal if they have the same board ###
  equals : (o) ->
    if (! (o instanceof Board))
      return false

    b = (Board) o
    b.board == @board

  ### Returns a string representation of this board as an ascii matrix ###
  toString : () ->
    s = ""
    for r in @board
      for el in r
        if(el is null)
          s = s + "   |"
        else if (el instanceof Prism)
          s = s + " p |"
        else if (el instanceof Spark)
          s = s + " s |"
        else
          s = s + " c |"
      s = s + "\n"
      for el in r 
        s = s + "----"
      s = s + "\n"
    return s

  ### Makes a random board with the given dimentions. Mainly for testing. Sparks/Crystals at corners, prisms otw ###
  @makeBoard = (rs, cs, cls) ->
    b = new Board(rs,cs);
    for r in [0 .. rs - 1] by 1
      for c in [0 .. cs - 1] by 1
        if r is 0 and c is 0 or r is (rs-1) and c is 0
          new Spark(b, new Loc(r,c), Color.subValues(1, cls-1))
        else if r is 0 and c is (cs - 1) or r is (rs-1) and c is (cs - 1)
          new Crystal(b, new Loc(r, c))
        else
          new Prism(b, new Loc(r, c), ColorCircle.randomArray(Hex.SIDES, cls))
    #b.relight()    
    return b

  ## Makes a board filled entirely with any prisms - good for testing, etc ###
  @anyBoard = (rs, cs) ->
    b = new Board(rs, cs)
    for r in [0 .. rs - 1] by 1
     for c in [0 .. cs - 1] by 1
      new Prism(b, new Loc(r, c), Color.fill(Hex.SIDES, Color.ANY))
    return b

  ### TODO: ALGORITHM STUFF. ### 