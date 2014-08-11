##= require ./hex

class @Prism extends Hex

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
    @prevRotation = 0       ## The rotation this was in (side on top, after modding by 6)
    @currentRotation = 0    ## The rotation this is currently in (side on top, after modding by 6)
    @targetRotation = 0     ## The rotation this wants to be in (side on top, after moding by 6)
    @canLight = true        ## false while rotating, true otherwise

  ### Returns the colors of this prism, clockwise from the current top ###
  colorArray : () ->
    @colorCircle.toArray()

  ### Allows setting the ColorCircle, but only if it isn't set yet (is null).
     @throws IllegalArgumentException if the colorCircle is currently non-null ###
  setColorCircle : (colors) ->
    if(@colorCircle != undefined and @colorCircle != null and @colorCircle.length > 0)
      return
    @colorCircle = ColorCircle.fromArray(colors)
    return

  ## Rotates this prism once clockwise (moves head back). Also causes the prism to look for light and redraw itself ###
  rotate : () ->
    @board.moves++
    @colorCircle = @colorCircle.prev
    @targetRotation++
    #@light()
    return

  ## Rotates this prism once counter-clockwise (moves head forward). Also causes the prism to look for light and redraw itself ###
  rotateCounter : () ->
    @board.moves++
    @colorCircle = @colorCircle.next
    @targetRotation--
    #@light()
    return

  ### @Override
      Returns the colorCircle at the correct index for the color of a given side ###
  colorOfSide : (n) ->
    if(n < 0 || n > Hex.SIDES - 1) 
      throw ("Illegal Side Number " + n)
    return @colorCircle.toArray()[n]

  ### Returns the number of sides of this that are the specified color ###
  colorCount : (c) ->
    if not isNaN(c)
      c = Color.asString(c).toLowerCase()
    else
      c = c.toLowerCase()
    count = 0
    for col in @colorArray()
      if c is col
        count++
      else
    return count

  ### @Override
     Tries to find light by looking at all neighbor hexes that this isn't providing light to
     Tries to stay the same color of light if multiple are avaliable. Otherwise chooses arbitrarily.
     Returns the color this is lit at the end of the procedure, false otherwise ###
  light : () ->
    oldLit = @isLit()
    #Check if this can participate in lighting (would be false if this is currently rotating)
    if @canLight
      # Check for any lighters that can't provide light to this anymore
      @pruneLighters()
      # tell neighbors that they may not have light
      @stopProvidingLight()
      
      # Try to find new light, of any and all colors.
      @findLightProviders(Color.NONE)

      # Provide light to neighbors
      @provideLight()
    else
      #Get rid of all lighters
      @lighters = {}
      # tell neighbors they may not have light
      @stopProvidingLight()

    # Redraw (post recursion) and return the color this is now lit
    for c in @isLit
      if c not in oldLit
        @lightChange = true
    for c in oldLit
      if c not in @isLit
        @lightChange = true
    @update()
    return

  ### Default behavior for a Prism is to rotate. Rotates clockwise if ROTATE_CLOCKWISE, rotates counterclockwise otherwise. ###
  click : () -> 
    if(Prism.ROTATE_CLOCKWISE)
      @rotate()
    else
      @rotateCounter()

  ### Does the opposite of the default behavior. Mwa haha! ###
  anticlick : () ->
    if(not Prism.ROTATE_CLOCKWISE)
      @rotate()
    else
      @rotateCounter()
