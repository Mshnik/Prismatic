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
  offset = 0

  ## The dummy texture that takes up the whole screen to make sure that color layers don't resize
  g = new PIXI.Graphics()
  g.clear()
  g.lineStyle(0, 0xFFFFFF, 1)
  g.drawRect(0,0,window.innerWidth, window.innerHeight)
  dumTex = g.generateTexture()
  @stage.addChild(new PIXI.Sprite(dumTex))

  ## Containers for goal elements. Only one layer per color - lit
  @goalContainer = new PIXI.DisplayObjectContainer()
  @goalContainer.count = 0  ## Set this later, once the board is loaded

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
  requestAnimFrame(window.animate)
  window.createHelpMenu()
  Board.loadBoard(window.BOARDNAME)
  return