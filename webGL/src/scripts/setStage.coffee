### Begins init processing ###

@BOARDNAME = "board01" ## Most recent board loaded. Initial value is default
@initted  = false       ## True if a full init process has occured. False until then
@gameOn   = true       ## True if the board should respond to clicks, false otherwise (false when help is up)


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

    ## Add basic color filter to this colorContainer
    f = new PIXI.ColorMatrixFilter()
    f.matrix = Color.matrixFor(colr)
    cContainer.filters = [f]
    @goalContainer[colr] = cContainer
    @goalContainer.addChild(cContainer)

  ## Container for help menu. Only actually added to stage when the help button is clicked ##
  @helpContainer = new PIXI.DisplayObjectContainer()
  preloadImages()
  return

### Creates the help menu. Called after images are loaded ###
@createHelpMenu = ->
  helpContainer = window.helpContainer
  helpContainer.position.x = 450
  helpContainer.position.y = 200
  ## Add background as first child  - 500x300 in original size ##
  helpContainer.addChild(PIXI.Sprite.fromImage("assets/img/helpBackground.png"))

  headerStyle = {font:"bold 15px Futura", fill: "#6E6E6E"}
  contentStyle = {font: "15px Futura", fill: "#6E6E6E"}
  
  close = new PIXI.Text("X", {font: "bold 20px Sans-Serif", fill: "gray"})
  close.position.x = 480
  close.position.y = 5
  close.interactive = true
  close.click = ->
    window.stage.removeChild(window.helpContainer)
    window.gameOn = true
    return
  helpContainer.addChild(close)

  title = new PIXI.Text("Prismatic", headerStyle)
  title.position.x = 200
  title.position.y = 10
  helpContainer.addChild(title)

  topContent = new PIXI.Text("Prismatic is a light-based color game. Get the right \nnumber of Crystals to light up for each color.", contentStyle)
  topContent.position.x = 20
  topContent.position.y = 40
  helpContainer.addChild(topContent)

  sparkIcon = PIXI.Sprite.fromImage("assets/img/spark.png")
  sparkIcon.position.x = 50
  sparkIcon.position.y = 95
  sparkIcon.scale.x = 0.25
  sparkIcon.scale.y = 0.25
  helpContainer.addChild(sparkIcon)

  sparksHeader = new PIXI.Text("Sparks", headerStyle)
  sparksHeader.position.x = 100
  sparksHeader.position.y = 100
  helpContainer.addChild(sparksHeader)

  sparksContent = new PIXI.Text(" - the start point. They emit one color of light.", contentStyle)
  sparksContent.position.x = 150
  sparksContent.position.y = 100
  helpContainer.addChild(sparksContent)

  prismIcon = PIXI.Sprite.fromImage("assets/img/hex-back.png")
  prismIcon.position.x = 50
  prismIcon.position.y = 135
  prismIcon.scale.x = 0.25
  prismIcon.scale.y = 0.25
  helpContainer.addChild(prismIcon)

  prismsHeader = new PIXI.Text("Prisms", headerStyle)
  prismsHeader.position.x = 100
  prismsHeader.position.y = 140
  helpContainer.addChild(prismsHeader)

  prismsContent = new PIXI.Text(" - the basic piece. The channel light and rotate.", contentStyle)
  prismsContent.position.x = 150
  prismsContent.position.y = 140
  helpContainer.addChild(prismsContent)

  crystalIcon = PIXI.Sprite.fromImage("assets/img/hex-lit.png")
  crystalIcon.position.x = 50
  crystalIcon.position.y = 175
  crystalIcon.scale.x = 0.25
  crystalIcon.scale.y = 0.25
  helpContainer.addChild(crystalIcon)

  crystalsHeader = new PIXI.Text("Crystals", headerStyle)
  crystalsHeader.position.x = 100
  crystalsHeader.position.y = 180
  helpContainer.addChild(crystalsHeader)

  crystalsContent = new PIXI.Text(" - the end goal. They recieve light.", contentStyle)
  crystalsContent.position.x = 160
  crystalsContent.position.y = 180
  helpContainer.addChild(crystalsContent)

  bottomContent = new PIXI.Text("> Click on Sparks to change their color. \n> Click on Prisms to rotate their alignment.", contentStyle)
  bottomContent.position.x = 20
  bottomContent.position.y = 230
  helpContainer.addChild(bottomContent)

  tagText = new PIXI.Text("created by Michael Patashnik - Mgpshnik@gmail.com", {font: "italic 10px Sans-Serif", fill: "gray"})
  tagText.position.x = 120
  tagText.position.y = 275
  helpContainer.addChild(tagText)
  return

### Makes and adds the win container. Called when the player beats this level ###
@makeWinGameContainer = ->
  @winContainer = new PIXI.DisplayObjectContainer()
  @winContainer.addChild(new PIXI.Sprite(new PIXI.Texture(PIXI.BaseTexture.fromImage("assets/img/helpBackground.png"), new PIXI.Rectangle(0,0,500,200))))
  @winContainer.position.x = 450
  @winContainer.position.y = 250

  headerStyle = {font:"bold 15px Futura", fill: "#6E6E6E"}
  contentStyle = {font: "15px Futura", fill: "#6E6E6E"}

  title = new PIXI.Text("You Win!", headerStyle)
  title.position.x = 200
  title.position.y = 10
  @winContainer.addChild(title)

  topContent = new PIXI.Text("You beat level " + @level + " in " + @BOARD.moves + " moves.", contentStyle)
  topContent.position.x = 20
  topContent.position.y = 40
  @winContainer.addChild(topContent)

  if @level < 50
    nextLvl = new PIXI.Text((@level + 1) + " >>", contentStyle)
    nextLvl.interactive = true
  else
    nextLvl = new PIXI.Text("Thank you for playing!", contentStyle)
    nextLvl.interactive = false
  nextLvl.click = ->
    num = 
      if (window.level + 1 < 10)
        "0" + (window.level + 1)
      else
        "" + (window.level + 1)
    window.BOARDNAME = "board" + num
    window.menu.children[3].click()
    return
  nextLvl.position.x = 225
  nextLvl.position.y = 175
  @winContainer.addChild(nextLvl)

  @stage.addChild(@winContainer)
  return

### Load assets into cache ###
@preloadImages = ->
  assets = ["assets/img/galaxy-28.jpg", "assets/img/helpBackground.png",
            "/assets/img/hex-back.png", "assets/img/hex-lit.png", "assets/img/core.png",
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
  bck = @menu.children[0]
  menuBackground = @menu.children[1]
  lvlText = @menu.children[2]
  resetButton = @menu.children[3]
  selectLabel = @menu.children[4]
  prevLvl = @menu.children[5]
  nextLvl = @menu.children[6]
  helpButton = @menu.children[7]
  goalContainer = @menu.children[8]

  ##Fix background image
  bck.scale.x = Math.max(window.innerWidth / bck.texture.baseTexture.width, 0.75) 
  bck.scale.y = Math.max((window.innerHeight) / bck.texture.baseTexture.height, 0.75) 

  ## Default size = 200.
  menuBackground.scale.x = window.innerWidth / 200

  ##Resize vertically - default height = 100
  newScale2 = Math.min(1, Math.max(0.5, window.innerHeight / 1500))
  menuBackground.scale.y = newScale2
  @base.position.y = newScale2 * 100
  for col, cContainer of @colorContainers
    cContainer.position.y = newScale2 * 100

  optMenu = [@helpContainer, @winContainer]
  ## Fix the help menu. No resizing, just reposition
  for menu in optMenu
    if menu?
      helpWidth = menu.getLocalBounds().width
      helpHeight = menu.getLocalBounds().height
      menu.position.x = (window.innerWidth - helpWidth) / 2
      menu.position.y = (window.innerHeight - newScale2 * 100 - helpHeight) / 2 + newScale2 * 100

  ## Scale all menu labels and buttons
  newScale3 = newScale2 * 0.5
  lvlText.scale.x = lvlText.scale.y = newScale3
  resetButton.scale.x = resetButton.scale.y = newScale3
  selectLabel.scale.x = selectLabel.scale.y = newScale3
  prevLvl.scale.x = prevLvl.scale.y = newScale3
  nextLvl.scale.x = nextLvl.scale.y = newScale3
  helpButton.scale.x = helpButton.scale.y = newScale3

  ## Move labels and buttons into place on x axis
  # Left justified elements
  menumargin = 20
  lvlPush = 
    if @level >= 10
      35
    else
      0
  lvlText.position.x = menumargin
  #Right Justified Elements
  helpButton.position.x = (window.innerWidth) - (250 * newScale3)
  nextLvl.position.x = helpButton.position.x - ((275) * newScale3)
  prevLvl.position.x = nextLvl.position.x - ((300) * newScale3)
  selectLabel.position.x = prevLvl.position.x - (300 * newScale3)
  resetButton.position.x =  selectLabel.position.x - 300 * newScale3


  fixY = (comp, scale) ->
    comp.position.y = 35 * scale
    return

  fixY(lvlText, newScale3)
  fixY(resetButton, newScale3)
  fixY(helpButton, newScale3)
  fixY(nextLvl, newScale3)
  fixY(prevLvl, newScale3)
  fixY(selectLabel, newScale3)


  ## Fix board
  if @BOARD?
    if goalContainer?
      ## Fix goalContainer
      goalContainer.position.y = -10 * newScale3
      goalContainer.scale.x = newScale3
      goalContainer.scale.y = newScale3
      goalContainer.position.x = lvlText.position.x + (575 + lvlPush) * newScale3 ## Not using lvlText.getLocalBounds() because it likes to change randomly (width)

    scale = (1 / 130) * Math.min(window.innerHeight / window.BOARD.getHeight() / 1.1, window.innerWidth * 1.15 / window.BOARD.getWidth())
    @base.scale.x = scale
    @base.scale.y = scale
    for col, cContainer of @colorContainers
      cContainer.scale.x = scale
      cContainer.scale.y = scale

    ##Center
    n = @hexRad * @base.scale.x
    newX = (window.innerWidth - window.BOARD.getWidth() * n)/2 + 20
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
      ## Update text on goal
      curLit = @BOARD.crystalLitCount()
      goalContainer = @menu.children[8]
      isWin = true ## True if this user has won - every goal set.
      for pan in goalContainer.children
        for spr in pan.children
          if spr instanceof PIXI.Text and spr.color.toUpperCase() of curLit
            spr.setText(curLit[spr.color.toUpperCase()] + spr.text.substring(1))
            if curLit[spr.color.toUpperCase()] < parseInt(spr.text.substring(2))
              isWin = false

      if isWin and (not @winContainer?)
        @gameOn = false
        @makeWinGameContainer()

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
  window.createHelpMenu()
  @BOARD = new Board() ## Temp board to handle resize requests while loading new board
  Board.loadBoard(window.BOARDNAME)
  return

## Font for text in the menu
@menuStyle = {font:"bold 85px 'Futura' ", fill:"white"}

@initMenu = () ->
  ## Create the background itself
  bck = PIXI.Sprite.fromImage("assets/img/galaxy-28.jpg")
  @menu.addChild(bck)
  ## Create the bar, with some transparency
  menuBar = PIXI.Sprite.fromImage("assets/img/menu.png")
  menuBar.alpha = 0.5
  @menu.addChild(menuBar)

  lvlText = new PIXI.Text("Lvl. " + @level + " of 50", @menuStyle)
  @menu.addChild(lvlText)

  resetButton = new PIXI.Text("Reset", @menuStyle)
  resetButton.interactive = true
  resetButton.click = ->
    if window.winContainer isnt null
      window.stage.removeChild(window.winContainer)
    window.winContainer = null
    window.gameOn = true
    window.clearBoard()
    Board.loadBoard(window.BOARDNAME)
    window.updateMenu()
    return

  @menu.addChild(resetButton)


  selectLabel = new PIXI.Text("Level: ", @menuStyle)
  @menu.addChild(selectLabel)

  if @level > 1
    prevLvl = new PIXI.Text("<< " + (@level - 1), @menuStyle)
    prevLvl.interactive = true
    
  else
    prevLvl = new PIXI.Text("     ", @menuStyle)
    prevLvl.interactive = false
  prevLvl.click = ->
    num = 
      if (window.level - 1 < 10)
        "0" + (window.level - 1)
      else
        "" + (window.level - 1)
    window.BOARDNAME = "board" + num
    resetButton.click()
    return
  @menu.addChild(prevLvl)

  if @level < 50
    nextLvl = new PIXI.Text((@level + 1) + " >>", @menuStyle)
    nextLvl.interactive = true
  else
    nextLvl = new PIXI.Text("     ", @menuStyle)
    nextLvl.interactive = false
  nextLvl.click = ->
    num = 
      if (window.level + 1 < 10)
        "0" + (window.level + 1)
      else
        "" + (window.level + 1)
    window.BOARDNAME = "board" + num
    resetButton.click()
    return
  @menu.addChild(nextLvl)

  helpButton = new PIXI.Text("Help", @menuStyle)
  helpButton.interactive = true
  helpButton.click = ->
    if (window.winContainer is null)
      window.gameOn = false
      window.stage.addChild(window.helpContainer)
    return
  @menu.addChild(helpButton)

  ## Add goal components to menu
  @menu.addChild(@goalContainer)
  return

## Updates the menu to the most recent text for level - assumes initted
@updateMenu = () ->
  lvlText = @menu.children[2]
  lvlText.setText("Lvl. " + @level + " of 50")
  prevLvl = @menu.children[5]
  if @level > 1
    prevLvl.setText("<< " + (@level - 1))
    prevLvl.interactive = true
  else
    prevLvl.setText("     ")
    prevLvl.interactive = false
  nextLvl = @menu.children[6]
  if @level < 50
    nextLvl.setText((@level + 1) + " >>")
    nextLvl.interactive = true
  else
    nextLvl.setText("     ")
    nextLvl.interactive = false
  return

### Clears board and associated sprites from screen, usually in anticipation of new board being loaded ###
@clearBoard = () ->
  sprToRemove = []
  for pan in @menu.children[8].children
    for spr in pan.children
      sprToRemove.push(spr)
  for spr in @stage.children[1].children
    sprToRemove.push(spr)
  for i in [1 ..(@stage.children.length - 1)]
    for pan in @stage.children[i].children
      for spr in pan.children
        sprToRemove.push(spr)
  for spr in sprToRemove
    if spr?
      spr.parent.removeChild(spr)
  @BOARD = null
  return

### Called when the board is loaded ###
@onBoardLoad = () ->
  ##Make the menu, now that we know what level we're on
  if not @initted
    window.initMenu()
    @initted = true

  ## Create the goal board on the right of the main board
  colors = window.BOARD.colorsPresent()

  ## Goal board has crystal, spark on even rows only.
  goalBoard = new Board(colors.length,1)
  i = 0
  for color in colors
    c = new Crystal(goalBoard, new Loc(i, 0))
    c.lit = color
    i++
  
  for c in goalBoard.allHexesOfClass("Crystal")
    ## Create sprites for crystal
    spr = PIXI.Sprite.fromImage("assets/img/crystal.png")
    spr.lit = false
    spr.color = c.lit
    spr.hex = c
    spr.position.x = c.loc.row * @hexRad * 2.75   ## Leaves some space for text between sprites
    spr.anchor.x = 0.5
    spr.anchor.y = 0.5
    @goalContainer[c.lit.toUpperCase()].addChild(spr)
    goalCount = window.BOARD[c.lit.toUpperCase()]
    @goalContainer[c.lit.toUpperCase()].goalCount = goalCount
    goalStyle = @menuStyle
    text = new PIXI.Text("0/" + goalCount, goalStyle)
    text.position.x = c.loc.row * @hexRad * 2.75 + @hexRad * 0.75
    text.position.y = - 60
    text.color = c.lit
    @goalContainer[c.lit.toUpperCase()].addChild(text)



  @goalContainer.count = 4

  window.BOARD.relight()
  ## Fit the canvas to the window
  document.body.appendChild(renderer.view)
  ## Scale the pieces based on the board size relative to the canvas size
  window.resize()
  window.drawBoard()
  @resize()

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
      if window.gameOn
        hex.click()
      return

  return hex.panel