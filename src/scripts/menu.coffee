### Code dealing with creation and maintenence on the menu ###

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
  
  menuicon = PIXI.Sprite.fromImage(@siteprefix + "assets/img/Icon_v2.png")
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
  prevLvl.tap = prevLvl.click
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
  nextLvl.tap = nextLvl.click
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
  resetButton.tap = resetButton.click
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
  helpButton.tap = helpButton.click
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
  easyButton.tap = easyButton.click
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
  medButton.tap = medButton.click
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
  hardButton.tap = hardButton.click
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