### Set up a PIXI stage ###
@init = ->
  
  @stage = new PIXI.Stage(0x295266)
  canvas = document.getElementById("game-canvas")
  renderer = PIXI.autoDetectRenderer(canvas.width, canvas.height, canvas)
  @animate = () ->
    requestAnimFrame( @animate )
    renderer.render(@stage)
    return
  requestAnimFrame( @animate )
# stage = new PIXI.Stage(0x66FF99);
#   renderer = PIXI.autoDetectRenderer(400, 300);
#   document.body.appendChild(renderer.view);
  
  # @hexPanel = new PIXI.DisplayObjectContainer()
  # @stage.addChild(@hexPanel)

  # #Add a dummy sprite - not working. Why?
  # d = PIXI.Texture.fromImage("assets/img/circle_blue.png")
  # dS = new PIXI.Sprite(d)
  # dS.position.x = 200
  # dS.position.y = 200
  # @hexPanel.addChild(dS)

  # @renderer.render(stage);
  return