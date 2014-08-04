// Generated by CoffeeScript 1.7.1

/* Begins init processing */

(function() {
  var c, _i, _len, _ref,
    __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; },
    __modulo = function(a, b) { return (a % b + +b) % b; };

  this.BOARDNAME = "board29";

  this.initted = false;

  this.gameOn = true;

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
      cContainer.filters = [f];
      this.goalContainer[colr] = cContainer;
      this.goalContainer.addChild(cContainer);
    }
    this.helpContainer = new PIXI.DisplayObjectContainer();
    preloadImages();
  };


  /* Creates the help menu. Called after images are loaded */

  this.createHelpMenu = function() {
    var bottomContent, close, contentStyle, crystalIcon, crystalsContent, crystalsHeader, headerStyle, helpContainer, prismIcon, prismsContent, prismsHeader, sparkIcon, sparksContent, sparksHeader, tagText, title, topContent;
    helpContainer = window.helpContainer;
    helpContainer.position.x = 450;
    helpContainer.position.y = 200;
    helpContainer.addChild(PIXI.Sprite.fromImage("assets/img/helpBackground.png"));
    headerStyle = {
      font: "bold 15px Sans-Serif",
      fill: "#6E6E6E"
    };
    contentStyle = {
      font: "15px Sans-Serif",
      fill: "#6E6E6E"
    };
    close = new PIXI.Text("X", {
      font: "bold 20px Sans-Serif",
      fill: "gray"
    });
    close.position.x = 480;
    close.position.y = 5;
    close.interactive = true;
    close.click = function() {
      window.stage.removeChild(window.helpContainer);
      window.gameOn = true;
    };
    helpContainer.addChild(close);
    title = new PIXI.Text("Prismatic", headerStyle);
    title.position.x = 200;
    title.position.y = 10;
    helpContainer.addChild(title);
    topContent = new PIXI.Text("Prismatic is a light-based color game. Get the right \nnumber of Crystals to light up for each color.", contentStyle);
    topContent.position.x = 20;
    topContent.position.y = 40;
    helpContainer.addChild(topContent);
    sparkIcon = PIXI.Sprite.fromImage("assets/img/spark.png");
    sparkIcon.position.x = 50;
    sparkIcon.position.y = 95;
    sparkIcon.scale.x = 0.25;
    sparkIcon.scale.y = 0.25;
    helpContainer.addChild(sparkIcon);
    sparksHeader = new PIXI.Text("Sparks", headerStyle);
    sparksHeader.position.x = 100;
    sparksHeader.position.y = 100;
    helpContainer.addChild(sparksHeader);
    sparksContent = new PIXI.Text(" - the start point. They emit one color of light.", contentStyle);
    sparksContent.position.x = 150;
    sparksContent.position.y = 100;
    helpContainer.addChild(sparksContent);
    prismIcon = PIXI.Sprite.fromImage("assets/img/hex-back.png");
    prismIcon.position.x = 50;
    prismIcon.position.y = 135;
    prismIcon.scale.x = 0.25;
    prismIcon.scale.y = 0.25;
    helpContainer.addChild(prismIcon);
    prismsHeader = new PIXI.Text("Prisms", headerStyle);
    prismsHeader.position.x = 100;
    prismsHeader.position.y = 140;
    helpContainer.addChild(prismsHeader);
    prismsContent = new PIXI.Text(" - the basic piece. The channel light and rotate.", contentStyle);
    prismsContent.position.x = 150;
    prismsContent.position.y = 140;
    helpContainer.addChild(prismsContent);
    crystalIcon = PIXI.Sprite.fromImage("assets/img/hex-lit.png");
    crystalIcon.position.x = 50;
    crystalIcon.position.y = 175;
    crystalIcon.scale.x = 0.25;
    crystalIcon.scale.y = 0.25;
    helpContainer.addChild(crystalIcon);
    crystalsHeader = new PIXI.Text("Crystals", headerStyle);
    crystalsHeader.position.x = 100;
    crystalsHeader.position.y = 180;
    helpContainer.addChild(crystalsHeader);
    crystalsContent = new PIXI.Text(" - the end goal. They recieve light.", contentStyle);
    crystalsContent.position.x = 160;
    crystalsContent.position.y = 180;
    helpContainer.addChild(crystalsContent);
    bottomContent = new PIXI.Text("> Click on Sparks to change their color. \n> Click on Prisms to rotate their alignment.", contentStyle);
    bottomContent.position.x = 20;
    bottomContent.position.y = 230;
    helpContainer.addChild(bottomContent);
    tagText = new PIXI.Text("created by Michael Patashnik - Mgpshnik@gmail.com", {
      font: "italic 10px Sans-Serif",
      fill: "gray"
    });
    tagText.position.x = 120;
    tagText.position.y = 275;
    helpContainer.addChild(tagText);
  };


  /* Load assets into cache */

  this.preloadImages = function() {
    var assets, loader;
    assets = ["assets/img/galaxy-28.jpg", "assets/img/helpBackground.png", "/assets/img/hex-back.png", "assets/img/hex-lit.png", "assets/img/core.png", "assets/img/spark.png", "assets/img/crystal.png", "assets/img/menu.png", "assets/img/connector_off.png", "assets/img/connector_on.png"];
    loader = new PIXI.AssetLoader(assets);
    loader.onComplete = this.initFinish;
    loader.load();
  };


  /* Resizes the stage correctly */

  this.resize = function() {
    var bck, cContainer, col, fixY, goalContainer, helpButton, lvlPush, lvlText, margin, menuBackground, menumargin, n, newScale2, newScale3, newX, nextLvl, prevLvl, resetButton, scale, selectLabel, _ref, _ref1, _ref2;
    margin = 0;
    window.renderer.resize(window.innerWidth - margin, window.innerHeight - margin);
    bck = this.menu.children[0];
    menuBackground = this.menu.children[1];
    lvlText = this.menu.children[2];
    resetButton = this.menu.children[3];
    selectLabel = this.menu.children[4];
    prevLvl = this.menu.children[5];
    nextLvl = this.menu.children[6];
    helpButton = this.menu.children[7];
    goalContainer = this.menu.children[8];
    bck.scale.x = Math.max(window.innerWidth / bck.texture.baseTexture.width, 0.75);
    bck.scale.y = Math.max(window.innerHeight / bck.texture.baseTexture.height, 0.75);
    menuBackground.scale.x = window.innerWidth / 200;
    newScale2 = Math.min(1, Math.max(0.5, window.innerHeight / 1500));
    menuBackground.scale.y = newScale2;
    this.base.position.y = newScale2 * 100;
    _ref = this.colorContainers;
    for (col in _ref) {
      cContainer = _ref[col];
      cContainer.position.y = newScale2 * 100;
    }
    newScale3 = newScale2 * 0.5;
    lvlText.scale.x = lvlText.scale.y = newScale3;
    resetButton.scale.x = resetButton.scale.y = newScale3;
    selectLabel.scale.x = selectLabel.scale.y = newScale3;
    prevLvl.scale.x = prevLvl.scale.y = newScale3;
    nextLvl.scale.x = nextLvl.scale.y = newScale3;
    helpButton.scale.x = helpButton.scale.y = newScale3;
    menumargin = 20;
    lvlPush = this.level >= 10 ? 35 : 0;
    lvlText.position.x = menumargin;
    helpButton.position.x = window.innerWidth - (250 * newScale3);
    nextLvl.position.x = helpButton.position.x - (275. * newScale3);
    prevLvl.position.x = nextLvl.position.x - (300. * newScale3);
    selectLabel.position.x = prevLvl.position.x - (300 * newScale3);
    resetButton.position.x = selectLabel.position.x - 300 * newScale3;
    fixY = function(comp, scale) {
      comp.position.y = 35 * scale;
    };
    fixY(lvlText, newScale3);
    fixY(resetButton, newScale3);
    fixY(helpButton, newScale3);
    fixY(nextLvl, newScale3);
    fixY(prevLvl, newScale3);
    fixY(selectLabel, newScale3);
    if (this.BOARD != null) {
      if (goalContainer != null) {
        goalContainer.position.y = -10 * newScale3;
        goalContainer.scale.x = newScale3;
        goalContainer.scale.y = newScale3;
        goalContainer.position.x = lvlText.position.x + (575 + lvlPush) * newScale3;
      }
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
      newX = (window.innerWidth - window.BOARD.getWidth() * n) / 2 + 20;
      this.base.position.x = newX;
      _ref2 = this.colorContainers;
      for (col in _ref2) {
        cContainer = _ref2[col];
        cContainer.position.x = newX;
      }
    }
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
    var col, cont, m, pulse, val, _ref1;
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
  };


  /* Finish initing after assets are loaded */

  this.initFinish = function() {
    var animate;
    Color.makeFilters();
    window.count = 0;
    animate = function() {
      var col, connector, core, curLit, goalContainer, h, hLit, inc, n, nConnector, nS, pan, panel, radTo60Degree, rotSpeed, spr, tolerance, value, _j, _k, _l, _len1, _len2, _len3, _len4, _len5, _len6, _len7, _len8, _len9, _m, _n, _o, _p, _q, _r, _ref1, _ref10, _ref11, _ref12, _ref13, _ref14, _ref15, _ref2, _ref3, _ref4, _ref5, _ref6, _ref7, _ref8, _ref9;
      window.count += 1;
      this.calcPulseFilter(window.count);
      rotSpeed = 1 / 5;
      tolerance = 0.000001;
      radTo60Degree = 1.04719755;
      if ((this.BOARD != null)) {
        curLit = this.BOARD.crystalLitCount();
        goalContainer = this.menu.children[8];
        _ref1 = goalContainer.children;
        for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
          pan = _ref1[_j];
          _ref2 = pan.children;
          for (_k = 0, _len2 = _ref2.length; _k < _len2; _k++) {
            spr = _ref2[_k];
            if (spr instanceof PIXI.Text && spr.color.toUpperCase() in curLit) {
              spr.setText(curLit[spr.color.toUpperCase()] + spr.text.substring(1));
            }
          }
        }
        _ref3 = this.BOARD.allHexes();
        for (_l = 0, _len3 = _ref3.length; _l < _len3; _l++) {
          h = _ref3[_l];
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
            _ref4 = h.cores;
            for (col in _ref4) {
              core = _ref4[col];
              if ((_ref5 = col.toLowerCase(), __indexOf.call(hLit, _ref5) < 0) && core.alpha > 0) {
                core.alpha = 0;
              } else if ((_ref6 = col.toLowerCase(), __indexOf.call(hLit, _ref6) >= 0) && core.alpha === 0) {
                core.alpha = 0.75;
              }
            }
          }
          nS = h.getNeighborsWithBlanks();
          _ref7 = h.colorPanels;
          for (col in _ref7) {
            panel = _ref7[col];
            _ref8 = panel.children;
            for (_m = 0, _len4 = _ref8.length; _m < _len4; _m++) {
              connector = _ref8[_m];
              c = h.colorOfSide(connector.side);
              n = nS[connector.side];
              if ((n != null) && __indexOf.call(hLit, c) >= 0 && n.colorOfSide(n.indexLinked(h)) === c && !connector.linked) {
                connector.texture = PIXI.Texture.fromImage("assets/img/connector_on.png");
                this.toLit(connector);
                _ref9 = n.colorPanels;
                for (_n = 0, _len5 = _ref9.length; _n < _len5; _n++) {
                  nConnector = _ref9[_n];
                  if (nConnector.side === n.indexLinked(h) && !nConnector.linked) {
                    nConnector.texture = PIXI.Texture.fromImage("assets/img/connector_on.png");
                    this.toLit(nConnector);
                  }
                }
              } else if (connector.linked && (__indexOf.call(hLit, c) < 0 || (n != null) && n.colorOfSide(n.indexLinked(h)) !== c)) {
                connector.texture = PIXI.Texture.fromImage("assets/img/connector_off.png");
                this.toUnlit(connector);
                if (n != null) {
                  _ref10 = n.colorPanels;
                  for (_o = 0, _len6 = _ref10.length; _o < _len6; _o++) {
                    nConnector = _ref10[_o];
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
            _ref11 = h.colorPanels;
            for (_p = 0, _len7 = _ref11.length; _p < _len7; _p++) {
              value = _ref11[_p];
              value.rotation += inc * radTo60Degree;
            }
            _ref12 = h.cores;
            for (col in _ref12) {
              core = _ref12[col];
              core.currentRotation += inc;
            }
            if (Math.abs(h.targetRotation - h.currentRotation) < tolerance) {
              inc = h.targetRotation - h.currentRotation;
              h.backPanel.rotation += inc * radTo60Degree;
              h.currentRotation += inc;
              _ref13 = h.cores;
              for (col in _ref13) {
                core = _ref13[col];
                core.currentRotation += inc;
              }
              _ref14 = h.colorPanels;
              for (_q = 0, _len8 = _ref14.length; _q < _len8; _q++) {
                value = _ref14[_q];
                value.rotation += inc * radTo60Degree;
                _ref15 = value.children;
                for (_r = 0, _len9 = _ref15.length; _r < _len9; _r++) {
                  spr = _ref15[_r];
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
    window.createHelpMenu();
    this.BOARD = new Board();
    Board.loadBoard(window.BOARDNAME);
  };

  this.menuStyle = {
    font: "bold 35px Sans-Serif",
    fill: "white"
  };

  this.initMenu = function() {
    var bck, helpButton, lvlText, menuBar, nextLvl, prevLvl, resetButton, selectLabel;
    bck = PIXI.Sprite.fromImage("assets/img/galaxy-28.jpg");
    this.menu.addChild(bck);
    menuBar = PIXI.Sprite.fromImage("assets/img/menu.png");
    menuBar.alpha = 0.5;
    this.menu.addChild(menuBar);
    lvlText = new PIXI.Text("Lvl. " + this.level + " of 50", this.menuStyle);
    this.menu.addChild(lvlText);
    resetButton = new PIXI.Text("Reset", this.menuStyle);
    resetButton.interactive = true;
    resetButton.click = function() {
      window.clearBoard();
      Board.loadBoard(window.BOARDNAME);
      window.updateMenu();
    };
    this.menu.addChild(resetButton);
    selectLabel = new PIXI.Text("Level: ", this.menuStyle);
    this.menu.addChild(selectLabel);
    if (this.level > 1) {
      prevLvl = new PIXI.Text("<< " + (this.level - 1), this.menuStyle);
      prevLvl.interactive = true;
    } else {
      prevLvl = new PIXI.Text("     ", this.menuStyle);
      prevLvl.interactive = false;
    }
    prevLvl.click = function() {
      var num;
      num = window.level - 1 < 10 ? "0" + (window.level - 1) : "" + (window.level - 1);
      window.BOARDNAME = "board" + num;
      resetButton.click();
    };
    this.menu.addChild(prevLvl);
    if (this.level < 50) {
      nextLvl = new PIXI.Text((this.level + 1) + " >>", this.menuStyle);
      nextLvl.interactive = true;
    } else {
      nextLvl = new PIXI.Text("     ", this.menuStyle);
      nextLvl.interactive = false;
    }
    nextLvl.click = function() {
      var num;
      num = window.level + 1 < 10 ? "0" + (window.level + 1) : "" + (window.level + 1);
      window.BOARDNAME = "board" + num;
      resetButton.click();
    };
    this.menu.addChild(nextLvl);
    helpButton = new PIXI.Text("Help", this.menuStyle);
    helpButton.interactive = true;
    helpButton.click = function() {
      window.gameOn = false;
      window.stage.addChild(window.helpContainer);
    };
    this.menu.addChild(helpButton);
    this.menu.addChild(this.goalContainer);
  };

  this.updateMenu = function() {
    var lvlText, nextLvl, prevLvl;
    lvlText = this.menu.children[2];
    lvlText.setText("Lvl. " + this.level + " of 50");
    prevLvl = this.menu.children[5];
    if (this.level > 1) {
      prevLvl.setText("<< " + (this.level - 1));
      prevLvl.interactive = true;
    } else {
      prevLvl.setText("     ");
      prevLvl.interactive = false;
    }
    nextLvl = this.menu.children[6];
    if (this.level < 50) {
      nextLvl.setText((this.level + 1) + " >>");
      nextLvl.interactive = true;
    } else {
      nextLvl.setText("     ");
      nextLvl.interactive = false;
    }
  };


  /* Clears board and associated sprites from screen, usually in anticipation of new board being loaded */

  this.clearBoard = function() {
    var i, pan, spr, sprToRemove, _j, _k, _l, _len1, _len2, _len3, _len4, _len5, _len6, _m, _n, _o, _p, _ref1, _ref2, _ref3, _ref4, _ref5, _ref6;
    sprToRemove = [];
    _ref1 = this.menu.children[8].children;
    for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
      pan = _ref1[_j];
      _ref2 = pan.children;
      for (_k = 0, _len2 = _ref2.length; _k < _len2; _k++) {
        spr = _ref2[_k];
        sprToRemove.push(spr);
      }
    }
    _ref3 = this.stage.children[1].children;
    for (_l = 0, _len3 = _ref3.length; _l < _len3; _l++) {
      spr = _ref3[_l];
      sprToRemove.push(spr);
    }
    for (i = _m = 1, _ref4 = this.stage.children.length - 1; 1 <= _ref4 ? _m <= _ref4 : _m >= _ref4; i = 1 <= _ref4 ? ++_m : --_m) {
      _ref5 = this.stage.children[i].children;
      for (_n = 0, _len4 = _ref5.length; _n < _len4; _n++) {
        pan = _ref5[_n];
        _ref6 = pan.children;
        for (_o = 0, _len5 = _ref6.length; _o < _len5; _o++) {
          spr = _ref6[_o];
          sprToRemove.push(spr);
        }
      }
    }
    for (_p = 0, _len6 = sprToRemove.length; _p < _len6; _p++) {
      spr = sprToRemove[_p];
      if (spr != null) {
        spr.parent.removeChild(spr);
      }
    }
    this.BOARD = null;
  };


  /* Called when the board is loaded */

  this.onBoardLoad = function() {
    var color, colors, goalBoard, goalCount, goalStyle, i, spr, text, _j, _k, _len1, _len2, _ref1;
    if (!this.initted) {
      window.initMenu();
      this.initted = true;
    }
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
      spr.position.x = c.loc.row * this.hexRad * 2.75;
      spr.anchor.x = 0.5;
      spr.anchor.y = 0.5;
      this.goalContainer[c.lit.toUpperCase()].addChild(spr);
      goalCount = window.BOARD[c.lit.toUpperCase()];
      this.goalContainer[c.lit.toUpperCase()].goalCount = goalCount;
      goalStyle = this.menuStyle;
      goalStyle.font = "100px bold Times New Roman";
      text = new PIXI.Text("0/" + goalCount, goalStyle);
      text.position.x = c.loc.row * this.hexRad * 2.75 + this.hexRad * 0.75;
      text.position.y = -60;
      text.color = c.lit;
      this.goalContainer[c.lit.toUpperCase()].addChild(text);
    }
    this.goalContainer.count = 4;
    window.BOARD.relight();
    document.body.appendChild(renderer.view);
    window.resize();
    window.drawBoard();
    return this.resize();
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
        if (window.gameOn) {
          hex.click();
        }
      };
    }
    return hex.panel;
  };

}).call(this);
