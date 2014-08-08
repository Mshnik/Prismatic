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