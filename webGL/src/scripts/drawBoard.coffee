  g = new Graphics()
  g.lineStyle(2, 0, 0)

  ## Draw outer rectangle
  r = g.getLocalBounds()
  drawRect(r.x, r.y, r.width, r.height)
