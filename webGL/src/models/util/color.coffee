class @Color

  # Actual colors - kinda an enum object.
  @_val = 
    NONE : 0
    RED : 1
    BLUE : 2
    GREEN : 3
    ORANGE : 4
    PURPLE : 5
    CYAN : 6
    YELLOW : 7
    PINK : 8

  @NONE = @_val.NONE
  @RED = @_val.RED
  @BLUE = @_val.BLUE
  @GREEN = @_val.GREEN
  @ORANGE = @_val.ORANGE
  @PURPLE = @_val.PURPLE
  @CYAN = @_val.CYAN
  @YELLOW = @_val.YELLOW
  @PINK = @_val.PINK

  @count : () -> Object.keys(@_val).length

  @values : () -> Object.keys(@_val)

  @subValues : (offset, n) ->
    c = Color.count()
    Color.values().splice(offset %% c, (offset + n) %% c)

  @noneArray : (length) ->
    Color.NONE for i in [0 ... (length - 1)] by 1

  @asString : (color) ->
    switch color
      when 1
        return "red"
      when 2
        return "blue"
      when 3
        return "green"
      when 4
        return "orange"
      when 5
        return "purple"
      when 6
        return "cyan"
      when 7
        return "yellow"
      when 8
        return "pink"
      else
        return "none"