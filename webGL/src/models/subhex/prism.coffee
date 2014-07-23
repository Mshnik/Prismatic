##= require ./hex

class Prism extends Hex

  ## Defines the default rotation direction for prisms - clockwise if true, counterclockwise otw
  @ROTATE_CLOCKWISE = true

  ### Constructs a Prism and puts it into board b
     @param b - the board this prism belongs to
     @param l - the location of this prism in (row, col) in the board
     @param colors - the colors of this prism, in clockwise order starting with the top. Can be null, then set later.
     @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length != SIDES.
  ###
  constructor : (board, loc, colors) ->
    super(board, loc)
    @setColorCircle(colors)

  ### Returns the colors of this prism, clockwise from the current top ###
  colorArray : () ->
    colorCircle.toArray()

  ### Allows setting the ColorCircle, but only if it isn't set yet (is null).
     @throws IllegalArgumentException if the colorCircle is currently non-null ###
  setColorCircle : (colors) ->
    if(colorCircle != null) 
      throw new IllegalArgumentException("Can't set colorCirle of " + this.toString())
    if(colors != null && colors.length != Hex.SIDES) 
      throw new IllegalArgumentException("Can't set color array of size " + colors.length)
    @colorCircle = ColorCircle.fromArray(colors)
    return

  ## Rotates this prism once clockwise (moves head back). Also causes the prism to look for light and redraw itself ###
  rotate : () ->
    @board.moves++
    @colorCircle = @colorCircle.getPrevious()
    @light()
    return

  ## Rotates this prism once counter-clockwise (moves head forward). Also causes the prism to look for light and redraw itself ###
  rotateCounter : () ->
    @board.moves++
    @colorCircle = @colorCircle.getNext()
    @light()
    return

  ### @Override
      Returns the colorCircle at the correct index for the color of a given side ###
  colorOfSide : (n) ->
    if(n < 0 || n > Hex.SIDES - 1) 
      throw new IllegalArgumentException ("Illegal Side Number " + n)
    return @colorCircle.toArray()[n]

  ### @Override
     Tries to find light by looking at all neighbor hexes that this isn't providing light to
     Tries to stay the same color of light if multiple are avaliable. Otherwise chooses arbitrarily.
     Returns the color this is lit at the end of the procedure, false otherwise ###
  light : () ->
    # Check for any lighters that can't provide light to this anymore
    @pruneLighters()
    # tell neighbors that they may not have light
    @stopProvidingLight()
    
    # Try to find new light, of any and all colors.
    @findLightProviders(Color.NONE)

    # Provide light to neighbors
    @provideLight()

    # Redraw (post recursion) and return the color this is now lit
    @update()
    return

  ### Default behavior for a Prism is to rotate. Rotates clockwise if ROTATE_CLOCKWISE, rotates counterclockwise otherwise. ###
  click : () -> 
    if(Prism.ROTATE_CLOCKWISE)
      @rotate()
    else
      @rotateCounter()
    return