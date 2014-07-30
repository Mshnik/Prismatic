##= require ./color

class ColorCircle

  ### Creates a circularly linked list of colorCircles from an array of colors.
      Returns the first colorCircle (head). Returns null if the input is null or length 0 ###
  @fromArray : (colors) -> 
    if(colors is null or colors.length is 0)
      return null
    l = colors.length

    ##Convert to contstants if they came in as strings
    # colorsFixed = ((if isNaN(c) then Color.fromString(c) else c) for c in colors)
    t = (new ColorCircle(c, null, null) for c in colors)
    for i in [0 .. (l - 1)] by 1
      cc = t[i]
      cc.size = l
      cc.prev = t[(i-1) %% l]
      cc.next = t[(i+1) %% l]
    t[0]

  ### Returns a random color array of a given length. Uses at most maxColors (or 6 if maxColors > 6) colors, never uses NONE.
      throws IllegalArgumentException if length <= 0 or maxColors <= 0 ###
  @randomArray : (length, maxColors) -> 
    if (length <= 0) 
      throw ("Can't make Color Array of length " + length + " for color circle")
    if(maxColors <= 0) 
      throw ("Can't make Color Array of length using at most " + maxColors + " colors")
    m = Math.min(maxColors, Color.regularColors().length)
    a = for i in [0 .. (length-1)] by 1
      Color.regularColors()[Math.floor(Math.random() * m)]
    a

  ### Constructs a color circle with the given inputs. Should not be used outside of this file - use helpers ###
  constructor : (col, prev, next) ->
    @color = col  ## This' color
    @prev = prev  ## previous link in the circle
    @next = next  ## Next link in the circle
    @size = 0     ## Size of the circle this is in

  ### Returns the color of this link ###
  getColor : () -> @color

  ### Returns the next link ###
  getNext : () -> @next

  ### Returns the previous link ###
  getPrevious : () -> @previous

  ### Returns the size of this circle ###
  getSize : () -> @size

  ### Converts this to an array of colors ###
  toArray : () ->
    arr = []
    a = this
    loop
      arr.push(a.color)
      a = a.next
      break if (a is this)
    return arr


  ### Returns a string representation of the colorCircle starting with this ###
  toString : () -> @toArray.toString()

  ### Two color circles are equal if their sizes are equald and they have the same color at every position ###
  equals : (o) ->
    if (! (o instanceof ColorCircle)) 
      return false
    c = (ColorCircle) o
    d = this
    if(@size != c.size) 
      return false
    loop
      if(c.color != d.color) 
        return false
      c = c.next;
      d = d.next;
      break if (d is this)
    return true