### Creates a dummy board and adds to scope. Mainly for testing ###
@createDummyBoard = () ->
  @BOARD = @Board.makeBoard(4,9,3)
  return


### Draws the Board in BOARD on the stage. ###
@drawBoard = () ->
 for h in @BOARD.allHexes()
  @stage.addChild(spriteForHex(h))
 @renderer.render(stage)
 return

### Creates a single sprite for a hex and adds it to stage ###
@spriteForHex = (hex) ->
  if typeof hex.sprite is "undefined" or hex.sprite is null 
    hexback = PIXI.Texture.fromImage("assets/img/hex-back.png")
    spr = new PIXI.Sprite(hexback)
    spr.position.x = hex.loc.col * 50
    spr.position.y = hex.loc.row * 50
    hex.sprite = spr
  return hex.sprite