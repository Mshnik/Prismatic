### Set up a PIXI stage ###
stage = new PIXI.STAGE(0x888888)
renderer = PIXI.autoDetectRenderer(640,480)
gameContainer = new PIXI.DisplayObjectContainer()
stage.addChild(gameContainer)
document.body.appendChild(renderer.view)