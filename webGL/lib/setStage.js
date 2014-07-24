// Generated by CoffeeScript 1.7.1

/* Begins init processing */

(function() {
  this.init = function() {
    return this.initStart();
  };


  /* Set up a PIXI stage - part before asset loading */

  this.initStart = function() {
    var canvas;
    this.stage = new PIXI.Stage(0x295266, true);
    this.stage.scale.x = 0.5;
    this.stage.scale.y = 0.5;
    canvas = document.getElementById("game-canvas");
    this.renderer = PIXI.autoDetectRenderer(canvas.width, canvas.height, canvas);
    PIXI.scaleModes.DEFAULT = PIXI.scaleModes.NEAREST;
    PIXI.scaleModes.DEFAULT = PIXI.scaleModes.LINEAR;
    preloadImages();
  };


  /* Load assets into cache */

  this.preloadImages = function() {
    var assets, loader;
    assets = ["assets/img/hex-back.png", "assets/img/hex-lit.png", "assets/img/circle_blue.png", "assets/img/circle_red.png", "assets/img/circle_green.png"];
    loader = new PIXI.AssetLoader(assets);
    loader.onComplete = this.initFinish;
    loader.load();
  };


  /* Animates the board and requests another frame */


  /* Finish initing after assets are loaded */

  this.initFinish = function() {
    var animate;
    animate = function() {
      var h, inc, radTo60Degree, rotSpeed, tolerance, _i, _len, _ref;
      rotSpeed = 1 / 10;
      tolerance = 0.000001;
      radTo60Degree = 1.04719755;
      if ((this.BOARD != null)) {
        _ref = this.BOARD.allHexes();
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          h = _ref[_i];
          if (h.isLit().length > 0 && !h.panel.children[0].lit) {
            h.panel.children[0].texture = PIXI.Texture.fromImage("assets/img/hex-lit.png");
          }
          if (h.isLit().length === 0 && h.panel.children[0].lit) {
            h.panel.children[0].texture = PIXI.Texture.fromImage("assets/img/hex-back.png");
          }

          /* Rotation of a prism - finds a prism that wants to rotate and rotates it a bit.
              If this is the first notification that this prism wants to rotate, stops providing light.
              If the prism is now done rotating, starts providing light again
           */
          if (h instanceof Prism && h.currentRotation !== h.targetRotation) {
            if (h.canLight) {
              h.canLight = false;
              h.light();
            }
            inc = (h.targetRotation - h.prevRotation) * rotSpeed;
            h.panel.rotation += inc * radTo60Degree;
            h.currentRotation += inc;
            if (Math.abs(h.targetRotation - h.currentRotation) < tolerance) {
              inc = h.targetRotation - h.currentRotation;
              h.panel.rotation += inc * radTo60Degree;
              h.currentRotation += inc;
              h.prevRotation = h.currentRotation;
              h.canLight = true;
              h.light();
            }
          }
        }
      }
      requestAnimFrame(animate);
      this.renderer.render(this.stage);
    };
    requestAnimFrame(animate);
    window.createDummyBoard();
    window.drawBoard();
  };


  /* Creates a dummy board and adds to scope. Mainly for testing */

  this.createDummyBoard = function() {
    this.BOARD = this.Board.makeBoard(4, 9, 3);
    this.BOARD.relight();
  };


  /* Draws the Board in BOARD on the stage. */

  this.drawBoard = function() {
    var h, _i, _len, _ref;
    _ref = this.BOARD.allHexes();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      h = _ref[_i];
      this.createSpriteForHex(h);
    }
  };

  this.hexRad = 53;


  /* Creates a single sprite for a hex and adds it to stage */

  this.createSpriteForHex = function(hex) {
    var c, cr, i, nudge, panel, point, shrink, spr, _i, _ref;
    if (typeof hex.panel === "undefined" || hex.panel === null) {
      panel = new PIXI.DisplayObjectContainer();
      panel.position.x = hex.loc.col * this.hexRad * 3 / 4 * 1.11 + this.hexRad / 2;
      panel.position.y = hex.loc.row * this.hexRad + this.hexRad / 2;
      if (hex.loc.col % 2 === 1) {
        panel.position.y += this.hexRad / 2;
      }
      panel.pivot.x = 0.5;
      panel.pivot.y = 0.5;
      spr = PIXI.Sprite.fromImage("assets/img/hex-back.png");
      spr.lit = false;
      spr.anchor.x = 0.5;
      spr.anchor.y = 0.5;
      spr.scale.x = 0.078;
      spr.scale.y = 0.078;
      panel.addChild(spr);
      panel.hex = spr;
      for (i = _i = 0, _ref = Hex.SIDES - 1; _i <= _ref; i = _i += 1) {
        c = hex.colorOfSide(i);
        if (!isNaN(c)) {
          c = Color.asString(c);
        }
        nudge = 0.54;
        shrink = 4;
        point = new PIXI.Point((this.hexRad / 2 - shrink) * Math.cos((i - 2) * 2 * Math.PI / Hex.SIDES + nudge), (this.hexRad / 2 - shrink) * Math.sin((i - 2) * 2 * Math.PI / Hex.SIDES + nudge));
        cr = PIXI.Sprite.fromImage("assets/img/circle_" + c.toLowerCase() + ".png");
        cr.anchor.x = 0.5;
        cr.anchor.y = 0.5;
        cr.scale.x = 0.078;
        cr.scale.y = 0.078;
        cr.position.x = point.x;
        cr.position.y = point.y;
        panel.addChild(cr);
      }
      hex.panel = panel;
      panel.hex = hex;
      panel.interactive = true;
      panel.click = function() {
        hex.click();
      };
      this.stage.addChild(panel);
    }
    return hex.panel;
  };

}).call(this);
