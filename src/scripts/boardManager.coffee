### Front end maintenence on board objects - loading, creating view for, etc ###

### Clears board and associated sprites from screen, usually in anticipation of new board being loaded ###
@clearBoard = () ->
  sprToRemove = []
  for pan in @menu.children[@goalContainerIndex].children
    for spr in pan.children
      sprToRemove.push(spr)
  for spr in @stage.children[1].children
    sprToRemove.push(spr)
  for i in [1 ..(@stage.children.length - 1)] by 1
    for pan in @stage.children[i].children
      for j in [1 .. pan.children.length] by 1
        spr = pan.children[j]
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
  
  @goalContainer.count = i
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

  ## Remove colors that aren't part of the solution from all sparks, 
  ## set alpha of that container to 0
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

    ## Fix and rotate this connector for the given side. Also adds to the given panel
    fixAndRotateConnector = (connector, side, color, panel) ->
      connector.linked = false
      connector.anchor.x = 0.5
      connector.anchor.y = 0.5
      connector.color = color
      connector.rotation = side * radTo60Degree
      connector.panel = panel
      panel.addChild(connector)
      return

    ## Create color bridges for prism
    if hex instanceof Prism
      for c in Color.regularColors()
        if hex.colorCount(c) > 0
          ## Create a panel for this set of connectors (panel per color)
          cpanel = new PIXI.DisplayObjectContainer()
          cpanel.position.x = hex.loc.col * @hexRad * 3/4 * 1.11 + @hexRad * (5/8)
          cpanel.position.y = hex.loc.row * @hexRad + @hexRad * (5/8)
          cpanel.position.y +=  @hexRad/2 if hex.loc.col % 2 == 1
          cpanel.pivot.x = 0.5
          cpanel.pivot.y = 0.5
          cpanel.color = c
          sidePanels.push(cpanel)

          ## Find the sides this color is on
          indices = []
          for i in [0 .. (Hex.SIDES - 1)] by 1
            if hex.colorOfSide(i) is c.toLowerCase()
              indices.push(i)
          if indices.length == 1
            con = PIXI.Sprite.fromImage(@siteprefix + "assets/img/connector-none.png")
            con.sides = [indices[0]]
            fixAndRotateConnector(con, indices[0], c, cpanel)
          else
            ##Create pair wise combinations of indices as a 2 digit number, greater number first
            pairCombos = []
            for i in [0 .. (indices.length - 1)] by 1
              for j in [(i + 1) .. (indices.length - 1)] by 1
                pairCombos.push(indices[j] * 10 + indices[i])
            ## For each pair combination, add its corresponding connector
            for combo in pairCombos
              sideOne = combo % 10
              sideTwo = Math.floor(combo / 10)
              diff = (sideTwo - sideOne) %% Hex.SIDES
              theSide = -1
              switch diff
                when 0
                  con = PIXI.Sprite.fromImage(@siteprefix + "assets/img/connector-opposite.png")
                  theSide = sideTwo
                when 1
                  con = PIXI.Sprite.fromImage(@siteprefix + "assets/img/connector-adjacent.png")
                  theSide = sideTwo
                when 2
                  con = PIXI.Sprite.fromImage(@siteprefix + "assets/img/connector-far-neighbor.png")
                  theSide = sideTwo
                when 3
                  con = PIXI.Sprite.fromImage(@siteprefix + "assets/img/connector-opposite.png")
                  theSide = sideOne
                when 4
                  con = PIXI.Sprite.fromImage(@siteprefix + "assets/img/connector-far-neighbor.png")
                  theSide = sideOne
                when 5
                  con = PIXI.Sprite.fromImage(@siteprefix + "assets/img/connector-adjacent.png")
                  theSide = sideOne
              con.sides = [sideOne, sideTwo]
              fixAndRotateConnector(con, theSide, c, cpanel)

          ## Add to unlit (for now)
          @colorContainers[c].unlit.addChild(cpanel)


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

    ## Add to back container, if prism
    if hex instanceof Prism
      @base.addChild(backpanel)

    #Add a click listener
    if hex.isLocked
      backpanel.interactive = false
      spr.alpha = 0
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