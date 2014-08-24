// Generated by CoffeeScript 1.7.1

/* Functions that actually init the game */


/* Begins init processing */

(function() {
  this.init = function() {
    return this.initStart();
  };


  /* Set up a PIXI stage - part before asset loading */

  this.initStart = function() {
    var c, cContainer, colr, dumTex, f, g, lit, margin, menuHeight, offset, pulse, unlit, _i, _j, _len, _len1, _ref, _ref1;
    this.stage = new PIXI.Stage(0x000000, true);
    margin = 0;
    this.renderer = PIXI.autoDetectRenderer(window.innerWidth - margin, window.innerHeight - margin);
    PIXI.scaleModes.DEFAULT = PIXI.scaleModes.NEAREST;
    this.menu = new PIXI.DisplayObjectContainer();
    this.stage.addChild(this.menu);
    menuHeight = 100;
    this.base = new PIXI.DisplayObjectContainer();
    this.base.position.y = menuHeight;
    this.stage.addChild(this.base);
    this.flat = new PIXI.ColorMatrixFilter();
    this.flat.matrix = [0.5, 0, 0, 0, 0, 0.5, 0, 0, 0, 0, 0.5, 0, 0, 0, 0, 1];
    offset = 0;
    g = new PIXI.Graphics();
    g.clear();
    g.lineStyle(0, 0xFFFFFF, 1);
    g.drawRect(0, 0, window.innerWidth, window.innerHeight);
    dumTex = g.generateTexture();
    this.colorContainers = {};
    this.colorContainersArr = [];
    _ref = Color.values();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      c = _ref[_i];
      colr = c;
      if (!isNaN(colr)) {
        colr = Color.asString(colr);
      }
      cContainer = new PIXI.DisplayObjectContainer();
      cContainer.position.y = menuHeight;
      cContainer.color = colr;
      f = new PIXI.ColorMatrixFilter();
      f.matrix = Color.matrixFor(colr);
      cContainer.filters = [f];
      unlit = new PIXI.DisplayObjectContainer();
      cContainer.addChild(unlit);
      unlit.addChild(new PIXI.Sprite(dumTex));
      lit = new PIXI.DisplayObjectContainer();
      cContainer.addChild(lit);
      lit.addChild(new PIXI.Sprite(dumTex));
      cContainer.unlit = unlit;
      if (colr !== "NONE") {
        cContainer.unlit.filters = [this.flat];
      } else {
        cContainer.unlit.filters = null;
      }
      cContainer.lit = lit;
      pulse = new PIXI.ColorMatrixFilter();
      pulse.matrix = [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1];
      cContainer.lit.pulseLength = 173;
      cContainer.lit.pulseOffset = offset;
      offset += 70;
      cContainer.lit.filters = [pulse];
      this.stage.addChild(cContainer);
      this.colorContainers[colr] = cContainer;
      this.colorContainersArr.push(cContainer);
    }
    this.goalContainer = new PIXI.DisplayObjectContainer();
    this.goalContainer.count = 0;
    offset = 0;
    _ref1 = Color.values();
    for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
      c = _ref1[_j];
      colr = c;
      if (!isNaN(colr)) {
        colr = Color.asString(colr);
      }
      cContainer = new PIXI.DisplayObjectContainer();
      cContainer.position.y = menuHeight;
      f = new PIXI.ColorMatrixFilter();
      f.matrix = Color.matrixFor(colr);
      pulse = new PIXI.ColorMatrixFilter();
      pulse.matrix = [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1];
      cContainer.filters = [f, pulse];
      this.goalContainer[colr] = cContainer;
      this.goalContainer.addChild(cContainer);
    }
    this.helpContainer = new PIXI.DisplayObjectContainer();
    preloadImages();
  };


  /* Load assets into cache */

  this.preloadImages = function() {
    var assets, loader;
    assets = [this.siteprefix + "assets/img/galaxy-28.jpg", this.siteprefix + "assets/img/helpBackground.png", this.siteprefix + "assets/img/Icon_v2.png", this.siteprefix + "assets/img/hex-back.png", this.siteprefix + "assets/img/spark.png", this.siteprefix + "assets/img/crystal.png", this.siteprefix + "assets/img/connector-adjacent.png", this.siteprefix + "assets/img/connector-bridge.png", this.siteprefix + "assets/img/connector-far-neighbor.png", this.siteprefix + "assets/img/connector-none.png", this.siteprefix + "assets/img/connector-opposite.png"];
    loader = new PIXI.AssetLoader(assets);
    loader.onComplete = this.initFinish;
    loader.load();
  };


  /* Detect when the window is resized - jquery ftw! */

  window.onresize = function() {
    return window.resize();
  };


  /* Finish initing after assets are loaded */

  this.initFinish = function() {
    Color.makeFilters();
    requestAnimFrame(window.animate);
    window.createHelpMenu();
    Board.loadBoard(window.BOARDNAME);
  };

}).call(this);