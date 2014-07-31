### Begins init processing ###
@init = -> 
  @initStart()

### Set up a PIXI stage - part before asset loading ###
@initStart = ->
  
  @stage = new PIXI.Stage(0x295266, true)
  margin = 20
  @renderer = PIXI.autoDetectRenderer(window.innerWidth - margin, window.innerHeight - margin)
  PIXI.scaleModes.DEFAULT = PIXI.scaleModes.NEAREST

  ## The menu and associated text/buttons/stuff
  @menu = new PIXI.DisplayObjectContainer()
  @stage.addChild(@menu)

  menuHeight = 100

  ##The container for the base hexes. The one that actually responds to clicks.
  @container = new PIXI.DisplayObjectContainer()
  container.position.y = menuHeight
  @stage.addChild(@container)

  ## Containers for elements to be colored. One layer per color 
  @colorContainers = {}
  for c in Color.values()
    colr = c
    if(not isNaN(colr))
      colr = Color.asString(colr)
    cContainer = new PIXI.DisplayObjectContainer()
    cContainer.position.y = menuHeight
    f = new PIXI.ColorMatrixFilter()
    f.matrix = Color.matrixFor(colr)
    cContainer.filters = [f]
    @stage.addChild(cContainer)
    @colorContainers[colr] = cContainer

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
  for col, cContainer of @colorContainers
    cContainer.position.y = newScale2 * 100

  ## Fix board
  if @BOARD?
    scale = (1 / 130) * Math.min(window.innerHeight / window.BOARD.getHeight() / 1.1, window.innerWidth * 1.15 / window.BOARD.getWidth())
    @container.scale.x = scale
    @container.scale.y = scale
    for col, cContainer of @colorContainers
      cContainer.scale.x = scale
      cContainer.scale.y = scale

    ##Center
    n = @hexRad * @container.scale.x
    newX = (window.innerWidth - window.BOARD.getWidth() * n)/2
    @container.position.x = newX
    for col, cContainer of @colorContainers
      cContainer.position.x = newX

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
        # Update lighting of all hexes
        if h.isLit().length > 0 and not h.backPanel.children[0].lit
          h.backPanel.children[0].texture = PIXI.Texture.fromImage("assets/img/hex-lit.png")
          h.backPanel.children[0].lit = true
        if h.isLit().length is 0 and h.backPanel.children[0].lit
          h.backPanel.children[0].texture = PIXI.Texture.fromImage("assets/img/hex-back.png")
          h.backPanel.children[0].lit = false

        hLit = h.isLit()
        nS = h.getNeighborsWithBlanks()
        for col, panel of h.colorPanels
          for connector in panel.children
            c = h.colorOfSide(connector.side)
            n = nS[connector.side]
            if n? and c in hLit and n.colorOfSide(n.indexLinked(h)) is c
              connector.texture = PIXI.Texture.fromImage("assets/img/connector_on.png")
              for nConnector in n.colorPanels[col].children
                if nConnector.side is n.indexLinked(h)
                  nConnector.texture = PIXI.Texture.fromImage("assets/img/connector_on.png")
            else
              connector.texture = PIXI.Texture.fromImage("assets/img/connector_off.png")
              if n?
                for nConnector in n.colorPanels[col].children
                  if nConnector.side is n.indexLinked(h)
                    nConnector.texture = PIXI.Texture.fromImage("assets/img/connector_off.png")

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
          h.backPanel.rotation += inc * radTo60Degree
          h.currentRotation += inc 
          for key, value of h.colorPanels
            value.rotation += inc * radTo60Degree
          if Math.abs(h.targetRotation - h.currentRotation) < tolerance
            inc = (h.targetRotation - h.currentRotation)
            h.backPanel.rotation += inc * radTo60Degree
            h.currentRotation += inc
            for key, value of h.colorPanels
              value.rotation += inc * radTo60Degree
              ## Update side index of each sprite
              for spr in value.children
                spr.side = (spr.side + (h.currentRotation - h.prevRotation)) %% Hex.SIDES
            h.prevRotation = h.currentRotation
            h.canLight = true
            h.light()

        ### Spark and crystal color changing ###
        if (h instanceof Spark or h instanceof Crystal) and h.toColor isnt ""
          col = if (not isNaN(h.toColor)) 
                  Color.asString(h.toColor).toUpperCase() 
                else 
                  h.toColor.toUpperCase()
          # Move connectors to new panel
          connectors = []
          for colr, panel of h.colorPanels
            for spr in panel.children
              connectors.push(spr)
            for ch in [0 .. (panel.children.length - 1)] by 1
              panel.removeChild(panel.getChildAt(0))
          for spr in connectors
            h.colorPanels[col].addChild(spr)
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

    ## Create panels that holds hex
    backpanel = new PIXI.DisplayObjectContainer()
    backpanel.position.x = hex.loc.col * @hexRad * 3/4 * 1.11 + @hexRad * (5/8)
    backpanel.position.y = hex.loc.row * @hexRad + @hexRad * (5/8)
    backpanel.position.y +=  @hexRad/2 if hex.loc.col % 2 == 1
    backpanel.pivot.x = 0.5
    backpanel.pivot.y = 0.5

    ## Create panels for colored parts of hexes
    hColPanel = {}
    for color in Color.values()
      c = color
      if(not isNaN(c))
        c = Color.asString(c)
      cpanel = new PIXI.DisplayObjectContainer()
      cpanel.position.x = hex.loc.col * @hexRad * 3/4 * 1.11 + @hexRad * (5/8)
      cpanel.position.y = hex.loc.row * @hexRad + @hexRad * (5/8)
      cpanel.position.y +=  @hexRad/2 if hex.loc.col % 2 == 1
      cpanel.pivot.x = 0.5
      cpanel.pivot.y = 0.5
      hColPanel[c] = cpanel

    ## Create hex and add to panel
    spr = PIXI.Sprite.fromImage("assets/img/hex-back.png")
    spr.lit = false ## Initially unlit
    spr.anchor.x = 0.5
    spr.anchor.y = 0.5
    backpanel.addChild(spr)
    backpanel.hex = spr

    ## Create color Circles
    for i in [0 .. Hex.SIDES - 1] by 1
      c = hex.colorOfSide(i)
      if(not isNaN(c))
        c = Color.asString(c).toUpperCase()
      else
        c = c.toUpperCase()
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
      # The side of the hex this is on
      cr.side = i
      ## Add to correct hColPanel
      hColPanel[c].addChild(cr)


    # Store panels in hex for later access
    hex.backPanel = backpanel
    hex.colorPanels = hColPanel

    ## Add to back container
    @container.addChild(backpanel)

    ## Add to correct color layers
    for key, value of hColPanel  
      @colorContainers[key].addChild(value)

    #Add a click listener
    backpanel.interactive = true
    backpanel.click = -> 
      hex.click()
      return

  return hex.panel