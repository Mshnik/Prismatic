##= require ./hex

class Spark extends Hex

  ### Constructs a Spark and puts it into board b
     @param b - the board this spark belongs to
     @param l - the location of this spark in (row, col) in the board
     @param colors - the colors of this spark, in clockwise order starting with the top. Can be null, then set later.
     @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length == 0.
  ###
  constructor : (board, loc, colors) ->
    super(board, loc)
    @setAvailableColors(colors)


  ### Returns the current color of this spark ###
  getColor : () ->
    @avaliableColors.getColor()

  ### Makes this spark use the next avaliable color. Relights and redraws ###
  useNextColor : () ->
    @avaliableColors = @avaliableColors.getNext()
    @light()
    @update()
    return

  ### Returns the avaliable colors of this spark. ###
  getAvaliableColors : () ->
    @avaliableColors.toArray()

  ###Allows setting the ColorCircle, but only if it isn't set yet (is null).
      @throws IllegalArgumentException if the colorCircle is currently non-null
  ###
  setAvaliableColors : (colors) ->
    if(@avaliableColors != null) 
      throw new IllegalArgumentException("Can't set colorCirle of " + this);
    if(colors isnt null and colors.length is 0) 
      throw new IllegalArgumentException("Can't set color array of size " + colors.length);
    @avaliableColors = ColorCircle.fromArray(colors);
    @light();
    @update();
    return
  
  ### @Override
      Sparks always find light because they always light themselves. No setting of fields neccesary ###
  light : () ->
    @stopProvidingLight()
    @provideLight()
    return
  
  ### @Override
      Default behavior for a spark is to switch to the next avaliable color ###
  click : () ->
    @useNextColor()
    return
  
  ### @Override
      Overrides Hex isLit() because Sparks are always lit ###
  isLit : () ->
    l = []
    l.push(@getColor());
    return l