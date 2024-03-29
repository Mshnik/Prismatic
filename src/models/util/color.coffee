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


  ## Color manipulation filters. Creating many of the same is redundant. Built below
  @filters = {}

  @count : () -> Object.keys(@_val).length

  @values : () -> Object.keys(@_val)

  ### Returns a subArray of REGULAR colors, starting at color n and giving l colors. Caps at the available number of regular colors ###
  @subValues : (n) ->
    c = Color.count()
    len = Math.min(n, c - @SPECIAL_OFFSET)
    Color.values().splice(@SPECIAL_OFFSET, len)

  ### Returns the regular colors ###
  @regularColors : () -> @subValues(Number.MAX_VALUE)

  ## Returns true if the given color is a regular color, false otherwise
  @isRegularColor : (c) ->
    if isNaN(c)
      c.toUpperCase() in @regularColors()
    else
      Color.asString(c).toUpperCase() in @regularColors()

  ### Returns an array of length length filled with color col ###
  @fill : (length, col) ->
    col for i in [0 ... (length - 1)] by 1


  ### Returns a hex value for a lit color. Unused for the time being ###
  @hexValueForLit : (color) ->
    c = 
      if isNaN(color)
        @fromString(color)
      else
        color
    switch c
      when @RED
        return 0xFF3300
      when @BLUE
        return 0x3399FF
      when @GREEN
        return 0x66FF66
      when @ORANGE
        return 0xFF9900
      when @PURPLE
        return 0x9966FF
      when @CYAN
        return 0x66FFFF
      when @YELLOW
        return 0xFFFF66
      when @PINK
        return 0xFF66CC
      else ## Returns a muted gray for other color
        return 0xCCCCCC

  ### Returns a hex value for an unlit color. Unused for the time being ###
  @hexValueForUnlit : (color) ->
    c = 
      if isNaN(color)
        @fromString(color)
      else
        color
    switch c
      when @RED
        return 0x801A00
      when @BLUE
        return 0x1F5C99
      when @GREEN
        return 0x3D993D
      when @ORANGE
        return 0x804C00
      when @PURPLE
        return 0x4C3380
      when @CYAN
        return 0x3D9999
      when @YELLOW
        return 0x99993D
      when @PINK
        return 0xB2478F
      else ## Returns a muted gray for other color
        return 0xCCCCCC


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
    if color?
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
    else
      return @NONE