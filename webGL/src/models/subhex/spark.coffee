##= require ./hex

class @Spark extends Hex

  ### Constructs a Spark and puts it into board b
     @param b - the board this spark belongs to
     @param l - the location of this spark in (row, col) in the board
     @param colors - the colors of this spark, in clockwise order starting with the top. Can be null, then set later.
     @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length == 0.
  ###
  constructor : (board, loc, colors) ->
    super(board, loc)
    @setAvailableColors(colors)
    @toColor = ""       ## Color to change this to on next redraw. Empty if current color is ok

  ### Returns the current color of this spark ###
  getColor : () ->
    @availableColors.getColor()

  ### Makes this spark use the next available color. Relights and redraws ###
  useNextColor : () ->
    @availableColors = @availableColors.getNext()
    @light()
    @update()
    return

  ### Returns the available colors of this spark. ###
  getAvailableColors : () ->
    @availableColors.toArray()

  ###Allows setting the ColorCircle, but only if it isn't set yet (is null). Don't call otherwise
  ###
  setAvailableColors : (colors) ->
    if(@availableColors != undefined and @availableColors != null and @availableColors.length > 0)
      return
    @availableColors = ColorCircle.fromArray(colors);
    return
  
  ### @Override
      Sparks always find light because they always light themselves. No setting of fields neccesary ###
  light : () ->
    @stopProvidingLight()
    @provideLight()
    return
  
  ### @Override
      Default behavior for a spark is to switch to the next available color ###
  click : () ->
    @useNextColor()
    @toColor = @getColor()
    return
  
  ### @Override
      Overrides Hex isLit() because Sparks are always lit ###
  isLit : () ->
    l = []
    l.push(@getColor());
    return l

  ### @Override
      Overrides Hex colorOfSide(), all color of lit ###
  colorOfSide : (n) ->
    return @getColor()