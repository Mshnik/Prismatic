// Generated by CoffeeScript 1.7.1

/* Begins init processing */

(function() {
  var c, _i, _len, _ref,
    __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; },
    __modulo = function(a, b) { return (a % b + +b) % b; };

  this.init = function() {
    return this.initStart();
  };

  this.typeIsArray = Array.isArray || function(value) {
    return {}.toString.call(value) === '[object Array]';
  };


  /* Set up a PIXI stage - part before asset loading */

  this.initStart = function() {
    var c, cContainer, colr, f, lit, margin, menuHeight, offset, pulse, unlit, _i, _j, _len, _len1, _ref, _ref1;
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
    this.colorContainers = {};
    _ref = Color.values();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      c = _ref[_i];
      colr = c;
      if (!isNaN(colr)) {
        colr = Color.asString(colr);
      }
      cContainer = new PIXI.DisplayObjectContainer();
      cContainer.position.y = menuHeight;
      f = new PIXI.ColorMatrixFilter();
      f.matrix = Color.matrixFor(colr);
      cContainer.filters = [f];
      unlit = new PIXI.DisplayObjectContainer();
      cContainer.addChild(unlit);
      lit = new PIXI.DisplayObjectContainer();
      cContainer.addChild(lit);
      cContainer.unlit = unlit;
      cContainer.unlit.filters = [this.flat];
      cContainer.lit = lit;
      pulse = new PIXI.ColorMatrixFilter();
      pulse.matrix = [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1];
      cContainer.lit.pulseLength = 173;
      cContainer.lit.pulseOffset = offset;
      offset += 70;
      cContainer.lit.filters = [pulse];
      this.stage.addChild(cContainer);
      this.colorContainers[colr] = cContainer;
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
      cContainer.pulseLength = 173;
      cContainer.pulseOffset = offset;
      offset += 70;
      cContainer.filters = [f, pulse];
      this.goalContainer[colr] = cContainer;
      this.goalContainer.addChild(cContainer);
    }
    preloadImages();
  };


  /* Load assets into cache */

  this.preloadImages = function() {
    var assets, loader;
    assets = ["assets/img/galaxy-28.jpg", "/assets/img/hex-back.png", "assets/img/hex-lit.png", "assets/img/core.png", "assets/img/spark.png", "assets/img/crystal.png", "assets/img/menu.png", "assets/img/connector_off.png", "assets/img/connector_on.png"];
    loader = new PIXI.AssetLoader(assets);
    loader.onComplete = this.initFinish;
    loader.load();
  };


  /* Resizes the stage correctly */

  this.resize = function() {
    var bck, cContainer, col, goalContainer, margin, menuBackground, menuLeft, menuMiddle, menuRight, n, newScale, newScale2, newScale3, newX, scale, _ref, _ref1, _ref2;
    margin = 0;
    window.renderer.resize(window.innerWidth - margin, window.innerHeight - margin);
    bck = this.menu.children[0];
    menuBackground = this.menu.children[1];
    menuLeft = menuBackground.children[0];
    menuMiddle = menuBackground.children[1];
    menuRight = menuBackground.children[2];
    goalContainer = this.menu.children[2];
    newScale = (window.innerWidth - 220) / 200;
    menuMiddle.scale.x = newScale;
    menuRight.position.x = 100 + (newScale * 200);
    newScale2 = Math.min(1, Math.max(0.5, window.innerHeight / 1000));
    menuBackground.scale.y = newScale2;
    this.base.position.y = newScale2 * 100;
    _ref = this.colorContainers;
    for (col in _ref) {
      cContainer = _ref[col];
      cContainer.position.y = newScale2 * 100;
    }
    bck.scale.x = Math.max(window.innerWidth / bck.texture.baseTexture.width, 0.75);
    bck.scale.y = Math.max(window.innerHeight / bck.texture.baseTexture.height, 0.75);
    if (this.BOARD != null) {
      scale = (1 / 130) * Math.min(window.innerHeight / window.BOARD.getHeight() / 1.1, window.innerWidth * 1.15 / window.BOARD.getWidth());
      this.base.scale.x = scale;
      this.base.scale.y = scale;
      _ref1 = this.colorContainers;
      for (col in _ref1) {
        cContainer = _ref1[col];
        cContainer.scale.x = scale;
        cContainer.scale.y = scale;
      }
      n = this.hexRad * this.base.scale.x;
      newX = (window.innerWidth - window.BOARD.getWidth() * n) / 2;
      this.base.position.x = newX;
      _ref2 = this.colorContainers;
      for (col in _ref2) {
        cContainer = _ref2[col];
        cContainer.position.x = newX;
      }
    }
    newScale3 = newScale2 * 0.5;
    goalContainer.position.y = 0;
    goalContainer.scale.x = newScale3;
    goalContainer.scale.y = newScale3;
    goalContainer.position.x = window.innerWidth - newScale3 * (goalContainer.getLocalBounds().width + 20);
  };


  /* Detect when the window is resized - jquery ftw! */

  window.onresize = function() {
    return window.resize();
  };

  this.toLit = function(connector) {
    var c;
    try {
      this.colorContainers[connector.color].unlit.removeChild(connector.panel);
    } catch (_error) {

    }
    if (this.typeIsArray(connector.color)) {
      if (connector.color.length > 0) {
        c = connector.color[0].toUpperCase();
      } else {
        c = Color.asString(Color.NONE).toUpperCase();
      }
    } else {
      c = connector.color.toUpperCase();
    }
    if (c === Color.asString(Color.NONE).toUpperCase()) {
      this.toUnlit(connector);
    } else {
      this.colorContainers[c.toUpperCase()].lit.addChild(connector.panel);
      connector.linked = true;
    }
  };

  this.toUnlit = function(connector) {
    var c;
    try {
      this.colorContainers[connector.color].lit.removeChild(connector.panel);
    } catch (_error) {

    }
    if (this.typeIsArray(connector.color)) {
      if (connector.color.length > 0) {
        c = connector.color[0];
      } else {
        c = Color.asString(Color.NONE);
      }
    } else {
      c = connector.color;
    }
    if ((connector.hex != null) && connector.hex instanceof Crystal) {
      this.colorContainers[Color.asString(Color.NONE).toUpperCase()].unlit.addChild(connector.panel);
    } else {
      this.colorContainers[c.toUpperCase()].unlit.addChild(connector.panel);
    }
    connector.linked = false;
  };

  this.colorOffset = {};

  _ref = Color.values();
  for (_i = 0, _len = _ref.length; _i < _len; _i++) {
    c = _ref[_i];
    if (!isNaN(c)) {
      c = Color.fromString(c).toUpperCase();
    } else {
      c = c.toUpperCase();
    }
    this.colorOffset[c] = Math.random() + 0.5;
  }


  /* Updates the pulse filter that controls lighting effects */

  this.calcPulseFilter = function(count) {
    var col, cont, m, pulse, val, _ref1, _ref2;
    _ref1 = this.colorContainers;
    for (col in _ref1) {
      val = _ref1[col];
      pulse = val.lit.filters[0];
      cont = (count + val.lit.pulseOffset) / val.lit.pulseLength;
      m = pulse.matrix;
      m[0] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5;
      m[5] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5;
      m[10] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5;
      m[15] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.25 + 0.75;
      pulse.matrix = m;
    }
    _ref2 = this.goalContainer;
    for (col in _ref2) {
      val = _ref2[col];
      if (Color.isRegularColor(col)) {
        pulse = val.filters[1];
        cont = (count + val.pulseOffset) / val.pulseLength;
        m = pulse.matrix;
        m[0] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5;
        m[5] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5;
        m[10] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.5 + 0.5;
        m[15] = Math.abs(Math.sin(cont * 2 * Math.PI)) * 0.25 + 0.75;
        pulse.matrix = m;
      }
    }
  };


  /* Finish initing after assets are loaded */

  this.initFinish = function() {
    var animate;
    window.initMenu();
    Color.makeFilters();
    window.count = 0;
    animate = function() {
      var col, connector, core, h, hLit, inc, n, nConnector, nS, panel, radTo60Degree, rotSpeed, spr, tolerance, value, _j, _k, _l, _len1, _len2, _len3, _len4, _len5, _len6, _len7, _m, _n, _o, _p, _ref1, _ref10, _ref11, _ref12, _ref13, _ref2, _ref3, _ref4, _ref5, _ref6, _ref7, _ref8, _ref9;
      window.count += 1;
      this.calcPulseFilter(window.count);
      rotSpeed = 1 / 5;
      tolerance = 0.000001;
      radTo60Degree = 1.04719755;
      if ((this.BOARD != null)) {
        _ref1 = this.BOARD.allHexes();
        for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
          h = _ref1[_j];
          if (h.isLit().length > 0 && !h.backPanel.children[0].lit) {
            h.backPanel.children[0].lit = true;
            if (!(h instanceof Prism)) {
              this.toLit(h.backPanel.spr);
            }
          }
          if (h.isLit().length === 0 && h.backPanel.children[0].lit) {
            h.backPanel.children[0].lit = false;
            if (!(h instanceof Prism)) {
              this.toUnlit(h.backPanel.spr);
            }
          }
          hLit = h.isLit();
          if (h instanceof Prism) {
            _ref2 = h.cores;
            for (col in _ref2) {
              core = _ref2[col];
              if ((_ref3 = col.toLowerCase(), __indexOf.call(hLit, _ref3) < 0) && core.alpha > 0) {
                core.alpha = 0;
              } else if ((_ref4 = col.toLowerCase(), __indexOf.call(hLit, _ref4) >= 0) && core.alpha === 0) {
                core.alpha = 0.75;
              }
            }
          }
          nS = h.getNeighborsWithBlanks();
          _ref5 = h.colorPanels;
          for (col in _ref5) {
            panel = _ref5[col];
            _ref6 = panel.children;
            for (_k = 0, _len2 = _ref6.length; _k < _len2; _k++) {
              connector = _ref6[_k];
              c = h.colorOfSide(connector.side);
              n = nS[connector.side];
              if ((n != null) && __indexOf.call(hLit, c) >= 0 && n.colorOfSide(n.indexLinked(h)) === c && !connector.linked) {
                connector.texture = PIXI.Texture.fromImage("assets/img/connector_on.png");
                this.toLit(connector);
                _ref7 = n.colorPanels;
                for (_l = 0, _len3 = _ref7.length; _l < _len3; _l++) {
                  nConnector = _ref7[_l];
                  if (nConnector.side === n.indexLinked(h) && !nConnector.linked) {
                    nConnector.texture = PIXI.Texture.fromImage("assets/img/connector_on.png");
                    this.toLit(nConnector);
                  }
                }
              } else if (connector.linked && (__indexOf.call(hLit, c) < 0 || (n != null) && n.colorOfSide(n.indexLinked(h)) !== c)) {
                connector.texture = PIXI.Texture.fromImage("assets/img/connector_off.png");
                this.toUnlit(connector);
                if (n != null) {
                  _ref8 = n.colorPanels;
                  for (_m = 0, _len4 = _ref8.length; _m < _len4; _m++) {
                    nConnector = _ref8[_m];
                    if (nConnector.side === n.indexLinked(h) && nConnector.linked) {
                      nConnector.texture = PIXI.Texture.fromImage("assets/img/connector_off.png");
                      this.toUnlit(nConnector);
                    }
                  }
                }
              }
            }
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
            inc = (h.targetRotation - h.prevRotation) >= 0 ? rotSpeed : -rotSpeed;
            h.backPanel.rotation += inc * radTo60Degree;
            h.currentRotation += inc;
            _ref9 = h.colorPanels;
            for (_n = 0, _len5 = _ref9.length; _n < _len5; _n++) {
              value = _ref9[_n];
              value.rotation += inc * radTo60Degree;
            }
            _ref10 = h.cores;
            for (col in _ref10) {
              core = _ref10[col];
              core.currentRotation += inc;
            }
            if (Math.abs(h.targetRotation - h.currentRotation) < tolerance) {
              inc = h.targetRotation - h.currentRotation;
              h.backPanel.rotation += inc * radTo60Degree;
              h.currentRotation += inc;
              _ref11 = h.cores;
              for (col in _ref11) {
                core = _ref11[col];
                core.currentRotation += inc;
              }
              _ref12 = h.colorPanels;
              for (_o = 0, _len6 = _ref12.length; _o < _len6; _o++) {
                value = _ref12[_o];
                value.rotation += inc * radTo60Degree;
                _ref13 = value.children;
                for (_p = 0, _len7 = _ref13.length; _p < _len7; _p++) {
                  spr = _ref13[_p];
                  spr.side = __modulo(spr.side + (h.currentRotation - h.prevRotation), Hex.SIDES);
                }
              }
              h.prevRotation = h.currentRotation;
              h.canLight = true;
              h.light();
            }
          }

          /* Spark and crystal color changing */
          if ((h instanceof Spark || h instanceof Crystal) && h.toColor !== "") {
            col = !isNaN(h.toColor) ? Color.asString(h.toColor).toUpperCase() : h.toColor.toUpperCase();
            h.backPanel.spr.color = col;
            this.toLit(h.backPanel.spr);
            h.toColor = "";
          }
        }
      }
      requestAnimFrame(animate);
      this.renderer.render(this.stage);
    };
    requestAnimFrame(animate);
    this.BOARD = new Board();
    Board.loadBoard("board2");
  };

  this.initMenu = function() {
    var baseTex, bck, menuBack_Left, menuBack_Middle, menuBack_Right, menuBackground;
    bck = PIXI.Sprite.fromImage("assets/img/galaxy-28.jpg");
    this.menu.addChild(bck);
    menuBackground = new PIXI.DisplayObjectContainer();
    menuBackground.alpha = 0.5;
    baseTex = PIXI.BaseTexture.fromImage("assets/img/menu.png");
    menuBack_Left = new PIXI.Sprite(new PIXI.Texture(baseTex, new PIXI.Rectangle(0, 0, 100, 100)));
    menuBack_Middle = new PIXI.Sprite(new PIXI.Texture(baseTex, new PIXI.Rectangle(100, 0, 200, 100)));
    menuBack_Middle.position.x = 100;
    menuBack_Right = new PIXI.Sprite(new PIXI.Texture(baseTex, new PIXI.Rectangle(300, 0, 100, 100)));
    menuBack_Right.position.x = 300;
    menuBackground.addChild(menuBack_Left);
    menuBackground.addChild(menuBack_Middle);
    menuBackground.addChild(menuBack_Right);
    this.menu.addChild(menuBackground);
    this.menu.addChild(this.goalContainer);
    this.resize();
  };


  /* Called when the board is loaded */

  this.onBoardLoad = function() {
    var color, colors, goalBoard, goalCount, i, spr, text, _j, _k, _len1, _len2, _ref1;
    colors = window.BOARD.colorsPresent();
    goalBoard = new Board(colors.length, 1);
    i = 0;
    for (_j = 0, _len1 = colors.length; _j < _len1; _j++) {
      color = colors[_j];
      c = new Crystal(goalBoard, new Loc(i, 0));
      c.lit = color;
      i++;
    }
    _ref1 = goalBoard.allHexesOfClass("Crystal");
    for (_k = 0, _len2 = _ref1.length; _k < _len2; _k++) {
      c = _ref1[_k];
      spr = PIXI.Sprite.fromImage("assets/img/crystal.png");
      spr.lit = false;
      spr.color = c.lit;
      spr.hex = c;
      spr.position.x = c.loc.row * this.hexRad * 2.2;
      spr.anchor.x = 0.5;
      spr.anchor.y = 0.5;
      this.goalContainer[c.lit.toUpperCase()].addChild(spr);
      goalCount = window.BOARD[c.lit.toUpperCase()];
      this.goalContainer[c.lit.toUpperCase()].goalCount = goalCount;
      text = new PIXI.Text("x" + goalCount, {
        font: "100px bold Times New Roman"
      });
      text.position.x = c.loc.row * this.hexRad * 2.2 + this.hexRad * 0.6;
      text.position.y = -60;
      this.goalContainer[c.lit.toUpperCase()].addChild(text);
    }
    this.goalContainer.count = 4;
    window.BOARD.relight();
    document.body.appendChild(renderer.view);
    window.resize();
    return window.drawBoard();
  };


  /* Creates a dummy board and adds to scope. Mainly for testing */

  this.createDummyBoard = function() {
    this.BOARD = this.Board.makeBoard(4, 12, 3);
    this.onBoardLoad();
  };


  /* Draws the Board in BOARD on the stage. */

  this.drawBoard = function() {
    var h, _j, _len1, _ref1;
    _ref1 = this.BOARD.allHexes();
    for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
      h = _ref1[_j];
      this.createSpriteForHex(h);
    }
  };

  this.hexRad = 110;


  /* Creates a single sprite for a hex and adds it to stage */

  this.createSpriteForHex = function(hex) {
    var backpanel, col, core, coreContainer, cpanel, cr, i, nudge, point, radTo60Degree, shrink, sidePanels, spr, _j, _k, _len1, _ref1, _ref2;
    if (typeof hex.panel === "undefined" || hex.panel === null) {
      radTo60Degree = 1.04719755;
      backpanel = new PIXI.DisplayObjectContainer();
      backpanel.position.x = hex.loc.col * this.hexRad * 3 / 4 * 1.11 + this.hexRad * (5 / 8);
      backpanel.position.y = hex.loc.row * this.hexRad + this.hexRad * (5 / 8);
      if (hex.loc.col % 2 === 1) {
        backpanel.position.y += this.hexRad / 2;
      }
      backpanel.pivot.x = 0.5;
      backpanel.pivot.y = 0.5;
      if (hex instanceof Prism) {
        spr = PIXI.Sprite.fromImage("assets/img/hex-back.png");
      } else if (hex instanceof Crystal) {
        spr = PIXI.Sprite.fromImage("assets/img/crystal.png");
      } else if (hex instanceof Spark) {
        spr = PIXI.Sprite.fromImage("assets/img/spark.png");
      }
      spr.lit = false;
      spr.anchor.x = 0.5;
      spr.anchor.y = 0.5;
      spr.hex = hex;
      backpanel.addChild(spr);
      backpanel.spr = spr;
      hex.spr = spr;
      sidePanels = [];
      if (hex instanceof Prism) {
        for (i = _j = 0, _ref1 = Hex.SIDES - 1; _j <= _ref1; i = _j += 1) {
          c = hex.colorOfSide(i);
          if (!isNaN(c)) {
            c = Color.asString(c).toUpperCase();
          } else {
            c = c.toUpperCase();
          }
          nudge = 0.528;
          shrink = 25;
          point = new PIXI.Point((this.hexRad / 2 - shrink) * Math.cos((i - 2) * 2 * Math.PI / Hex.SIDES + nudge), (this.hexRad / 2 - shrink) * Math.sin((i - 2) * 2 * Math.PI / Hex.SIDES + nudge));
          cr = PIXI.Sprite.fromImage("assets/img/connector_off.png");
          cr.linked = false;
          cr.anchor.x = 0.5;
          cr.anchor.y = 0.8;
          cr.rotation = i * radTo60Degree;
          cr.position.x = point.x;
          cr.position.y = point.y;
          cr.side = i;
          cr.color = c;
          cpanel = new PIXI.DisplayObjectContainer();
          cpanel.position.x = hex.loc.col * this.hexRad * 3 / 4 * 1.11 + this.hexRad * (5 / 8);
          cpanel.position.y = hex.loc.row * this.hexRad + this.hexRad * (5 / 8);
          if (hex.loc.col % 2 === 1) {
            cpanel.position.y += this.hexRad / 2;
          }
          cpanel.pivot.x = 0.5;
          cpanel.pivot.y = 0.5;
          cpanel.addChild(cr);
          cr.panel = cpanel;
          sidePanels.push(cpanel);
          this.colorContainers[c].unlit.addChild(cpanel);
          coreContainer = {};
          _ref2 = Color.regularColors();
          for (_k = 0, _len1 = _ref2.length; _k < _len1; _k++) {
            col = _ref2[_k];
            col = !isNaN(col) ? Color.asString(col).toUpperCase() : col.toUpperCase();
            core = PIXI.Sprite.fromImage("assets/img/core.png");
            core.position.x = hex.loc.col * this.hexRad * 3 / 4 * 1.11 + this.hexRad * (7 / 16);
            core.position.y = hex.loc.row * this.hexRad + this.hexRad * (7 / 16) - 0.5;
            if (hex.loc.col % 2 === 1) {
              core.position.y += this.hexRad / 2;
            }
            core.pivot.x = 0.5;
            core.pivot.y = 0.5;
            core.alpha = 0;
            coreContainer[col] = core;
            this.colorContainers[col].lit.addChild(core);
          }
        }
      } else if (hex instanceof Crystal) {
        spr.color = hex.lit === Color.NONE ? "NONE" : hex.lit.toUpperCase();
        spr.panel = backpanel;
        this.toUnlit(spr);
      } else if (hex instanceof Spark) {
        spr.color = hex.getColor().toUpperCase();
        spr.panel = backpanel;
        this.toLit(spr);
      }
      hex.backPanel = backpanel;
      hex.colorPanels = sidePanels;
      hex.cores = coreContainer;
      if (hex instanceof Prism) {
        this.base.addChild(backpanel);
      }
      backpanel.interactive = true;
      backpanel.click = function() {
        hex.click();
      };
    }
    return hex.panel;
  };

}).call(this);
