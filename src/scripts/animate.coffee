
## The frame count the window is on ##
@count = 0

### Lights the hex ###
@updateLight = (hex) ->
  if hex instanceof Crystal
    hex.spr.tint = @Color.hexValueForLit(hex.lit)
  else if hex instanceof Spark
    hex.spr.tint = @Color.hexValueForLit(hex.isLit()[0])
  else if hex instanceof Prism
    colorsLit = hex.isLit()
    for connector in hex.connectors
      if connector.color.toLowerCase() in colorsLit
        connector.tint = @Color.hexValueForLit(connector.color)
      else
        connector.tint = @Color.hexValueForUnlit(connector.color)
  else
    throw ("Incorrect call to updateLight")

## True if this user has won - every goal set.
checkForWin = () ->   
  curLit = @BOARD.crystalLitCount()
  goalContainer = @menu.children[@goalContainerIndex]
  for pan in goalContainer.children
    for spr in pan.children
      if spr instanceof PIXI.Text and spr.color.toUpperCase() of curLit
        spr.setText(curLit[spr.color.toUpperCase()] + spr.text.substring(1))
        if curLit[spr.color.toUpperCase()] < parseInt(spr.text.substring(2))
          return false
  return true

### The animation function. Called by pixi and requests to be recalled ###
@animate = () ->
    ## Color animation
    window.count += 1;  ## Frame count
    rotSpeed = 1/5
    tolerance = 0.000001 ## For floating point errors - difference below this is considered 'equal'
    radTo60Degree = 1.04719755 ## 1 radian * this coefficient = 60 degrees
    if (@BOARD?)
      ## Update text on goal
      curLit = @BOARD.crystalLitCount()
      goalContainer = @menu.children[@goalContainerIndex]
      if checkForWin() and (not @winContainer?) and @showWinContainer
        @gameOn = false
        @makeWinGameContainer()
      
      for h in @BOARD.allHexes()
        @updateLight(h)

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
          for value in h.connectors
            value.rotation += inc * radTo60Degree
          if Math.abs(h.targetRotation - h.currentRotation) < tolerance
            inc = (h.targetRotation - h.currentRotation)
            h.backPanel.rotation += inc * radTo60Degree
            h.currentRotation += inc
            for value in h.connectors
              value.rotation += inc * radTo60Degree
              ## Update side index of each sprite
              newSides = []
              for side in value.sides
                newSides.push((side + (h.currentRotation - h.prevRotation)) %% Hex.SIDES)
              value.sides = newSides
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
          @updateLight(h)
          h.toColor = ""
    requestAnimFrame(animate)
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
    scale = (1 / 130) * Math.min((window.innerHeight - menuHeight) / window.BOARD.getHeight(), window.innerWidth * 1.15 / window.BOARD.getWidth())
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
      subPaneArr = [cContainer.lit, cContainer.unlit]
      for subPane in subPaneArr
        dumSpr = subPane.children[0]
        dumSpr.position.x = -cContainer.position.x
        dumSpr.position.y = -cContainer.position.y
        dumSpr.height = (window.innerHeight + menuHeight)/scale
        dumSpr.width = window.innerWidth

  return