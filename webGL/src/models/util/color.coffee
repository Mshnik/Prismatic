class @Color

  # Actual colors - kinda an enum object.
  @_val = 
    NONE : 0
    ANY : 1
    RED : 2
    BLUE : 3
    GREEN : 4
    ORANGE : 5
    PURPLE : 6
    CYAN : 7
    YELLOW : 8
    PINK : 9

  @NONE = @_val.NONE
  @ANY = @_val.ANY
  @RED = @_val.RED
  @BLUE = @_val.BLUE
  @GREEN = @_val.GREEN
  @ORANGE = @_val.ORANGE
  @PURPLE = @_val.PURPLE
  @CYAN = @_val.CYAN
  @YELLOW = @_val.YELLOW
  @PINK = @_val.PINK

  @SPECIAL_OFFSET = 2 ## Number of special colors - NONE and ANY

  @count : () -> Object.keys(@_val).length

  @values : () -> Object.keys(@_val)

  ### Returns a subArray of REGULAR colors, starting at color n and giving l colors. Caps at the available number of regular colors ###
  @subValues : (n) ->
    c = Color.count()
    len = Math.min(n, c - @SPECIAL_OFFSET)
    Color.values().splice(@SPECIAL_OFFSET, len)

  ### Returns the regular colors ###
  @regularColors : () -> @subValues(Number.MAX_VALUE)

  ### Returns an array of length length filled with color col ###
  @fill : (length, col) ->
    col for i in [0 ... (length - 1)] by 1

  @asString : (color) ->
    switch color
      when @ANY
        return "any"
      when @RED
        return "red"
      when @BLUE
        return "blue"
      when @GREEN
        return "green"
      when @ORANGE
        return "orange"
      when @PURPLE
        return "purple"
      when @CYAN
        return "cyan"
      when @YELLOW
        return "yellow"
      when @PINK
        return "pink"
      else
        return "none"

  @fromString : (color) ->
    switch color.toLowerCase()
      when "any"
        return @ANY
      when "red"
        return @RED
      when "blue"
        return @BLUE
      when "green"
        return @GREEN
      when "orange"
        return @ORANGE
      when "purple"
        return @PURPLE
      when "cyan"
        return @CYAN
      when "yellow"
        return @YELLOW
      when "pink"
        return @PINK
      else
        return @NONE