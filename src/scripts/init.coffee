### Functions that actually init the game ###

### Begins init processing ###
@init = -> 
  @initStart()

### Set up a PIXI stage - part before asset loading ###
@initStart = ->
  
  @stage = new PIXI.Stage(0x000000, true)
  margin = 0
  @renderer = PIXI.autoDetectRenderer(window.innerWidth - margin, window.innerHeight - margin)
  PIXI.scaleModes.DEFAULT = PIXI.scaleModes.NEAREST

  ## The menu and associated text/buttons/stuff
  @menu = new PIXI.DisplayObjectContainer()
  @stage.addChild(@menu)

  menuHeight = 100

  ##The container for the base hexes. The one that actually responds to clicks.
  @base = new PIXI.DisplayObjectContainer()
  @base.position.y = menuHeight
  @stage.addChild(@base)

  ## The off filter - creates lighting effects for all colors.
  @flat = new PIXI.ColorMatrixFilter()
  @flat.matrix = [0.5, 0, 0, 0,
                  0, 0.5, 0, 0,
                  0, 0, 0.5, 0,
                  0, 0, 0, 1]

  offset = 0
  ## Containers for elements to be colored. Two layers per color - lit and unlit 
  @colorContainers = {}
  ##The ordering of the colorContainers on the stage. Earlier = added earlier
  @colorContainersArr = []
  for c in Color.values()
    colr = c
    if(not isNaN(colr))
      colr = Color.asString(colr)
    cContainer = new PIXI.DisplayObjectContainer()
    cContainer.position.y = menuHeight
    cContainer.color = colr

    ## Add basic color filter to this colorContainer
    f = new PIXI.ColorMatrixFilter()
    f.matrix = Color.matrixFor(colr)
    cContainer.filters = [f]

    ## Create lit and unlit branches for this color Container
    unlit = new PIXI.DisplayObjectContainer()
    cContainer.addChild(unlit)
    lit = new PIXI.DisplayObjectContainer()
    cContainer.addChild(lit)
    
    cContainer.unlit = unlit
    if(colr isnt "NONE")
      cContainer.unlit.filters = [@flat]
    else
      cContainer.unlit.filters = null
    cContainer.lit = lit
    ## The pulse filter - creates lighting effects for all colors. Shouldn't favor a color, as it goes overtop other colorizing filters
    ## Initially flat - advanced in rendering steps
    pulse = new PIXI.ColorMatrixFilter()
    pulse.matrix = [1, 0, 0, 0,
                   0, 1, 0, 0,
                   0, 0, 1, 0,
                   0, 0, 0, 1]
    ## Create the length of the pulse for this color
    cContainer.lit.pulseLength = 173
    cContainer.lit.pulseOffset = offset
    offset += 70
    cContainer.lit.filters = [pulse]

    ##Set the index of this colorContainer
    @stage.addChild(cContainer)
    @colorContainers[colr] = cContainer
    @colorContainersArr.push(cContainer)

  ## Containers for goal elements. Only one layer per color - lit
  @goalContainer = new PIXI.DisplayObjectContainer()
  @goalContainer.count = 0  ## Set this later, once the board is loaded
  offset = 0
  for c in Color.values()
    colr = c
    if(not isNaN(colr))
      colr = Color.asString(colr)
    cContainer = new PIXI.DisplayObjectContainer()
    cContainer.position.y = menuHeight

    ## Add basic color filter and pulse to this colorContainer.
    ## Pulse only animated if this goal is met
    f = new PIXI.ColorMatrixFilter()
    f.matrix = Color.matrixFor(colr)
    pulse = new PIXI.ColorMatrixFilter()
    pulse.matrix = [1, 0, 0, 0,
                   0, 1, 0, 0,
                   0, 0, 1, 0,
                   0, 0, 0, 1]
    cContainer.filters = [f, pulse]
    @goalContainer[colr] = cContainer
    @goalContainer.addChild(cContainer)

  ## Container for help menu. Only actually added to stage when the help button is clicked ##
  @helpContainer = new PIXI.DisplayObjectContainer()
  preloadImages()
  return

### Load assets into cache ###
@preloadImages = ->
  assets = [@siteprefix + "assets/img/galaxy-28.jpg", @siteprefix + "assets/img/helpBackground.png", @siteprefix + "assets/img/Icon_v2.png",
            @siteprefix + "assets/img/hex-back.png", @siteprefix + "assets/img/spark.png", @siteprefix + "assets/img/crystal.png",
            @siteprefix + "assets/img/connector-adjacent.png", @siteprefix + "assets/img/connector-bridge.png", 
            @siteprefix + "assets/img/connector-far-neighbor.png", @siteprefix + "assets/img/connector-none.png", 
            @siteprefix + "assets/img/connector-opposite.png"]
  loader = new PIXI.AssetLoader(assets)
  loader.onComplete = @initFinish
  loader.load()
  return

### Detect when the window is resized - jquery ftw! ###
window.onresize = () ->
  window.resize()

### Finish initing after assets are loaded ###
@initFinish = ->
  Color.makeFilters()
  requestAnimFrame(window.animate)
  window.createHelpMenu()
  Board.loadBoard(window.BOARDNAME)
  return