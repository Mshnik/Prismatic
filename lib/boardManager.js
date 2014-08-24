// Generated by CoffeeScript 1.7.1

/* Front end maintenence on board objects - loading, creating view for, etc */


/* Clears board and associated sprites from screen, usually in anticipation of new board being loaded */

(function() {
  var __modulo = function(a, b) { return (a % b + +b) % b; };

  this.clearBoard = function() {
    var i, j, pan, spr, sprToRemove, _i, _j, _k, _l, _len, _len1, _len2, _len3, _len4, _m, _n, _o, _ref, _ref1, _ref2, _ref3, _ref4, _ref5;
    sprToRemove = [];
    _ref = this.menu.children[this.goalContainerIndex].children;
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      pan = _ref[_i];
      _ref1 = pan.children;
      for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
        spr = _ref1[_j];
        sprToRemove.push(spr);
      }
    }
    _ref2 = this.stage.children[1].children;
    for (_k = 0, _len2 = _ref2.length; _k < _len2; _k++) {
      spr = _ref2[_k];
      sprToRemove.push(spr);
    }
    for (i = _l = 1, _ref3 = this.stage.children.length - 1; _l <= _ref3; i = _l += 1) {
      _ref4 = this.stage.children[i].children;
      for (_m = 0, _len3 = _ref4.length; _m < _len3; _m++) {
        pan = _ref4[_m];
        for (j = _n = 1, _ref5 = pan.children.length; _n <= _ref5; j = _n += 1) {
          spr = pan.children[j];
          sprToRemove.push(spr);
        }
      }
    }
    for (_o = 0, _len4 = sprToRemove.length; _o < _len4; _o++) {
      spr = sprToRemove[_o];
      if (spr != null) {
        spr.parent.removeChild(spr);
      }
    }
    this.BOARD = null;
  };


  /* Called when the board is loaded */

  this.onBoardLoad = function() {
    var c, color, colors, fix, goalArr, goalBoard, goalCount, goalStyle, h, i, key, pushCoef, r, removeCount, s, spaceCoef, spark, spr, text, use, val, _i, _j, _k, _l, _len, _len1, _len2, _len3, _len4, _m, _n, _o, _ref, _ref1, _ref2, _ref3, _ref4;
    if (!this.initted) {
      window.initMenu();
      this.menu.children[7].click();
      this.initted = true;
    }
    colors = window.BOARD.colorsPresent();
    goalArr = [];
    for (_i = 0, _len = colors.length; _i < _len; _i++) {
      c = colors[_i];
      for (i = _j = 1, _ref = window.BOARD[c.toUpperCase()]; _j <= _ref; i = _j += 1) {
        goalArr.push(c.toUpperCase());
      }
    }
    fix = function(h, cir) {
      i = 0;
      while (!h.colorCircle.matches(ColorCircle.fromArray(val))) {
        h.rotate();
        i++;
        if (i === 6) {
          console.error("Rotated six times, no match!");
          break;
        }
        h.targetRotation = 0;
      }
    };
    if (this.difficulty === this.Game.EASY) {
      removeCount = goalArr.length / 2;
      _ref1 = this.BOARD.Locked;
      for (key in _ref1) {
        val = _ref1[key];
        h = this.BOARD.getHex(Loc.fromString(key));
        fix(h, ColorCircle.fromArray(val));
        h.isLocked = true;
      }
    }
    if (this.difficulty === this.Game.MEDIUM) {
      removeCount = goalArr.length / 4;
      use = true;
      _ref2 = this.BOARD.Locked;
      for (key in _ref2) {
        val = _ref2[key];
        if (use) {
          h = this.BOARD.getHex(Loc.fromString(key));
          fix(h, ColorCircle.fromArray(val));
          h.isLocked = true;
        }
        use = !use;
      }
    }
    if (this.difficulty === this.Game.HARD) {
      removeCount = 0;
    }
    this.BOARD.moves = 0;
    for (i = _k = 1; _k <= removeCount; i = _k += 1) {
      r = Math.floor(Math.random() * goalArr.length);
      window.BOARD[goalArr[i]]--;
      goalArr.splice(i, 1);
    }
    goalBoard = new Board(colors.length, 1);
    i = 0;
    for (_l = 0, _len1 = colors.length; _l < _len1; _l++) {
      color = colors[_l];
      goalCount = window.BOARD[color.toUpperCase()];
      if (goalCount > 0) {
        c = new Crystal(goalBoard, new Loc(i, 0));
        c.lit = color;
        i++;
      }
    }
    this.goalContainer.count = i;
    spaceCoef = 5 / 6;
    pushCoef = 1 / 4;
    _ref3 = goalBoard.allHexesOfClass("Crystal");
    for (_m = 0, _len2 = _ref3.length; _m < _len2; _m++) {
      c = _ref3[_m];
      spr = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/crystal.png");
      spr.lit = false;
      spr.color = c.lit;
      spr.hex = c;
      spr.position.x = c.loc.row * this.hexRad * spaceCoef;
      spr.anchor.x = spr.anchor.y = 0.5;
      spr.scale.x = spr.scale.y = 0.25;
      this.goalContainer[c.lit.toUpperCase()].addChild(spr);
      goalCount = window.BOARD[c.lit.toUpperCase()];
      this.goalContainer[c.lit.toUpperCase()].goalCount = goalCount;
      goalStyle = this.menuContentStyle;
      text = new PIXI.Text("0/" + goalCount, goalStyle);
      text.position.x = c.loc.row * this.hexRad * spaceCoef + this.hexRad * pushCoef;
      text.position.y = -12;
      text.color = c.lit;
      this.goalContainer[c.lit.toUpperCase()].addChild(text);
    }
    for (_n = 0, _len3 = colors.length; _n < _len3; _n++) {
      c = colors[_n];
      if (window.BOARD[c.toUpperCase()] === 0) {
        this.colorContainers[c.toUpperCase()].alpha = 0;
        _ref4 = window.BOARD.allHexesOfClass("Spark");
        for (_o = 0, _len4 = _ref4.length; _o < _len4; _o++) {
          spark = _ref4[_o];
          s = spark.getAvailableColors();
          s.splice(s.indexOf(c), 1);
          spark.setAvailableColors(s);
        }
      } else {
        this.colorContainers[c.toUpperCase()].alpha = 1;
      }
    }
    window.BOARD.relight();
    document.body.appendChild(renderer.view);
    window.resize();
    window.drawBoard();
    return this.resize();
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

  this.hexRad = 110;


  /* Creates a single sprite for a hex and adds it to stage */

  this.createSpriteForHex = function(hex) {
    var backpanel, c, combo, con, cpanel, diff, fixAndRotateConnector, i, indices, j, pairCombos, radTo60Degree, sideOne, sidePanels, sideTwo, spr, theSide, _i, _j, _k, _l, _len, _len1, _m, _ref, _ref1, _ref2, _ref3, _ref4;
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
        spr = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/hex-back.png");
      } else if (hex instanceof Crystal) {
        spr = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/crystal.png");
      } else if (hex instanceof Spark) {
        spr = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/spark.png");
      }
      spr.lit = false;
      spr.anchor.x = 0.5;
      spr.anchor.y = 0.5;
      spr.hex = hex;
      backpanel.addChild(spr);
      backpanel.spr = spr;
      hex.spr = spr;
      sidePanels = [];
      fixAndRotateConnector = function(connector, side, color, panel) {
        connector.linked = false;
        connector.anchor.x = 0.5;
        connector.anchor.y = 0.5;
        connector.color = color;
        connector.rotation = side * radTo60Degree;
        connector.panel = panel;
        panel.addChild(connector);
      };
      if (hex instanceof Prism) {
        _ref = Color.regularColors();
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          c = _ref[_i];
          if (hex.colorCount(c) > 0) {
            cpanel = new PIXI.DisplayObjectContainer();
            cpanel.position.x = hex.loc.col * this.hexRad * 3 / 4 * 1.11 + this.hexRad * (5 / 8);
            cpanel.position.y = hex.loc.row * this.hexRad + this.hexRad * (5 / 8);
            if (hex.loc.col % 2 === 1) {
              cpanel.position.y += this.hexRad / 2;
            }
            cpanel.pivot.x = 0.5;
            cpanel.pivot.y = 0.5;
            cpanel.color = c;
            sidePanels.push(cpanel);
            indices = [];
            for (i = _j = 0, _ref1 = Hex.SIDES - 1; _j <= _ref1; i = _j += 1) {
              if (hex.colorOfSide(i) === c.toLowerCase()) {
                indices.push(i);
              }
            }
            if (indices.length === 1) {
              con = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/connector-none.png");
              con.sides = [indices[0]];
              fixAndRotateConnector(con, indices[0], c, cpanel);
            } else {
              pairCombos = [];
              for (i = _k = 0, _ref2 = indices.length - 1; _k <= _ref2; i = _k += 1) {
                for (j = _l = _ref3 = i + 1, _ref4 = indices.length - 1; _l <= _ref4; j = _l += 1) {
                  pairCombos.push(indices[j] * 10 + indices[i]);
                }
              }
              for (_m = 0, _len1 = pairCombos.length; _m < _len1; _m++) {
                combo = pairCombos[_m];
                sideOne = combo % 10;
                sideTwo = Math.floor(combo / 10);
                diff = __modulo(sideTwo - sideOne, Hex.SIDES);
                theSide = -1;
                switch (diff) {
                  case 0:
                    con = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/connector-opposite.png");
                    theSide = sideTwo;
                    break;
                  case 1:
                    con = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/connector-adjacent.png");
                    theSide = sideTwo;
                    break;
                  case 2:
                    con = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/connector-far-neighbor.png");
                    theSide = sideTwo;
                    break;
                  case 3:
                    con = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/connector-opposite.png");
                    theSide = sideOne;
                    break;
                  case 4:
                    con = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/connector-far-neighbor.png");
                    theSide = sideOne;
                    break;
                  case 5:
                    con = PIXI.Sprite.fromImage(this.siteprefix + "assets/img/connector-adjacent.png");
                    theSide = sideOne;
                }
                con.sides = [sideOne, sideTwo];
                fixAndRotateConnector(con, theSide, c, cpanel);
              }
            }
            this.colorContainers[c].unlit.addChild(cpanel);
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
      if (hex instanceof Prism) {
        this.base.addChild(backpanel);
      }
      if (hex.isLocked) {
        backpanel.interactive = false;
        spr.alpha = 0;
      } else {
        backpanel.interactive = true;
      }
      backpanel.click = function(event) {
        if (window.gameOn) {
          if (!event.originalEvent.shiftKey) {
            hex.click();
          } else {
            hex.anticlick();
          }
        }
      };
    }
    return hex.panel;
  };

}).call(this);