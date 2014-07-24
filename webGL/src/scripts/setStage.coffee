### Begins init processing ###
@init = -> 
  @initStart()

### Set up a PIXI stage - part before asset loading ###
@initStart = ->
  
  @stage = new PIXI.Stage(0x295266)
  canvas = document.getElementById("game-canvas")
  renderer = PIXI.autoDetectRenderer(canvas.width, canvas.height, canvas)
  @animate = () ->
    requestAnimFrame( @animate )
    renderer.render(@stage)
    return
  requestAnimFrame( @animate )
  PIXI.scaleModes.DEFAULT = PIXI.scaleModes.NEAREST
# stage = new PIXI.Stage(0x66FF99);
#   renderer = PIXI.autoDetectRenderer(400, 300);
#   document.body.appendChild(renderer.view);
  
  # @hexPanel = new PIXI.DisplayObjectContainer()
  # @stage.addChild(@hexPanel)

  # #Add a dummy sprite - not working. Why?
  # d = PIXI.Texture.fromImage("assets/img/circle_blue.png")
  # dS = new PIXI.Sprite(d)
  # dS.position.x = 200
  # dS.position.y = 200
  # @hexPanel.addChild(dS)

  # @renderer.render(stage);
  preloadImages()
  return

### Load assets into cache ###
@preloadImages = ->
  assets = ["assets/img/hex-back.png"]
  loader = new PIXI.AssetLoader(assets)
  loader.onComplete = @initFinish
  loader.load()
  return

### Finish initing after assets are loaded ###
@initFinish = ->
  window.createDummyBoard()
  window.drawBoard()
  return


### Creates a dummy board and adds to scope. Mainly for testing ###
@createDummyBoard = () ->
  @BOARD = @Board.makeBoard(4,9,3)
  return


### Draws the Board in BOARD on the stage. ###
@drawBoard = () ->
 for h in @BOARD.allHexes()
  @spriteForHex(h)
 return

@hexRad = 53

### Creates a single sprite for a hex and adds it to stage ###
@spriteForHex = (hex) ->
  if typeof hex.sprite is "undefined" or hex.sprite is null 

    spr = PIXI.Sprite.fromImage("assets/img/hex-back.png")
    spr.anchor.x = 0.5
    spr.anchor.y = 0.5

    spr.scale.x = 0.078
    spr.scale.y = 0.078

    # tex.setFrame(new PIXI.Rectangle(0,0,@hexRad * 2, @hexRad * 2))

    spr.position.x = hex.loc.col * @hexRad * 3/4 * 1.11 + @hexRad/2
    spr.position.y = hex.loc.row * @hexRad + @hexRad/2
    spr.position.y +=  @hexRad/2 if hex.loc.col % 2 == 1

    # Center the pivot
    spr.pivot.x = 0.5
    spr.pivot.y = 0.5

    @stage.addChild(spr)

    # Store in hex for later
    hex.sprite = spr
  return hex.sprite