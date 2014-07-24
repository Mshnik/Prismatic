### Begins init processing ###
@init = -> 
  @initStart()

### Set up a PIXI stage - part before asset loading ###
@initStart = ->
  
  @stage = new PIXI.Stage(0x295266, true)
  @stage.scale.x = 0.5
  @stage.scale.y = 0.5
  canvas = document.getElementById("game-canvas")
  @renderer = PIXI.autoDetectRenderer(canvas.width, canvas.height, canvas)
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
  PIXI.scaleModes.DEFAULT = PIXI.scaleModes.LINEAR
  preloadImages()
  return

### Load assets into cache ###
@preloadImages = ->
  assets = ["assets/img/hex-back.png", "assets/img/hex-lit.png", "assets/img/circle_blue.png", 
            "assets/img/circle_red.png", "assets/img/circle_green.png"]
  loader = new PIXI.AssetLoader(assets)
  loader.onComplete = @initFinish
  loader.load()
  return

### Animates the board and requests another frame ###


### Finish initing after assets are loaded ###
@initFinish = ->
  animate = () ->
    rotSpeed = 1/10
    tolerance = 0.000001 ## For floating point errors - difference below this is considered 'equal'
    radTo60Degree = 1.04719755 ## 1 rad * this = 60 degrees
    if (@BOARD?)
      for h in @BOARD.allHexes()
        ## Update lighting of all hexes
        if h.isLit().length > 0 and not h.panel.children[0].lit
          h.panel.children[0].texture = PIXI.Texture.fromImage("assets/img/hex-lit.png")
          h.panel.children[0].lit = true
        if h.isLit().length is 0 and h.panel.children[0].lit
          h.panel.children[0].texture = PIXI.Texture.fromImage("assets/img/hex-back.png")
          h.panel.children[0].lit = false
        ### Rotation of a prism - finds a prism that wants to rotate and rotates it a bit.
            If this is the first notification that this prism wants to rotate, stops providing light.
            If the prism is now done rotating, starts providing light again ###
        if h instanceof Prism and h.currentRotation isnt h.targetRotation
          if h.canLight
            h.canLight = false
            h.light()
          inc = (h.targetRotation - h.prevRotation) * rotSpeed
          h.panel.rotation += inc * radTo60Degree
          h.currentRotation += inc 
          if Math.abs(h.targetRotation - h.currentRotation) < tolerance
            inc = (h.targetRotation - h.currentRotation)
            h.panel.rotation += inc * radTo60Degree
            h.currentRotation += inc
            h.prevRotation = h.currentRotation
            h.canLight = true
            h.light()
    requestAnimFrame(animate )
    @renderer.render(@stage)
    return
  requestAnimFrame(animate )
  window.createDummyBoard()
  window.drawBoard()
  return


### Creates a dummy board and adds to scope. Mainly for testing ###
@createDummyBoard = () ->
  @BOARD = @Board.makeBoard(4,9,3)
  @BOARD.relight()
  return


### Draws the Board in BOARD on the stage. ###
@drawBoard = () ->
 for h in @BOARD.allHexes()
  @createSpriteForHex(h)
 return

@hexRad = 53

### Creates a single sprite for a hex and adds it to stage ###
@createSpriteForHex = (hex) ->
  if typeof hex.panel is "undefined" or hex.panel is null 
    
    ## Create panel that holds hex and all associated sprites
    panel = new PIXI.DisplayObjectContainer()
    panel.position.x = hex.loc.col * @hexRad * 3/4 * 1.11 + @hexRad/2
    panel.position.y = hex.loc.row * @hexRad + @hexRad/2
    panel.position.y +=  @hexRad/2 if hex.loc.col % 2 == 1
    panel.pivot.x = 0.5
    panel.pivot.y = 0.5

    ## Create hex and add to panel
    spr = PIXI.Sprite.fromImage("assets/img/hex-back.png")
    spr.lit = false ## Initially unlit
    spr.anchor.x = 0.5
    spr.anchor.y = 0.5
    spr.scale.x = 0.078
    spr.scale.y = 0.078
    panel.addChild(spr)
    panel.hex = spr

    #setFrame(new PIXI.Rectangle(0,0,@hexRad * 2, @hexRad * 2))

    ## Create color Circles
    for i in [0 .. Hex.SIDES - 1] by 1
      c = hex.colorOfSide(i)
      if(not isNaN(c))
        c = Color.asString(c)
      nudge = 0.54
      shrink = 4
      point = new PIXI.Point( (@hexRad / 2 - shrink) * Math.cos((i - 2) * 2 * Math.PI / Hex.SIDES + nudge), 
                             (@hexRad / 2 - shrink) * Math.sin((i - 2) * 2 * Math.PI / Hex.SIDES + nudge))
      cr = PIXI.Sprite.fromImage("assets/img/circle_" + c.toLowerCase() + ".png")
      cr.anchor.x = 0.5
      cr.anchor.y = 0.5
      cr.scale.x = 0.078
      cr.scale.y = 0.078
      cr.position.x = point.x
      cr.position.y = point.y
      panel.addChild(cr)


    # Store in eachother for pointers
    hex.panel = panel
    panel.hex = hex

    #Add a click listener
    panel.interactive = true
    panel.click = -> 
      hex.click()
      return

    @stage.addChild(panel)
  return hex.panel