### Creates a dummy board and adds to scope. Mainly for testing ###
@createDummyBoard = () ->
  @BOARD = @Board.makeBoard(4,9,3)
  return


### Draws the Board in BOARD on the stage. ###
@drawBoard = () ->
 for h in @BOARD.allHexes()
  @stage.addChild(spriteForHex(h))
 return

@hexRad = 53

### Creates a single sprite for a hex and adds it to stage ###
@spriteForHex = (hex) ->
  if typeof hex.sprite is "undefined" or hex.sprite is null 
    hexback = PIXI.Texture.fromImage("assets/img/hex-back.png")
    spr = new PIXI.Sprite(hexback)
    spr.scale.x = 0.078
    spr.scale.y = 0.078
    # Center the anchor
    spr.anchor.x = 0.5
    spr.anchor.y = 0.5
    # # Set the position
    spr.position.x = hex.loc.col * @hexRad + @hexRad/2
    spr.position.y = hex.loc.row * @hexRad + @hexRad/2





    # Store in hex for later
    hex.sprite = spr
  return hex.sprite