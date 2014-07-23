### Set up a PIXI stage ###
@init = ->
  console.log("Init Called")
  stage = new PIXI.Stage(0x295266)
  canvas = document.getElementById("game-canvas")
  renderer = PIXI.autoDetectRenderer(canvas.width, canvas.height, canvas)
  renderer.render(stage);