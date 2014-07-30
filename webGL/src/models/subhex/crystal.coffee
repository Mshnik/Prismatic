class @Crystal extends Hex

  ### Constructs a Crystal and puts it into board b
      @param b - the board this spark belongs to
      @param l - the location of this crystal in (row, col) in the board
      @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB.
  ###
  constructor : (board, loc) ->
    super(board, loc)
    @lit = Color.NONE   ## Single color this is lit. Only changed at end of recalculating light
    @canLight = false   ## Can never provide light
    @toColor = ""       ## Color to change this to on next redraw. Empty if current color is ok


  ### @Override
      Try to find light like a prism, but don't ever provide light. Thus only look for a provider, don't need to recurse.
      Only find single light color. ###
  light : () ->
    lighterChanged = @pruneLighters()
    #First try to find a provider of the previous color of light
    if(lighterChanged or @lit is Color.NONE or @lit is Color.asString(Color.NONE))
      @findLightProviders(@lit)
      if(@isLit().length == 0)
        @findLightProviders(Color.NONE)
    
    #Update the color this is lit
    if(@isLit().length == 0)
      @lit = Color.NONE;
    else
      @lit = @isLit()[0]

    ##Redraw 
    @toColor = @lit
    @update()
    return

  ### @Override
      Helper method for use in findLight implementations. Tries to find light among neighbors.
      Overrides hex findLightProvider so that it can take any color of light, no matter the side color.
      Only looks for preferred. If preferred is NONE, takes any color. Only takes on e color ###
  findLightProviders : (preferred) ->
    for h in @getNeighbors()
      hLit = h.isLit()
      c = h.colorOfSide(h.indexLinked(this))
      if( not (h instanceof Crystal) and (hLit.length > 0 and (preferred is Color.NONE or preferred in hLit)) and c not in @isLit() and c in hLit)
        @lighters[h.loc.toString()] = c        
    return
  
  ### @Override
      All sides of this crystal are the color of its lighter. (Not that this can provide light) ###
  colorOfSide : (n) -> 
    if(n < 0 || n > Hex.SIDES - 1) 
      throw ("Can't find color of side " + n + " of " + this)
    if @lit?
      return @lit
    else
      return Color.NONE

  ### Interacting with a Crystal does nothing - do nothing here ###
  click : () -> return

