
## The frame count the window is on ##
@count = 0

### Moves the connector to the correct lit layer ###
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

### Moves the connector to the correct unlit layer ###
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

### Creates a frame offset for the each color ###
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
  for cont in @goalContainer.children
    if cont.children.length >= 2 and cont.filters.length >= 2
      pulse = cont.filters[1]
      correspondCont = @colorContainers[cont.children[0].color.toUpperCase()].lit
      c = (count + correspondCont.pulseOffset)/correspondCont.pulseLength
      m = pulse.matrix
      if parseInt(cont.children[1].text.substring(0, 1)) >= parseInt(cont.children[1].text.substring(2))
        m[0] = Math.abs(Math.sin(c * 2 * Math.PI)) * 0.5 + 0.5
        m[5] = Math.abs(Math.sin(c * 2 * Math.PI)) * 0.5 + 0.5
        m[10] = Math.abs(Math.sin(c * 2 * Math.PI)) * 0.5 + 0.5
        m[15] = Math.abs(Math.sin(c * 2 * Math.PI)) * 0.25 + 0.75
      else
        m[0] = 1
        m[5] = 1
        m[10] = 1
        m[15] = 1
      pulse.matrix = m
  return

### The animation function. Called by pixi and requests to be recalled ###
@animate = () ->
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

      ##Every so often, rotate the color container orders - TODO
      # if window.count %% 250 is 0
      #   contain = @colorContainersArr[0]
      #   @colorContainersArr = @colorContainersArr.splice(1, @colorContainersArr.length - 1)
      #   for a in @colorContainersArr
      #     console.log(a.color)
      #   @stage.removeChild(contain)
      #   @colorContainersArr.push(contain)
      #   for a in @colorContainersArr
      #     console.log(a.color)
      #   @stage.addChild(contain)
      

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
          ## Adjust opacity of cores
          for col, core of h.cores
            if col.toLowerCase() not in hLit and core.alpha > 0
              core.alpha = 0
            else if col.toLowerCase() in hLit and core.alpha is 0
              core.alpha = 0.75

        nS = h.getNeighborsWithBlanks()
        for panel in h.colorPanels
          col = panel.color.toLowerCase()
          for connector in panel.children
            for side in connector.sides
              n = nS[side]

              if n? and col in hLit and n.colorOfSide(n.indexLinked(h)) is col and not connector.linked
                @toLit(connector)
                for nPanel in n.colorPanels
                  for nConnector in nPanel.children
                    for nSide in nConnector.sides
                      if nSide is n.indexLinked(h) and not nConnector.linked
                        @toLit(nConnector)
              else if connector.linked and col not in hLit
                @toUnlit(connector)
                if n?
                  for nPanel in n.colorPanels
                    for nConnector in nPanel.children
                      for nSide in nConnector.sides
                        if nSide is n.indexLinked(h) and not nConnector.linked
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
          if Math.abs(h.targetRotation - h.currentRotation) < tolerance
            inc = (h.targetRotation - h.currentRotation)
            h.backPanel.rotation += inc * radTo60Degree
            h.currentRotation += inc
            for value in h.colorPanels
              value.rotation += inc * radTo60Degree
              ## Update side index of each sprite
              for spr in value.children
                newSides = []
                for side in spr.sides
                  newSides.push((side + (h.currentRotation - h.prevRotation)) %% Hex.SIDES)
                spr.sides = newSides
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

### The window resize function. Called whenever the window is resized, and by some other functions after they add elements ###
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
    helpWidth = @helpContainer.children[3].getLocalBounds().width * @helpContainer.children[3].scale.x
    helpHeight = @helpContainer.children[3].getLocalBounds().height * @helpContainer.children[3].scale.y
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
      effectiveCount = Math.max(3, goalContainer.count)
      helpGoalHighlight.scale.x = 90*effectiveCount / 100
      helpGoalHighlight.position.x = (helpButton.position.x -  90 * effectiveCount) - @helpContainer.position.x - helpGoalHighlight.scale.x * 11 - 20
      helpGoalText = @helpContainer.children[2]
      helpGoalText.position.y = -@helpContainer.position.y + 40
      helpGoalText.position.x = helpGoalHighlight.position.x + 8 + 9 * effectiveCount



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