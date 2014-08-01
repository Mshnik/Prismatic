### Begins init processing ###
@init = -> 
  @initStart()

## A helper 
@typeIsArray = Array.isArray || ( value ) -> return {}.toString.call( value ) is '[object Array]'

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
  for c in Color.values()
    colr = c
    if(not isNaN(colr))
      colr = Color.asString(colr)
    cContainer = new PIXI.DisplayObjectContainer()
    cContainer.position.y = menuHeight

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
    cContainer.unlit.filters = [@flat]
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

    @stage.addChild(cContainer)
    @colorContainers[colr] = cContainer

  preloadImages()
  return

### Load assets into cache ###
@preloadImages = ->
  assets = ["assets/img/galaxy-28.jpg", "/assets/img/hex-back.png", "assets/img/hex-lit.png", "assets/img/core.png"
            "assets/img/spark.png", "assets/img/crystal.png",
            "assets/img/menu.png", "assets/img/connector_off.png", "assets/img/connector_on.png"]
  loader = new PIXI.AssetLoader(assets)
  loader.onComplete = @initFinish
  loader.load()
  return

### Resizes the stage correctly ###
@resize = () ->
  margin = 0
  window.renderer.resize(window.innerWidth - margin, window.innerHeight - margin)

  ## Expand/contract the menu. Horizontal expansion/contraction is on middle sprite. Vertical is on whole menubackground container
  menuBackground = @menu.children[0]
  menuLeft = menuBackground.children[0]
  menuMiddle = menuBackground.children[1]
  menuRight = menuBackground.children[2]
  bck = @menu.children[1]

  ## Default size = 200.
  newScale = (window.innerWidth - 220) / 200
  menuMiddle.scale.x = newScale
  menuRight.position.x = 100 + (newScale * 200)

  ##Resize vertically - default height = 100
  newScale2 = Math.min(1, Math.max(0.5, window.innerHeight / 1000))
  menuBackground.scale.y = newScale2
  @base.position.y = newScale2 * 100
  for col, cContainer of @colorContainers
    cContainer.position.y = newScale2 * 100

  ##Fix background image
  bck.position.y = newScale2 * 100
  bck.scale.x = Math.max(window.innerWidth / bck.texture.baseTexture.width, 0.75) 
  bck.scale.y = Math.max((window.innerHeight - 100) / bck.texture.baseTexture.height, 0.75) 

  ## Fix board
  if @BOARD?
    scale = (1 / 130) * Math.min(window.innerHeight / window.BOARD.getHeight() / 1.1, window.innerWidth * 1.15 / window.BOARD.getWidth())
    @base.scale.x = scale
    @base.scale.y = scale
    for col, cContainer of @colorContainers
      cContainer.scale.x = scale
      cContainer.scale.y = scale

    ##Center
    n = @hexRad * @base.scale.x
    newX = (window.innerWidth - window.BOARD.getWidth() * n)/2
    @base.position.x = newX
    for col, cContainer of @colorContainers
      cContainer.position.x = newX

  return

### Detect when the window is resized - jquery ftw! ###
window.onresize = () ->
  window.resize()

@toLit = (connector) ->
  try
    @colorContainers[connector.color].unlit.removeChild(connector.panel)
  catch
  if @typeIsArray connector.color
    if connector.color.length > 0
      c = connector.color[0].toUpperCase()
    else
      c = Color.asString(Color.NONE).toUpperCase()
  else
    c = connector.color.toUpperCase()
  if c is Color.asString(Color.NONE).toUpperCase()
    @toUnlit(connector) ## Prevent lighting of color.NONE
  else
    @colorContainers[c.toUpperCase()].lit.addChild(connector.panel)
    connector.linked = true
  return

@toUnlit = (connector) ->
  try
    @colorContainers[connector.color].lit.removeChild(connector.panel)
  catch
  if @typeIsArray connector.color
    if connector.color.length > 0
      c = connector.color[0]
    else
      c = Color.asString(Color.NONE)
  else
    c = connector.color
  if connector.hex? and connector.hex instanceof Crystal  
    @colorContainers[Color.asString(Color.NONE).toUpperCase()].unlit.addChild(connector.panel)
  else
    @colorContainers[c.toUpperCase()].unlit.addChild(connector.panel)
  connector.linked = false
  return

@colorOffset = {}
for c in Color.values()
  if not isNaN(c)
    c = Color.fromString(c).toUpperCase()
  else
    c = c.toUpperCase()
  @colorOffset[c] = Math.random() + 0.5

### Updates the pulse filter that controls lighting effects ###
@calcPulseFilter = (count) ->
  for col, val of @colorContainers
    pulse = val.lit.filters[0]
    cont = (count + val.lit.pulseOffset)/val.lit.pulseLength
    m = pulse.matrix
    m[0] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5
    m[5] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5
    m[10] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5
    m[15] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.25 + 0.75
    pulse.matrix = m
  return

### Finish initing after assets are loaded ###
@initFinish = ->
  ##Make the menu
  window.initMenu()
  Color.makeFilters()
  window.count = 0
  animate = () ->
    ## Color animation
    window.count += 1;  ## Frame count
    @calcPulseFilter(window.count)
    rotSpeed = 1/5
    tolerance = 0.000001 ## For floating point errors - difference below this is considered 'equal'
    radTo60Degree = 1.04719755 ## 1 radian * this coefficient = 60 degrees
    if (@BOARD?)
      for h in @BOARD.allHexes()
        ##Update lighting of all hexes
        if h.isLit().length > 0 and not h.backPanel.children[0].lit
          h.backPanel.children[0].lit = true
          if not (h instanceof Prism)
            @toLit(h.backPanel.spr)
        if h.isLit().length is 0 and h.backPanel.children[0].lit
          h.backPanel.children[0].lit = false
          if not (h instanceof Prism)
            @toUnlit(h.backPanel.spr)

        hLit = h.isLit()
        if h instanceof Prism
          for col, core of h.cores
            if col.toLowerCase() not in hLit and core.alpha > 0
              core.alpha = 0
            else if col.toLowerCase() in hLit and core.alpha is 0
              core.alpha = 0.75

        nS = h.getNeighborsWithBlanks()
        for col, panel of h.colorPanels
          for connector in panel.children
            c = h.colorOfSide(connector.side)
            n = nS[connector.side]
            if n? and c in hLit and n.colorOfSide(n.indexLinked(h)) is c and not connector.linked
              connector.texture = PIXI.Texture.fromImage("assets/img/connector_on.png")
              @toLit(connector)
              for nConnector in n.colorPanels
                if nConnector.side is n.indexLinked(h) and not nConnector.linked
                  nConnector.texture = PIXI.Texture.fromImage("assets/img/connector_on.png")
                  @toLit(nConnector)
            else if connector.linked and (c not in hLit or n? and n.colorOfSide(n.indexLinked(h)) isnt c)
              connector.texture = PIXI.Texture.fromImage("assets/img/connector_off.png")
              @toUnlit(connector)
              if n?
                for nConnector in n.colorPanels
                  if nConnector.side is n.indexLinked(h) and nConnector.linked
                    nConnector.texture = PIXI.Texture.fromImage("assets/img/connector_off.png")
                    @toUnlit(nConnector)

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
          for value in h.colorPanels
            value.rotation += inc * radTo60Degree
          for col, core of h.cores
            core.currentRotation += inc
          if Math.abs(h.targetRotation - h.currentRotation) < tolerance
            inc = (h.targetRotation - h.currentRotation)
            h.backPanel.rotation += inc * radTo60Degree
            h.currentRotation += inc
            for col, core of h.cores
              core.currentRotation += inc
            for value in h.colorPanels
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
          h.backPanel.spr.color = col
          @toLit(h.backPanel.spr)
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

    ## Create the background itself
  bck = PIXI.Sprite.fromImage("assets/img/galaxy-28.jpg")
  bck.position.y = 100
  @menu.addChild(bck)
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

    ## Create hex and add to panel
    if hex instanceof Prism
      spr = PIXI.Sprite.fromImage("assets/img/hex-back.png")
    else if hex instanceof Crystal
      spr = PIXI.Sprite.fromImage("assets/img/crystal.png")
    else if hex instanceof Spark
      spr = PIXI.Sprite.fromImage("assets/img/spark.png")
    spr.lit = false ## Initially unlit
    spr.anchor.x = 0.5
    spr.anchor.y = 0.5
    spr.hex = hex
    backpanel.addChild(spr)
    backpanel.spr = spr
    hex.spr = spr

    sidePanels = []
    ## Create color Circles
    if hex instanceof Prism
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
        cr.linked = false
        cr.anchor.x = 0.5
        cr.anchor.y = 0.8
        cr.rotation = i * radTo60Degree
        cr.position.x = point.x
        cr.position.y = point.y
        # The side of the hex this is on
        cr.side = i
        cr.color = c
        ## Create a panel for this connector
        cpanel = new PIXI.DisplayObjectContainer()
        cpanel.position.x = hex.loc.col * @hexRad * 3/4 * 1.11 + @hexRad * (5/8)
        cpanel.position.y = hex.loc.row * @hexRad + @hexRad * (5/8)
        cpanel.position.y +=  @hexRad/2 if hex.loc.col % 2 == 1
        cpanel.pivot.x = 0.5
        cpanel.pivot.y = 0.5
        cpanel.addChild(cr)
        cr.panel = cpanel
        sidePanels.push(cpanel)
        ## Add to unlit (for now)
        @colorContainers[c].unlit.addChild(cpanel)

        ## Create cores
        coreContainer = {}
        for col in Color.regularColors()
          col = 
            if not isNaN(col)
              Color.asString(col).toUpperCase()
            else
              col.toUpperCase()
          core = PIXI.Sprite.fromImage("assets/img/core.png")
          core.position.x = hex.loc.col * @hexRad * 3/4 * 1.11 + @hexRad * (7/16)
          core.position.y = hex.loc.row * @hexRad + @hexRad * (7/16) - 0.5
          core.position.y +=  @hexRad/2 if hex.loc.col % 2 == 1
          core.pivot.x = 0.5
          core.pivot.y = 0.5
          core.alpha = 0
          coreContainer[col] = core
          @colorContainers[col].lit.addChild(core)


    ## Store the color this is currently lit (if non-prism)
    else if hex instanceof Crystal
      spr.color = 
        if hex.lit is Color.NONE
          "NONE"
        else
          hex.lit.toUpperCase()
      spr.panel = backpanel
      @toUnlit(spr)
    else if hex instanceof Spark
      spr.color = hex.getColor().toUpperCase()
      spr.panel = backpanel
      @toLit(spr)


    # Store panels in hex for later access
    hex.backPanel = backpanel
    hex.colorPanels = sidePanels
    hex.cores = coreContainer

    ## Add to back container, if prism
    if hex instanceof Prism
      @base.addChild(backpanel)

    #Add a click listener
    backpanel.interactive = true
    backpanel.click = -> 
      hex.click()
      return

  return hex.panel