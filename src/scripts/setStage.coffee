### Begins init processing ###

@BOARDNAME = "board11" ## Most recent board loaded. Initial value is default
@initted  = false       ## True if a full init process has occured. False until then
@gameOn   = true       ## True if the board should respond to clicks, false otherwise (false when help is up)
@showWinContainer = true  ## True if the win container should be shown when the player wins
@difficulty = @Game.MEDIUM  ## Difficulty the player is currently on

@siteprefix = "prismatic/" ## For the git site, routing assets

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
  headerStyle = {font:"bold 15px Futura", fill: "#6E6E6E"}
  contentStyle = {font: "15px Futura", fill: "#6E6E6E"}

  ## Overaly that covers the rest of the screen when the help menu is up
  overlay = new PIXI.Graphics()
  overlay.beginFill(0x000000, 1)
  overlay.drawRect(0,0,window.innerWidth,window.innerHeight)
  overlay.endFill()
  overlaySprite = new PIXI.Sprite(overlay.generateTexture())
  overlaySprite.interactive = true
  overlaySprite.alpha = 0.35
  overlaySprite.position.x = -450
  overlaySprite.position.y = -200
  overlaySprite.click = ->
    window.stage.removeChild(window.helpContainer)
    window.gameOn = true
    return
  helpContainer.addChild(overlaySprite)

  overlay.clear()
  overlay.beginFill(0xFFFFFF, 0.25)
  overlay.drawRect(0,0,100,50)
  overlay.endFill()
  overlay.lineStyle(1, 0x999999, 1)
  overlay.drawRect(0,0,100, 50)
  goalOverlay = new PIXI.Sprite(overlay.generateTexture())
  helpContainer.addChild(goalOverlay)

  goalText = new PIXI.Text("Light this many crystals to beat the level", {font: "14px 'Futura'", fill: "white"})
  helpContainer.addChild(goalText)

  ## Add background as second child  - 500x300 in original size ##
  helpContainer.addChild(PIXI.Sprite.fromImage(@siteprefix + "assets/img/helpBackground.png"))
  
  close = new PIXI.Text("X", {font: "bold 20px Sans-Serif", fill: "gray"})
  close.position.x = 480
  close.position.y = 5
  close.interactive = true
  close.buttonMode = true
  close.click = ->
    window.stage.removeChild(window.helpContainer)
    window.gameOn = true
    return
  helpContainer.addChild(close)
  ## Store in container for an extra reference to this
  helpContainer.close = close.click

  title = new PIXI.Text("Prismatic", headerStyle)
  title.position.x = 200
  title.position.y = 10
  helpContainer.addChild(title)

  topContent = new PIXI.Text("Prismatic is a light-based color game. Get the right \nnumber of Crystals to light up for each color.", contentStyle)
  topContent.position.x = 20
  topContent.position.y = 40
  helpContainer.addChild(topContent)

  sparkIcon = PIXI.Sprite.fromImage(@siteprefix + "assets/img/spark.png")
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

  prismIcon = PIXI.Sprite.fromImage(@siteprefix + "assets/img/hex-back.png")
  prismIcon.position.x = 50
  prismIcon.position.y = 135
  prismIcon.scale.x = 0.25
  prismIcon.scale.y = 0.25
  helpContainer.addChild(prismIcon)

  prismsHeader = new PIXI.Text("Prisms", headerStyle)
  prismsHeader.position.x = 100
  prismsHeader.position.y = 140
  helpContainer.addChild(prismsHeader)

  prismsContent = new PIXI.Text(" - the basic piece. They channel light and rotate.", contentStyle)
  prismsContent.position.x = 150
  prismsContent.position.y = 140
  helpContainer.addChild(prismsContent)

  crystalIcon = PIXI.Sprite.fromImage(@siteprefix + "assets/img/crystal.png")
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

  @resize()
  return

### Makes and adds the win container. Called when the player beats this level ###
@makeWinGameContainer = ->
  @winContainer = new PIXI.DisplayObjectContainer()
  @winContainer.addChild(new PIXI.Sprite(new PIXI.Texture(PIXI.BaseTexture.fromImage(@siteprefix + "assets/img/helpBackground.png"), new PIXI.Rectangle(0,0,500,200))))
  @winContainer.position.x = 450
  @winContainer.position.y = 250

  headerStyle = {font:"bold 15px Futura", fill: "#6E6E6E"}
  contentStyle = {font: "15px Futura", fill: "#6E6E6E"}

  close = new PIXI.Text("X", {font: "bold 20px Sans-Serif", fill: "gray"})
  close.position.x = 480
  close.position.y = 5
  close.interactive = true
  close.buttonMode = true
  close.click = ->
    window.stage.removeChild(window.winContainer)
    window.winContainer = null
    window.gameOn = true
    window.showWinContainer = false ## Don't show when this player wins this map
    return
  @winContainer.addChild(close)

  title = new PIXI.Text("You Win!", headerStyle)
  title.position.x = 200
  title.position.y = 10
  @winContainer.addChild(title)

  topContent = new PIXI.Text("You beat level " + @level + " in " + @BOARD.moves + 
    " moves on " + @Game.asString(@difficulty) + " mode.", contentStyle)
  topContent.position.x = 20
  topContent.position.y = 40
  @winContainer.addChild(topContent)

  if @level < 50
    nextLvl = new PIXI.Text((@level + 1) + " >>", contentStyle)
    nextLvl.interactive = true
  else
    nextLvl = new PIXI.Text("Thank you for playing!", contentStyle)
    nextLvl.interactive = false
  nextLvl.buttonMode = true
  nextLvl.click = ->
    num = 
      if (window.level + 1 < 10)
        "0" + (window.level + 1)
      else
        "" + (window.level + 1)
    window.BOARDNAME = "board" + num
    window.menu.children[5].click()
    return
  nextLvl.position.x = 225
  nextLvl.position.y = 175
  @winContainer.addChild(nextLvl)

  @stage.addChild(@winContainer)
  @resize()
  return

### Load assets into cache ###
@preloadImages = ->
  assets = [@siteprefix + "assets/img/galaxy-28.jpg", @siteprefix + "assets/img/helpBackground.png", @siteprefix + "assets/img/icon_v2.png",
            @siteprefix + "assets/img/hex-back.png", @siteprefix +  "assets/img/core.png",
            @siteprefix + "assets/img/spark.png", @siteprefix + "assets/img/crystal.png",
            @siteprefix + "assets/img/connector_off.png", @siteprefix + "assets/img/connector_on.png"]
  loader = new PIXI.AssetLoader(assets)
  loader.onComplete = @initFinish
  loader.load()
  return

### Resizes the stage correctly ###
@resize = () ->
  margin = 0
  window.renderer.resize(window.innerWidth - margin, window.innerHeight - margin)
  
  newScale2 = Math.min(1, Math.max(0.5, window.innerHeight / 1500))
  newScale3 = newScale2 * 0.5

  ## Expand/contract the menu. Horizontal expansion/contraction is on middle sprite. Vertical is on whole menubackground container
  if @menu? and @menu.children.length > 0
    bck = @menu.children[0]
    icon = @menu.children[1]
    title = @menu.children[2]
    prevLvl = @menu.children[3]
    lvlText = @menu.children[4]
    nextLvl = @menu.children[5]
    resetButton = @menu.children[6]
    helpButton = @menu.children[7]
    easyButton = @menu.children[8]
    medButton = @menu.children[9]
    hardButton = @menu.children[10]
    goalContainer = @menu.children[@goalContainerIndex]

    ##Fix background image
    bck.scale.x = Math.max(window.innerWidth / bck.texture.baseTexture.width, 0.75) 
    bck.scale.y = Math.max((window.innerHeight) / bck.texture.baseTexture.height, 0.75) 

    ## Resize vertically
    menuHeight = 75
    @base.position.y =  menuHeight
    for col, cContainer of @colorContainers
      cContainer.position.y = menuHeight

    ## Scale all menu labels and buttons
    # lvlText.scale.x = lvlText.scale.y = newScale3
    # resetButton.scale.x = resetButton.scale.y = newScale3
    # selectLabel.scale.x = selectLabel.scale.y = newScale3
    # prevLvl.scale.x = prevLvl.scale.y = newScale3
    # nextLvl.scale.x = nextLvl.scale.y = newScale3
    # helpButton.scale.x = helpButton.scale.y = newScale3

    ## Move labels and buttons into place on x axis
    # Left justified elements
    menumargin = 20
    lvlPush = (n) -> 
      if @level + n >= 10
        5
      else
        0
    prevLvl.position.x = menumargin
    lvlText.position.x = prevLvl.position.x + 50 + lvlPush(-1)
    nextLvl.position.x = lvlText.position.x + 75 + lvlPush(0)
    resetButton.position.x =  nextLvl.position.x + 60 + lvlPush(1)

    ## Center Justified Elements
    icon.position.x = (window.innerWidth - 125)/2
    title.position.x = (window.innerWidth - 50)/2

    #Right Justified Elements
    helpButton.position.x = window.innerWidth - menumargin - 40
    hardButton.position.x = helpButton.position.x
    medButton.position.x = hardButton.position.x - 60
    easyButton.position.x = medButton.position.x - 60


    fixY = (comp, scale) ->
      comp.position.y = 45 * scale
      return

    fixY(icon, newScale3)
    icon.position.y -= 4
    fixY(title, newScale3)
    title.position.y -= 5
    fixY(lvlText, newScale3)
    fixY(resetButton, newScale3)
    fixY(helpButton, newScale3)
    fixY(nextLvl, newScale3)
    fixY(prevLvl, newScale3)

    fixYRowTwo = (comp, scale) ->
      comp.position.y = 45 * scale + 35
      return

    fixYRowTwo(easyButton, newScale3)
    fixYRowTwo(medButton, newScale3)
    fixYRowTwo(hardButton, newScale3)

    if goalContainer?
      ## Fix goalContainer
      goalContainer.position.y = -75
      goalContainer.position.x = helpButton.position.x -  90 * goalContainer.count

  ## Fix the help menu. resize the back opacity layer, reposition the rest
  if @helpContainer?
    helpWidth = @helpContainer.children[3].getLocalBounds().width
    helpHeight = @helpContainer.children[3].getLocalBounds().height
    @helpContainer.position.x = (window.innerWidth - helpWidth) / 2
    @helpContainer.position.y = (window.innerHeight - menuHeight - helpHeight) / 2 + menuHeight
    helpBack = @helpContainer.children[0]
    helpBack.position.x = -@helpContainer.position.x - 15
    helpBack.position.y = -@helpContainer.position.y - 15
    helpBack.height = window.innerHeight + 25
    helpBack.width = window.innerWidth + 25
    if goalContainer?
      helpGoalHighlight = @helpContainer.children[1]
      helpGoalHighlight.position.y = -@helpContainer.position.y
      helpGoalHighlight.scale.x = 90*goalContainer.count / 100
      helpGoalHighlight.position.x = goalContainer.position.x - @helpContainer.position.x - helpGoalHighlight.scale.x * 11 - 20
      helpGoalText = @helpContainer.children[2]
      helpGoalText.position.y = -@helpContainer.position.y + 40
      helpGoalText.position.x = helpGoalHighlight.position.x + 8 + 9 * goalContainer.count



  ## Fix the win menu. No resizing, just reposition
  if @winContainer? 
    helpWidth = @winContainer.getLocalBounds().width
    helpHeight = @winContainer.getLocalBounds().height
    @winContainer.position.x = (window.innerWidth - helpWidth) / 2
    @winContainer.position.y = (window.innerHeight - menuHeight - helpHeight) / 2 + menuHeight

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
      goalContainer = @menu.children[@goalContainerIndex]
      isWin = true ## True if this user has won - every goal set.
      for pan in goalContainer.children
        for spr in pan.children
          if spr instanceof PIXI.Text and spr.color.toUpperCase() of curLit
            spr.setText(curLit[spr.color.toUpperCase()] + spr.text.substring(1))
            if curLit[spr.color.toUpperCase()] < parseInt(spr.text.substring(2))
              isWin = false

      if isWin and (not @winContainer?) and @showWinContainer
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
              connector.texture = PIXI.Texture.fromImage(@siteprefix + "assets/img/connector_on.png")
              @toLit(connector)
              for nConnector in n.colorPanels
                if nConnector.side is n.indexLinked(h) and not nConnector.linked
                  nConnector.texture = PIXI.Texture.fromImage(@siteprefix + "assets/img/connector_on.png")
                  @toLit(nConnector)
            else if connector.linked and (c not in hLit or n? and n.colorOfSide(n.indexLinked(h)) isnt c)
              connector.texture = PIXI.Texture.fromImage(@siteprefix + "assets/img/connector_off.png")
              @toUnlit(connector)
              if n?
                for nConnector in n.colorPanels
                  if nConnector.side is n.indexLinked(h) and nConnector.linked
                    nConnector.texture = PIXI.Texture.fromImage(@siteprefix + "assets/img/connector_off.png")
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
  Board.loadBoard(window.BOARDNAME)
  return

## Font for text in the menu
@menuHeaderStyle = {font:"bold 20px 'Futura' ", fill:"white"}
@menuContentStyle = {font: "16px 'Futura' ", fill: "gray"}

## Index of the goal container on the menu
@goalContainerIndex = 11

### Creates the menu. Has children:
    0) background image
    1) icon
    2) title
    3) prev level
    4) level text
    5) next level
    6) reset level
    7) help button
    8) easy button
    9) med button
    10) hard button
    11) goal container
###
@initMenu = () ->
  ## Create the background itself
  bck = PIXI.Sprite.fromImage(@siteprefix + "assets/img/galaxy-28.jpg")
  @menu.addChild(bck)
  
  menuicon = PIXI.Sprite.fromImage(@siteprefix + "assets/img/icon_v2.png")
  menuicon.scale.x = menuicon.scale.y = 0.5
  @menu.addChild(menuicon)

  titleText = new PIXI.Text("prismatic", @menuHeaderStyle)
  @menu.addChild(titleText)

  if @level > 1
    prevLvl = new PIXI.Text("<< " + (@level - 1), @menuContentStyle)
    prevLvl.interactive = true
    
  else
    prevLvl = new PIXI.Text("     ", @menuContentStyle)
    prevLvl.interactive = false
  prevLvl.buttonMode = true
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

  lvlText = new PIXI.Text(@level + " of 50", @menuContentStyle)
  @menu.addChild(lvlText)

  if @level < 50
    nextLvl = new PIXI.Text((@level + 1) + " >>", @menuContentStyle)
    nextLvl.interactive = true
  else
    nextLvl = new PIXI.Text("     ", @menuContentStyle)
    nextLvl.interactive = false
  nextLvl.buttonMode = true
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

  resetButton = new PIXI.Text("Reset", @menuContentStyle)
  resetButton.interactive = true
  resetButton.buttonMode = true
  resetButton.click = ->
    if window.winContainer isnt undefined and window.winContainer isnt null
      window.stage.removeChild(window.winContainer)
    window.winContainer = null
    window.gameOn = true
    window.showWinContainer = true
    window.clearBoard()
    Board.loadBoard(window.BOARDNAME)
    window.updateMenu()
    return
  resetBack = new PIXI.Sprite(@backBox(50 , 25))
  resetBack.position.x = -15
  resetBack.position.y = -11
  resetButton.addChild(resetBack)  
  resetBorder = new PIXI.Sprite(@borderBox(50 , 25))
  resetBorder.position.x = -15
  resetBorder.position.y = -11
  resetButton.addChild(resetBorder)
  @menu.addChild(resetButton)

  helpButton = new PIXI.Text("Help", @menuContentStyle)
  helpButton.interactive = true
  helpButton.buttonMode = true
  helpButton.click = ->
    if (window.winContainer is undefined or window.winContainer is null)
      if window.gameOn
        window.gameOn = false
        window.stage.addChild(window.helpContainer)
      else
        helpContainer.close()
    return
  helpBack = new PIXI.Sprite(@backBox(45 , 25))
  helpBack.position.x = -15
  helpBack.position.y = -11
  helpButton.addChild(helpBack)
  helpBorder = new PIXI.Sprite(@borderBox(45 , 25))
  helpBorder.position.x = -15
  helpBorder.position.y = -11
  helpButton.addChild(helpBorder)
  @menu.addChild(helpButton)

  easyButton = new PIXI.Text("Easy", @menuContentStyle)
  easyButton.interactive = true
  easyButton.buttonMode = true
  easyButton.click = ->
    if window.difficulty isnt window.Game.EASY
       window.difficulty = Game.EASY
       resetButton.click()
    return
  easyBorder = new PIXI.Sprite(@borderBox(45 , 25))
  easyBorder.position.x = -15
  easyBorder.position.y = -11
  easyButton.addChild(easyBorder)
  @menu.addChild(easyButton)

  medButton = new PIXI.Text("Med", @menuContentStyle)
  medButton.interactive = true
  medButton.buttonMode = true
  medButton.click = ->
    if window.difficulty isnt Game.MEDIUM
       window.difficulty = Game.MEDIUM
       resetButton.click()
    return
  medBorder = new PIXI.Sprite(@borderBox(45 , 25))
  medBorder.position.x = -15
  medBorder.position.y = -11
  medButton.addChild(medBorder)
  @menu.addChild(medButton)

  hardButton = new PIXI.Text("Hard", @menuContentStyle)
  hardButton.interactive = true
  hardButton.buttonMode = true
  hardButton.click = ->
    if window.difficulty isnt Game.HARD
       window.difficulty = Game.HARD
       resetButton.click()
    return
  hardBorder = new PIXI.Sprite(@borderBox(45 , 25))
  hardBorder.position.x = -15
  hardBorder.position.y = -11
  hardButton.addChild(hardBorder)
  @menu.addChild(hardButton)

  ## Add goal components to menu
  @menu.addChild(@goalContainer)
  @updateMenu()
  return

@g = new PIXI.Graphics()
## Returns a simple gray box texture with the given dimensions
@borderBox = (width, height) ->
  @g.clear()
  @g.lineStyle(1.5, 0x777777, 1)
  @g.drawRect(0,0,width, height)
  return @g.generateTexture()

## Returns a simple white box (filled) with the given dimensions
@backBox = (width, height) ->
  @g.clear()
  @g.beginFill(0xFFFFFF, 0.5)
  @g.drawRect(0,0,width,height)
  @g.endFill()
  return @g.generateTexture()


## Updates the menu to the most recent text for level - assumes initted
@updateMenu = () ->
  lvlText = @menu.children[4]
  lvlText.setText(@level + " of 50")
  prevLvl = @menu.children[3]
  if prevLvl.children.length > 0
    prevLvl.removeChild(prevLvl.getChildAt(0))  ## Get rid of the old boxes
  if @level > 1
    prevLvl.setText("<< " + (@level - 1))
    prevLvl.interactive = true
    size = 
      if @level > 10
        55
      else
        45
    prevBorder = new PIXI.Sprite(@borderBox(size , 25))
    prevBorder.position.x = -15
    prevBorder.position.y = -11
    prevLvl.addChild(prevBorder)
  else
    prevLvl.setText("     ")
    prevLvl.interactive = false
  nextLvl = @menu.children[5]
  if nextLvl.children.length > 0
    nextLvl.removeChild(nextLvl.getChildAt(0))   ## Get rid of the old boxes
  if @level < 50
    nextLvl.setText((@level + 1) + " >>")
    nextLvl.interactive = true
    size = 
      if @level > 8
        55
      else
        45
    nextBorder = new PIXI.Sprite(@borderBox(size , 25))
    nextBorder.position.x = -15
    nextBorder.position.y = -11
    nextLvl.addChild(nextBorder)
  else
    nextLvl.setText("     ")
    nextLvl.interactive = false

  backingTex = @backBox(45, 25)
  ## Set the correct level button selected
  difficultyButtons = [@menu.children[8], @menu.children[9], @menu.children[10]]
  for b in difficultyButtons
    try
      b.removeChild(b.getChildAt(1))
    catch
  diffBorder = new PIXI.Sprite(backingTex)
  diffBorder.position.x = -15
  diffBorder.position.y = -11
  difficultyButtons[@difficulty].addChild(diffBorder)
    

  return

### Clears board and associated sprites from screen, usually in anticipation of new board being loaded ###
@clearBoard = () ->
  sprToRemove = []
  for pan in @menu.children[@goalContainerIndex].children
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
    @menu.children[7].click()
    @initted = true

  ## Create the goal board on the right of the main board
  colors = window.BOARD.colorsPresent()

  ## Array of goals, ex: ["RED", "RED", "BLUE", "GREEN"]
  goalArr = []
  for c in colors
    for i in [1 .. window.BOARD[c.toUpperCase()]] by 1
      goalArr.push(c.toUpperCase())

  fix = (h, cir) ->
    i = 0
    while not h.colorCircle.matches(ColorCircle.fromArray(val))
      h.rotate()
      i++
      if i == 6
        console.error("Rotated six times, no match!")
        break
      h.targetRotation = 0
    return

  ## If on easy, remove half of the goal requirements, rounded down
  ## Also rotate and lock all of the locked tiles
  if @difficulty is @Game.EASY
    removeCount = goalArr.length /2
    for key, val of @BOARD.Locked
      h = @BOARD.getHex(Loc.fromString(key))
      fix(h, ColorCircle.fromArray(val))
      h.isLocked = true

  ## If on medium, remove a fourth of the goal requirements, rounded down
  ## Also rotate and lock half of the locked tiles. (rounded up)
  if @difficulty is @Game.MEDIUM
    removeCount = goalArr.length / 4
    use = true
    for key, val of @BOARD.Locked
      if use
        h = @BOARD.getHex(Loc.fromString(key))
        fix(h, ColorCircle.fromArray(val))
        h.isLocked = true
      use = not use

  ## If on hard, remove no requirements
  if @difficulty is @Game.HARD
    removeCount = 0

  @BOARD.moves = 0

  for i in [1 .. removeCount] by 1
    r = Math.floor(Math.random() * goalArr.length) ## Unused
    window.BOARD[goalArr[i]]--
    goalArr.splice(i, 1)

  ## Goal board has crystals.
  goalBoard = new Board(colors.length,1)
  i = 0
  for color in colors
    goalCount = window.BOARD[color.toUpperCase()]
    if(goalCount > 0)
      c = new Crystal(goalBoard, new Loc(i, 0))
      c.lit = color
      i++
  
  @goalContainer.count = Math.max(i,3)
  spaceCoef = 5/6
  pushCoef = 1/4
  for c in goalBoard.allHexesOfClass("Crystal")
    ## Create sprites for crystal
    spr = PIXI.Sprite.fromImage(@siteprefix + "assets/img/crystal.png")
    spr.lit = false
    spr.color = c.lit
    spr.hex = c
    spr.position.x = c.loc.row * @hexRad * spaceCoef  ## Leaves some space for text between sprites
    spr.anchor.x = spr.anchor.y = 0.5
    spr.scale.x = spr.scale.y = 0.25
    @goalContainer[c.lit.toUpperCase()].addChild(spr)
    goalCount = window.BOARD[c.lit.toUpperCase()]
    @goalContainer[c.lit.toUpperCase()].goalCount = goalCount
    goalStyle = @menuContentStyle
    text = new PIXI.Text("0/" + goalCount, goalStyle)
    text.position.x = c.loc.row * @hexRad * spaceCoef + @hexRad * pushCoef
    text.position.y = -12
    text.color = c.lit
    @goalContainer[c.lit.toUpperCase()].addChild(text)

  for c in colors
    if window.BOARD[c.toUpperCase()] is 0
      @colorContainers[c.toUpperCase()].alpha = 0
      for spark in window.BOARD.allHexesOfClass("Spark")
        s = spark.getAvailableColors()
        s.splice(s.indexOf(c), 1)
        spark.setAvailableColors(s)
    else
      @colorContainers[c.toUpperCase()].alpha = 1

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
      spr = PIXI.Sprite.fromImage(@siteprefix + "assets/img/hex-back.png")
    else if hex instanceof Crystal
      spr = PIXI.Sprite.fromImage(@siteprefix + "assets/img/crystal.png")
    else if hex instanceof Spark
      spr = PIXI.Sprite.fromImage(@siteprefix + "assets/img/spark.png")
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
      coreRad = 32        # Radius of the core hexagon
      cumulative = [0.01, -0.02, 0.01, 0, 0, 0.02]
      for i in [0 .. Hex.SIDES - 1] by 1
        c = hex.colorOfSide(i)
        if(not isNaN(c))
          c = Color.asString(c).toUpperCase()
        else
          c = c.toUpperCase()
        point = new PIXI.Point(coreRad * Math.cos((i - 2) * 2 * Math.PI / Hex.SIDES + radTo60Degree/2 + cumulative[i]), 
                               coreRad * Math.sin((i - 2) * 2 * Math.PI / Hex.SIDES + radTo60Degree/2 + cumulative[i]))
        cr = PIXI.Sprite.fromImage(@siteprefix + "assets/img/connector_off.png")
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
          core = PIXI.Sprite.fromImage(@siteprefix + "assets/img/core.png")
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
    if hex.isLocked
      backpanel.interactive = false
      spr.tint = 0xFFFF21
    else
      backpanel.interactive = true
    backpanel.click = (event) -> 
      if window.gameOn
        if not event.originalEvent.shiftKey
          hex.click()
        else
          hex.anticlick()
      return

  return hex.panel