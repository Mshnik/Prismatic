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
  backr = PIXI.Sprite.fromImage(@siteprefix + "assets/img/helpBackground.png")
  backr.height = 365
  helpContainer.addChild(backr)
  
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

  topContent = new PIXI.Text("Prismatic is a light-bending puzzle game. Light up Crystals \nby sending light from sparks through prisms.", contentStyle)
  topContent.position.x = 20
  topContent.position.y = 40
  helpContainer.addChild(topContent)

  sparkIcon = PIXI.Sprite.fromImage(@siteprefix + "assets/img/spark.png")
  sparkIcon.position.x = 50
  sparkIcon.position.y = 98
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
  prismIcon.position.y = 155
  prismIcon.scale.x = 0.25
  prismIcon.scale.y = 0.25
  helpContainer.addChild(prismIcon)

  prismsHeader = new PIXI.Text("Prisms", headerStyle)
  prismsHeader.position.x = 100
  prismsHeader.position.y = 140
  helpContainer.addChild(prismsHeader)

  prismsContent = new PIXI.Text("           - the basic piece. They channel light and rotate.\n" +
                                "Prisms outlined in yellow are locked in place.\nThey are locked for a reason.", contentStyle)
  prismsContent.position.x = 100
  prismsContent.position.y = 140
  helpContainer.addChild(prismsContent)

  crystalIcon = PIXI.Sprite.fromImage(@siteprefix + "assets/img/crystal.png")
  crystalIcon.position.x = 50
  crystalIcon.position.y = 218
  crystalIcon.scale.x = 0.25
  crystalIcon.scale.y = 0.25
  helpContainer.addChild(crystalIcon)

  crystalsHeader = new PIXI.Text("Crystals", headerStyle)
  crystalsHeader.position.x = 100
  crystalsHeader.position.y = 220
  helpContainer.addChild(crystalsHeader)

  crystalsContent = new PIXI.Text("              - the end goal. They recieve light.\n" + 
                                  "Light the crystals the given combination of colors \nto progress to the next level.", contentStyle)
  crystalsContent.position.x = 100
  crystalsContent.position.y = 220
  helpContainer.addChild(crystalsContent)

  bottomContent = new PIXI.Text("> Click on Sparks to change their color. \n> Click on Prisms to rotate their alignment.", contentStyle)
  bottomContent.position.x = 20
  bottomContent.position.y = 295
  helpContainer.addChild(bottomContent)

  tagText = new PIXI.Text("created by Michael Patashnik - Mgpshnik@gmail.com", {font: "italic 10px Sans-Serif", fill: "gray"})
  tagText.position.x = 120
  tagText.position.y = 342
  helpContainer.addChild(tagText)

  @resize()
  return