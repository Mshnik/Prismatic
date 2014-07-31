### Begins init processing ###
@init = -> 
  @initStart()

### Set up a PIXI stage - part before asset loading ###
@initStart = ->
  
  @stage = new PIXI.Stage(0x295266, true)
  margin = 20
  @renderer = PIXI.autoDetectRenderer(window.innerWidth - margin, window.innerHeight - margin)
  PIXI.scaleModes.DEFAULT = PIXI.scaleModes.NEAREST
  @menu = new PIXI.DisplayObjectContainer()
  @stage.addChild(@menu)
  @container = new PIXI.DisplayObjectContainer()
  container.position.y = 100
  @stage.addChild(@container)

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
  assets = ["assets/img/hex-back.png", "assets/img/hex-lit.png", "assets/img/menu.png",
            "assets/img/connector_off.png", "assets/img/connector_on.png"]
  loader = new PIXI.AssetLoader(assets)
  loader.onComplete = @initFinish
  loader.load()
  return

### Resizes the stage correctly ###
@resize = () ->
  margin = 20
  window.renderer.resize(window.innerWidth - margin, window.innerHeight - margin)

  ## Expand/contract the menu. Horizontal expansion/contraction is on middle sprite. Vertical is on whole menubackground container
  menuBackground = @menu.children[0]
  menuLeft = menuBackground.children[0]
  menuMiddle = menuBackground.children[1]
  menuRight = menuBackground.children[2]

  ## Default size = 200.
  newScale = (window.innerWidth - 220) / 200
  menuMiddle.scale.x = newScale
  menuRight.position.x = 100 + (newScale * 200)

  ##Resize vertically - default height = 100
  newScale2 = Math.min(1, Math.max(0.75, window.innerHeight / 1000))
  menuBackground.scale.y = newScale2
  @container.position.y = newScale2 * 100

  ## Fix board
  if(@BOARD?)
    scale = (1 / 130) * Math.min(window.innerHeight / window.BOARD.getHeight() / 1.1, window.innerWidth * 1.15 / window.BOARD.getWidth())
    @container.scale.x = scale
    @container.scale.y = scale

    ##Center
    n = @hexRad * @container.scale.x
    @container.position.x = (window.innerWidth - window.BOARD.getWidth() * n)/2
  return

### Detect when the window is resized - jquery ftw! ###
window.onresize = () ->
  window.resize()


### Finish initing after assets are loaded ###
@initFinish = ->
  window.initMenu()
  Color.makeFilters()
  window.count = 0
  animate = () ->
    ## Color animation
    window.count += 1;  ## Frame count
    rotSpeed = 1/10
    tolerance = 0.000001 ## For floating point errors - difference below this is considered 'equal'
    radTo60Degree = 1.04719755 ## 1 radian * this coefficient = 60 degrees
    if (@BOARD?)
      for h in @BOARD.allHexes()
        ## Update lighting of all hexes
        if h.isLit().length > 0 and not h.panel.children[0].lit
          h.panel.children[0].texture = PIXI.Texture.fromImage("assets/img/hex-lit.png")
          h.panel.children[0].lit = true
        if h.isLit().length is 0 and h.panel.children[0].lit
          h.panel.children[0].texture = PIXI.Texture.fromImage("assets/img/hex-back.png")
          h.panel.children[0].lit = false

        if h.lightChange
          hLit = h.isLit()
          p = h.panel
          nS = h.getNeighborsWithBlanks()
          for i in [0 .. Hex.SIDES - 1] by 1
            c = h.colorOfSide(i)
            n = nS[i]
            if n? and c in hLit and n.colorOfSide(n.indexLinked(h)) is c
              p.children[i+1].texture = PIXI.Texture.fromImage("assets/img/connector_on.png")
            else
              p.children[i+1].texture = PIXI.Texture.fromImage("assets/img/connector_off.png")
          h.lightChange = false

        ### Rotation of a prism - finds a prism that wants to rotate and rotates it a bit.
            If this is the first notification that this prism wants to rotate, stops providing light.
            If the prism is now done rotating, starts providing light again ###
        if h instanceof Prism and h.currentRotation isnt h.targetRotation
          if h.canLight
            h.canLight = false
            h.light()
          inc = 
            if (h.targetRotation - h.prevRotation) >= 0 
              rotSpeed
            else
              -rotSpeed
          h.panel.rotation += inc * radTo60Degree
          h.currentRotation += inc 
          if Math.abs(h.targetRotation - h.currentRotation) < tolerance
            inc = (h.targetRotation - h.currentRotation)
            h.panel.rotation += inc * radTo60Degree
            h.currentRotation += inc
            h.prevRotation = h.currentRotation
            h.canLight = true
            h.light()
        if (h instanceof Spark or h instanceof Crystal) and h.toColor isnt ""
          col = if (not isNaN(h.toColor)) 
                  Color.asString(h.toColor) 
                else 
                  h.toColor
          filter = Color.filters[col]
          for i in [1 .. Hex.SIDES] by 1
            h.panel.children[i].filters = [filter]
          h.toColor = ""
    requestAnimFrame(animate )
    @renderer.render(@stage)
    return
  requestAnimFrame(animate )
  @BOARD = new Board() ## Temp board to handle resize requests while loading new board
  Board.loadBoard("board1")
  return

@initMenu = () ->
  menuBackground = new PIXI.DisplayObjectContainer()
  baseTex = PIXI.BaseTexture.fromImage("assets/img/menu.png")
  menuBack_Left = new PIXI.Sprite(new PIXI.Texture(baseTex, new PIXI.Rectangle(0, 0, 100, 100)))
  menuBack_Middle = new PIXI.Sprite(new PIXI.Texture(baseTex, new PIXI.Rectangle(100, 0, 200, 100)))
  menuBack_Middle.position.x = 100
  menuBack_Right = new PIXI.Sprite(new PIXI.Texture(baseTex, new PIXI.Rectangle(300, 0, 100, 100)))
  menuBack_Right.position.x = 300

  ## Add pieces to background
  menuBackground.addChild(menuBack_Left)
  menuBackground.addChild(menuBack_Middle)
  menuBackground.addChild(menuBack_Right)
  @menu.addChild(menuBackground)
  @resize()
  return

### Called when the board is loaded ###
@onBoardLoad = () ->
  window.BOARD.relight()
  ## Fit the canvas to the window
  document.body.appendChild(renderer.view)
  ## Scale the pieces based on the board size relative to the canvas size
  window.resize()
  window.drawBoard()

### Creates a dummy board and adds to scope. Mainly for testing ###
@createDummyBoard = () ->
  @BOARD = @Board.makeBoard(4, 12,3)
  @onBoardLoad()
  return


### Draws the Board in BOARD on the stage. ###
@drawBoard = () ->
 for h in @BOARD.allHexes()
  @createSpriteForHex(h)
 return

@hexRad = 110

### Creates a single sprite for a hex and adds it to stage ###
@createSpriteForHex = (hex) ->
  if typeof hex.panel is "undefined" or hex.panel is null 
    radTo60Degree = 1.04719755 ## 1 radian * this coefficient = 60 degrees

    ## Create panel that holds hex and all associated sprites
    panel = new PIXI.DisplayObjectContainer()
    panel.position.x = hex.loc.col * @hexRad * 3/4 * 1.11 + @hexRad * (5/8)
    panel.position.y = hex.loc.row * @hexRad + @hexRad * (5/8)
    panel.position.y +=  @hexRad/2 if hex.loc.col % 2 == 1
    panel.pivot.x = 0.5
    panel.pivot.y = 0.5

    ## Create hex and add to panel
    spr = PIXI.Sprite.fromImage("assets/img/hex-back.png")
    spr.lit = false ## Initially unlit
    spr.anchor.x = 0.5
    spr.anchor.y = 0.5
    panel.addChild(spr)
    panel.hex = spr

    #setFrame(new PIXI.Rectangle(0,0,@hexRad * 2, @hexRad * 2))

    ## Create color Circles
    for i in [0 .. Hex.SIDES - 1] by 1
      c = hex.colorOfSide(i)
      if(not isNaN(c))
        c = Color.asString(c)
      nudge = 0.528  ## Nudges along radius
      shrink = 25 ## Moves beam towards center
      point = new PIXI.Point( (@hexRad / 2 - shrink) * Math.cos((i - 2) * 2 * Math.PI / Hex.SIDES + nudge), 
                             (@hexRad / 2 - shrink) * Math.sin((i - 2) * 2 * Math.PI / Hex.SIDES + nudge))
      cr = PIXI.Sprite.fromImage("assets/img/connector_off.png")
      cr.anchor.x = 0.5
      cr.anchor.y = 0.8
      cr.rotation = i * radTo60Degree
      cr.position.x = point.x
      cr.position.y = point.y
      cr.scale.x = 0.20
      cr.scale.y = 0.09

      ##Apply color filter
      filter = Color.filters[c]
      cr.filters = [filter]

      panel.addChild(cr)


    # Store in eachother for pointers
    hex.panel = panel
    panel.hex = hex

    #Add a click listener
    panel.interactive = true
    panel.click = -> 
      hex.click()
      return

    @container.addChild(panel)
  return hex.panel