// Generated by CoffeeScript 1.7.1

/* Begins init processing */

(function() {
  this.init = function() {
    return this.initStart();
  };


  /* Set up a PIXI stage - part before asset loading */

  this.initStart = function() {
    var canvas, renderer;
    this.stage = new PIXI.Stage(0x295266);
    canvas = document.getElementById("game-canvas");
    renderer = PIXI.autoDetectRenderer(canvas.width, canvas.height, canvas);
    this.animate = function() {
      requestAnimFrame(this.animate);
      renderer.render(this.stage);
    };
    requestAnimFrame(this.animate);
    PIXI.scaleModes.DEFAULT = PIXI.scaleModes.NEAREST;
    preloadImages();
  };


  /* Load assets into cache */

  this.preloadImages = function() {
    var assets, loader;
    assets = ["assets/img/hex-back.png"];
    loader = new PIXI.AssetLoader(assets);
    loader.onComplete = this.initFinish;
    loader.load();
  };


  /* Finish initing after assets are loaded */

  this.initFinish = function() {
    window.createDummyBoard();
    window.drawBoard();
  };


  /* Creates a dummy board and adds to scope. Mainly for testing */

  this.createDummyBoard = function() {
    this.BOARD = this.Board.makeBoard(4, 9, 3);
  };


  /* Draws the Board in BOARD on the stage. */

  this.drawBoard = function() {
    var h, _i, _len, _ref;
    _ref = this.BOARD.allHexes();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      h = _ref[_i];
      this.spriteForHex(h);
    }
  };

  this.hexRad = 53;


  /* Creates a single sprite for a hex and adds it to stage */

  this.spriteForHex = function(hex) {
    var spr;
    if (typeof hex.sprite === "undefined" || hex.sprite === null) {
      spr = PIXI.Sprite.fromImage("assets/img/hex-back.png");
      spr.anchor.x = 0.5;
      spr.anchor.y = 0.5;
      spr.scale.x = 0.078;
      spr.scale.y = 0.078;
      spr.position.x = hex.loc.col * this.hexRad * 3 / 4 * 1.11 + this.hexRad / 2;
      spr.position.y = hex.loc.row * this.hexRad + this.hexRad / 2;
      if (hex.loc.col % 2 === 1) {
        spr.position.y += this.hexRad / 2;
      }
      spr.pivot.x = 0.5;
      spr.pivot.y = 0.5;
      this.stage.addChild(spr);
      hex.sprite = spr;
    }
    return hex.sprite;
  };

}).call(this);
