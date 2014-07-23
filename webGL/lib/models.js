// Generated by CoffeeScript 1.7.1
(function() {
  var Color, ColorCircle, Loc,
    __modulo = function(a, b) { return (a % b + +b) % b; },
    __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; },
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  Color = (function() {
    function Color() {}

    Color._val = {
      NONE: 0,
      RED: 1,
      BLUE: 2,
      GREEN: 3,
      ORANGE: 4,
      PURPLE: 5,
      CYAN: 6,
      YELLOW: 7,
      PINK: 8
    };

    Color.NONE = Color._val.NONE;

    Color.RED = Color._val.RED;

    Color.BLUE = Color._val.BLUE;

    Color.GREEN = Color._val.GREEN;

    Color.ORANGE = Color._val.ORANGE;

    Color.PURPLE = Color._val.PURPLE;

    Color.CYAN = Color._val.CYAN;

    Color.YELLOW = Color._val.YELLOW;

    Color.PINK = Color._val.PINK;

    Color.count = function() {
      return Object.keys(Color).length;
    };

    Color.values = function() {
      return Object.keys(Color);
    };

    Color.subValues = function(offset, n) {
      var c;
      c = Color.count;
      return Color.values().splice(__modulo(offset, c), __modulo(offset + n, c));
    };

    Color.noneArray = function(length) {
      var i, _i, _ref, _results;
      _results = [];
      for (i = _i = 0, _ref = length - 1; _i < _ref; i = _i += 1) {
        _results.push(Color.NONE);
      }
      return _results;
    };

    return Color;

  })();

  ColorCircle = (function() {
    function ColorCircle() {}


    /* Creates a circularly linked list of colorCircles from an array of colors.
        Returns the first colorCircle (head). Returns null if the input is null or length 0
     */

    ColorCircle.fromArray = function(colors) {
      var c, cc, i, l, t, _i, _ref;
      if (colors === null || colors.length === 0) {
        return null;
      }
      l = colors.length;
      t = (function() {
        var _i, _len, _results;
        _results = [];
        for (_i = 0, _len = colors.length; _i < _len; _i++) {
          c = colors[_i];
          _results.push(new ColorCircle(c, null, null));
        }
        return _results;
      })();
      for (i = _i = 0, _ref = l - 1; _i <= _ref; i = _i += 1) {
        cc = t[i];
        cc.size = l;
        cc.prev = t[__modulo(i - 1, l)];
        cc.next = t[__modulo(i + 1, l)];
      }
      return t[0];
    };


    /* Returns a random color array of a given length. Uses at most maxColors (or 6 if maxColors > 6) colors, never uses NONE.
        throws IllegalArgumentException if length <= 0 or maxColors <= 0
     */

    ColorCircle.randomArray = function(length, maxColors) {
      var a, i;
      if (length <= 0) {
        throw "Can't make Color Array of length " + length + " for color circle";
      }
      if (maxColors <= 0) {
        throw "Can't make Color Array of length using at most " + maxColors + " colors";
      }
      a = (function() {
        var _i, _ref, _results;
        _results = [];
        for (i = _i = 0, _ref = length - 1; _i <= _ref; i = _i += 1) {
          _results.push(Color.values()[1 + Math.floor(Math.random() * (Math.min(maxColors, Color.values().length - 1)))]);
        }
        return _results;
      })();
      return a;
    };


    /* Creates a random color circle of the given length. Uses at most maxColors (or 6 if maxColors > 6) colors, never uses NONE.
        throws IllegalArgumentException if length <= 0 or maxColors <= 0
     */

    return ColorCircle;

  })();

  this.random = function(length, maxColors) {
    var a;
    a = this.fromArray(this.randomArray(length, maxColors));
    return a;
  };

  ({

    /* Constructs a color circle with the given inputs. Should not be used outside of this file - use helpers */
    constructor: function(col, prev, next) {
      this.color = col;
      this.prev = prev;
      this.next = next;
      return this.size = 0;
    },

    /* Returns the color of this link */
    getColor: function() {
      return this.color;
    },

    /* Returns the next link */
    getNext: function() {
      return this.next;
    },

    /* Returns the previous link */
    getPrevious: function() {
      return this.previous;
    },

    /* Returns the size of this circle */
    getSize: function() {
      return this.size;
    },

    /* Converts this to an array of colors */
    toArray: function() {
      var a, arr;
      arr = [];
      a = this;
      while (true) {
        arr.push(a.color);
        a = a.next;
        if (a === this) {
          break;
        }
      }
      return arr;
    },

    /* Returns a string representation of the colorCircle starting with this */
    toString: function() {
      return this.toArray.toString();
    },

    /* Two color circles are equal if their sizes are equald and they have the same color at every position */
    equals: function(o) {
      var c, d;
      if (!(o instanceof ColorCircle)) {
        return false;
      }
      c = ColorCircle(o);
      d = this;
      if (this.size !== c.size) {
        return false;
      }
      while (true) {
        if (c.color !== d.color) {
          return false;
        }
        c = c.next;
        d = d.next;
        if (d === this) {
          break;
        }
      }
      return true;
    }
  });


  /*A simple location class - nice int tuple.
   Using this over the Point class because constantly shifting from (x,y) to (col,row) is confusing
   */

  Loc = (function() {
    function Loc(row, col) {
      this.row = row;
      this.col = col;
    }

    Loc.prototype.equals = function(o) {
      var l;
      if (!o instanceof Loc) {
        return false;
      } else {
        l = Loc(o);
        return this.row === l.row && this.col === l.col;
      }
    };

    Loc.prototype.toString = function() {
      return "(" + this.row + "," + this.col + ")";
    };

    Loc.NOWHERE = new Loc(-9999, -9999);

    return Loc;

  })();

  this.Board = (function() {

    /* Constructor for an empty board of size rs*cs */
    function Board(rs, cs) {
      var c, r, _i, _j, _ref, _ref1;
      if (rs < 0 || cs < 0) {
        throw "Illegal Board Construction for Dimensions " + rs + ", " + cs;
      }
      this.height = rs;
      this.width = cs;
      this.board = [];
      for (r = _i = 0, _ref = rs - 1; _i <= _ref; r = _i += 1) {
        this.board.push([]);
        for (c = _j = 0, _ref1 = cs - 1; _j <= _ref1; c = _j += 1) {
          this.board[r].push(null);
        }
      }
      this.game = null;
    }


    /* Returns the height of this board */

    Board.prototype.getHeight = function() {
      return this.height;
    };


    /* Returns the width of this board */

    Board.prototype.getWidth = function() {
      return this.width;
    };


    /* Returns the game this board belongs to (if any) */

    Board.prototype.getGame = function() {
      return this.game;
    };


    /* Sets the game this board belongs to. Throws a runtime exception if game is already set */

    Board.prototype.setGame = function(g) {
      if (this.game !== null) {
        throw "Can't set Game of " + this + " to " + g + " because it is already " + game;
      }
      this.game = g;
    };


    /* Gets rid of this board - signifies that it is no longer used */

    Board.prototype.dispose = function() {
      this.game = null;
      this.board = null;
    };


    /* Returns the index (0 ... Hex.SIDES - 1) of the side of h1 that is facing h2. 
        Returns -1 if either are null or are not neighbors
     */

    Board.prototype.indexLink = function(h1, h2) {
      var h1Neighbors, i, _i, _ref;
      if (h1 === null || h2 === null) {
        return -1;
      }
      h1Neighbors = h1.getNeighborsWithBlanks();
      for (i = _i = 0, _ref = Hex.SIDES; _i <= _ref; i = _i += 1) {
        if (h2 === h1Neighbors[i]) {
          return i;
        }
      }
      return -1;
    };


    /* Returns the color that links h1 and h2. 
        Returns none if either is null or they are not neighbors, or they are not color linked
     */

    Board.prototype.colorLinked = function(h1, h2) {
      var c1, c2, index;
      index = this.indexLink(h1, h2);
      if (index === -1) {
        return Color.NONE;
      }
      c1 = h1.colorOfSide(index);
      c2 = h2.colorOfSide(__modulo(index + Hex.SIDES / 2, Hex.SIDES));
      if (c1 === c2) {
        return c1;
      } else {
        return Color.NONE;
      }
    };


    /* Returns the hex at the given location */

    Board.prototype.getHex = function(loc) {
      return this.board[loc.row][loc.col];
    };


    /* Returns a flattened version of the board - all hexes in no particular order */

    Board.prototype.allHexes = function() {
      var a, arr, h, _i, _j, _len, _len1, _ref;
      arr = [];
      _ref = this.board;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        a = _ref[_i];
        for (_j = 0, _len1 = a.length; _j < _len1; _j++) {
          h = a[_j];
          arr.push(h);
        }
      }
      return arr;
    };


    /* Sets the hex at position (r,c). Also sets all neighbor hexes as needing a neighbor update.
        Hex must have this as its board. Used in hex construciton, not much elsewhere
     */

    Board.prototype.setHex = function(h, r, c) {
      var n, _i, _len, _ref;
      if (h.board !== this) {
        throw "Can't put hex belonging to " + h.board + " in board " + this;
      }
      this.board[r][c] = h;
      _ref = h.getNeighbors();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        n = _ref[_i];
        n.neighborsUpdated = true;
      }
    };


    /* Re-calculates light on whole board */

    Board.prototype.relight = function() {
      var h, _i, _len, _ref;
      _ref = this.allHexes;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        h = _ref[_i];
        h.light();
      }
    };


    /* Two boards are equal if they have the same board */

    Board.prototype.equals = function(o) {
      var b;
      if (!(o instanceof Board)) {
        return false;
      }
      b = Board(o);
      return b.board === this.board;
    };


    /* Returns a string representation of this board as an ascii matrix */

    Board.prototype.toString = function() {
      var el, r, s, _i, _j, _k, _len, _len1, _len2, _ref;
      s = "";
      _ref = this.board;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        r = _ref[_i];
        for (_j = 0, _len1 = r.length; _j < _len1; _j++) {
          el = r[_j];
          if (el === null) {
            s = s + "   |";
          } else if (el instanceof Prism) {
            s = s + " p |";
          } else if (el instanceof Spark) {
            s = s + " s |";
          } else {
            s = s + " c |";
          }
        }
        s = s + "\n";
        for (_k = 0, _len2 = r.length; _k < _len2; _k++) {
          el = r[_k];
          s = s + "----";
        }
        s = s + "\n";
      }
      return s;
    };


    /* Makes a random board with the given dimentions. Mainly for testing. Sparks/Crystals at corners, prisms otw */

    Board.makeBoard = function(rs, cs, cls) {
      var b, c, r, _i, _j, _ref, _ref1;
      b = new Board(rs, cs);
      for (r = _i = 0, _ref = rs - 1; _i <= _ref; r = _i += 1) {
        for (c = _j = 0, _ref1 = cs - 1; _j <= _ref1; c = _j += 1) {
          if (r === 0 && c === 0 || r === (rs - 1) && c === 0) {
            new Spark(b, new Loc(r, c), Color.subValues(1, cls));
          } else if (r === 0 && c === (cs - 1) || r === (rs - 1) && c === (cs - 1)) {
            new Crystal(b, new Loc(r, c));
          } else {
            new Prism(b, new Loc(r, c), ColorCircle.randomArray(Hex.SIDES, cls));
          }
        }
      }
      return b;
    };

    return Board;

  })();

  this.Game = (function() {

    /* Creates a game to wrap the given board */
    function Game(board) {
      this.setBoard(board);
    }


    /* Sets the board for this game - disposes of older board, if any */

    Game.prototype.setBoard = function(board) {
      if ((this.board !== null && this.board !== board)(this.board.dispose)) {
        return this.board = board;
      }
    };


    /* Returns the board for this game */

    Game.prototype.getBoard = function() {
      return this.board;
    };


    /* Called when a hex alters itself on the board - should repaint as necessary */

    Game.prototype.updateHex = function(h) {
      throw "Can't Instantiate Type Game - updateHex must be overriden in subclasses";
    };


    /* Resets this game to its initial settings */

    Game.prototype.reset = function() {
      throw "Can't Instantiate Type Game - reset must be overriden in subclasses";
    };


    /* Should return the difficulty of this game as an integer */

    Game.prototype.getDifficulty = function() {
      throw "Can't Instantiate Type Game - getDifficulty must be overriden in subclasses";
    };

    return Game;

  })();


  /* Abstract parent of all tiles */

  this.Hex = (function() {
    Hex.SIDES = 6;

    Hex.NEIGHBOR_COORDINATES = [[new Loc(-1, 0), new Loc(-1, 1), new Loc(0, 1), new Loc(1, 0), new Loc(0, -1), new Loc(-1, -1)], [new Loc(-1, 0), new Loc(0, 1), new Loc(1, 1), new Loc(1, 0), new Loc(1, -1), new Loc(0, -1)]];


    /*Stores Board b and Point p as board and location in this hex.
       Throws IllegalArgumentException if b is null, point p is already occupied on board b,
       Or if the location is out of bounds.
     */

    function Hex(board, loc) {
      var a, vec;
      try {
        if (board === null) {
          throw "Can't put hex into null board";
        }
        if (board.getHex(loc) !== null) {
          throw "Board " + board + " already has hex at position " + "(" + loc.row + "," + loc.col + "), can't construct new hex there.";
        }
      } catch (_error) {
        a = _error;
        throw "Can't construct hex in " + board.toString + " at " + loc + ": " + a.message;
      }
      this.board = board;
      this.loc = loc;
      this.neighbors = (function() {
        var _i, _len, _ref, _results;
        _ref = Hex.NEIGHBOR_COORDINATES[__modulo(loc.col, 2)];
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          vec = _ref[_i];
          _results.push(new Loc(loc.row + vec.row, loc.col + vec.col));
        }
        return _results;
      })();
      this.neighborsUpdated = true;
      this.neighborHexes = [];
      this.board.setHex(this, loc.row, loc.col);
      this.lighters = [];
    }


    /*Returns the neighbors of this hex, clockwise from above. Will always return an array of lenght SIDES,
       but may contain nulls.
       Spots that this does not have a neighbor (off the board) are stored as null.
       Part of lazy calculation of neighborHexes - only updates if neighborsUpdated is true.
       Otherwise returns the (already calculated) neighborHexes
     */

    Hex.prototype.getNeighborsWithBlanks = function() {
      var e, l, neighborsUpdated;
      if (neighborsUpdated) {
        this.neighborHexes = (function() {
          var _i, _len, _ref, _results;
          _ref = this.neighbors;
          _results = [];
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            l = _ref[_i];
            try {
              _results.push(board.getHex(l.row, l.col));
            } catch (_error) {
              e = _error;
              _results.push(null);
            }
          }
          return _results;
        }).call(this);
        neighborsUpdated = false;
        return this.neighborHexes;
      } else {
        return this.neighborHexes;
      }
    };


    /*Returns the neighbors of this hex, clockwise from above, with nulls removed. 
       Thus no null elements, but resulting array has length 0 <= x <= SIDES
     */

    Hex.prototype.getNeighbors = function() {
      var a, h, _i, _len, _results;
      a = this.getNeighborsWithBlanks;
      _results = [];
      for (_i = 0, _len = a.length; _i < _len; _i++) {
        h = a[_i];
        if (h(!null)) {
          _results.push(h);
        } else {

        }
      }
      return _results;
    };

    Hex.prototype.indexLinked = function(h) {
      return this.board.indexLinked(this, h);
    };

    Hex.prototype.colorLinked = function(h) {
      return this.board.colorLinked(this, h);
    };


    /* Returns the colors this is lit. Returns empty if this isn't lit */

    Hex.prototype.isLit = function() {
      var key, val, _ref, _results;
      _ref = this.lighters;
      _results = [];
      for (key in _ref) {
        val = _ref[key];
        _results.push(val);
      }
      return _results;
    };


    /* Returns a set of locations (hexes) that all eventually provide light to this of a given color.
        Can be used to prevent cycles from forming.
     */

    Hex.prototype.lighterSet = function(c) {
      var arr, k, key, val, _i, _len, _ref, _ref1;
      arr = null;
      _ref = this.lighters;
      for (key in _ref) {
        val = _ref[key];
        if (val === c) {
          arr.push(key);
          _ref1 = key.lighterSet(c);
          for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
            k = _ref1[_i];
            arr.push(k);
          }
        } else {

        }
      }
      return arr;
    };


    /* Fixes light for this hex. Must be implemented in subclasses. */

    Hex.prototype.light = function() {
      throw "Cannot instantiate Hex Class - light method must be overriden";
    };


    /* Helper method for use in light implementations. Removes lighters that can't light this anymore from lighters map
        Returns true if at least one lighter was removed, false otherwise
     */

    Hex.prototype.pruneLighters = function() {
      var c, h, l, oldArr, val, y, _i, _len;
      oldArr = (function() {
        var _ref, _results;
        _ref = this.lighters;
        _results = [];
        for (y in _ref) {
          val = _ref[y];
          _results.push(y);
        }
        return _results;
      }).call(this);
      for (_i = 0, _len = oldArr.length; _i < _len; _i++) {
        l = oldArr[_i];
        h = this.board.getHex(l);
        c = this.colorLinked(h);
        if (c === Color.NONE || __indexOf.call(h.isLit(), c) < 0) {
          delete this.lighters.l;
        }
      }
      return oldArr.length !== Object.keys(this.lighters).length;
    };


    /* Helper method for use in findLight implementations. Tells neighbors this is currently lighting to look elsewhere */

    Hex.prototype.stopProvidingLight = function() {
      var c, cond1, cond2, cond3, h, _i, _len, _ref;
      c = this.isLit();
      _ref = this.getNeighbors();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        h = _ref[_i];
        cond1 = this.loc in h.lighters;
        cond2 = !(h.lighters[this.loc] in c);
        cond3 = colorLinked(h)(!h.lighters[this.loc]);
        if (cond1 && (cond2 || cond3)) {
          h.light();
        }
      }
    };


    /* Helper method for use in findLight implementations. Tells neighbors that this is now lit, 
       maybe get light from this, if not already or this getting light from that.
       If this isn't lit, do nothing. 
       
       Note: Always try to provide light to crystal, never try to provide light to spark. Neither of these recurse, so no trouble.
       Sparks can always provide light, others can only provide light if they have a lighter
     */

    Hex.prototype.provideLight = function() {
      var c, h, hLit, lit, _i, _len, _ref;
      if (this instanceof Spark || (Object.keys(this.lighters).length > 0)) {
        lit = this.isLit();
        _ref = getNeighbors();
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          h = _ref[_i];
          hLit = h.isLit();
          c = this.colorLinked(h);
          if (!(h instanceof Spark) && ((h instanceof Crystal && hlit.length === 0) || (h instanceof Prism && c in lit && !c in hLit))) {
            h.light();
          }
        }
      }
    };


    /* Helper method for use in findLight implementations. Tries to find light among neighbors.
        If a link is found, sets that neighbor as lighter. If no link found, sets lighter to null.
        Only looks for preferred. If preferred is NONE, takes any color.
     */

    Hex.prototype.findLightProvider = function(preferred) {
      var c, h, hLit, _i, _len, _ref;
      _ref = this.getNeighbors();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        h = _ref[_i];
        hLit = h.isLit();
        c = this.colorLinked(h);
        if (c in hLit && (preferred === Color.NONE || preferred === c) && __indexOf.call(h.lighterSet(c), this) < 0) {
          lighters[h.loc] = c;
        }
      }
    };


    /* Returns the color of side n of this hex (where side 0 is the top).
       IllegalArgumentException if n < 0, n > SIDES - 1
     */

    Hex.prototype.colorOfSide = function(n) {
      return "Cannot instantiate Hex Class - colorOfSide method must be overriden";
    };


    /* Perform default behavior for interacting with this hex */

    Hex.prototype.click = function() {
      return "Cannot instantiate Hex Class - click method must be overriden";
    };


    /* Returns the location of this hex as the string for this hex */

    Hex.prototype.toString = function() {
      return this.loc.toString();
    };


    /* Two hexes are equal if their boards and their locations are the same */

    Hex.prototype.equals = function(o) {
      var h;
      if (!(o instanceof Hex)) {
        return false;
      } else {
        h = Hex(o);
        return this.board === h.board && this.location === h.location;
      }
    };


    /* Signifies that this has been changed; tells the game (if any) to update this as necessary. */

    Hex.prototype.update = function() {
      if ((this.board(!null)) && (this.board.getGame()(!null))) {
        this.board.getGame().updateHex(this);
      }
    };

    return Hex;

  })();

  this.Crystal = (function(_super) {
    __extends(Crystal, _super);


    /* Constructs a Crystal and puts it into board b
        @param b - the board this spark belongs to
        @param l - the location of this crystal in (row, col) in the board
        @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB.
     */

    function Crystal(board, loc) {
      Crystal.__super__.constructor.call(this, board, loc);
      this.lit = Color.NONE;
    }


    /* @Override
        Try to find light like a prism, but don't ever provide light. Thus only look for a provider, don't need to recurse.
        Only find single light color.
     */

    Crystal.prototype.light = function() {
      var lighterChanged, lit;
      lighterChanged = this.pruneLighters();
      if (lighterChanged) {
        this.findLightProviders(lit);
        if (this.isLit().length === 0) {
          this.findLightProviders(Color.NONE);
        }
      }
      if (this.isLit().length === 0) {
        lit = Color.NONE;
      } else {
        lit = this.isLit()[0];
      }
      update();
    };


    /* @Override
        Helper method for use in findLight implementations. Tries to find light among neighbors.
        Overrides hex findLightProvider so that it can take any color of light, no matter the side color.
        Only looks for preferred. If preferred is NONE, takes any color. Only takes on e color
     */

    Crystal.prototype.findLightProviders = function(preferred) {
      var c, h, hLit, _i, _len, _ref;
      _ref = this.getNeighbors();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        h = _ref[_i];
        hLit = h.isLit();
        c = h.colorOfSide(h.indexLink(this));
        if ((hLit.length > 0 && (preferred === Color.NONE || preferred in hLit)) && c in hLit) {
          lighters[h.loc] = c;
          return;
        }
      }
    };


    /* @Override
        All sides of this crystal are the color of its lighter. (Not that this can provide light)
     */

    Crystal.prototype.colorOfSide = function(n) {
      if (n < 0 || n > Hex.SIDES - 1) {
        throw "Can't find color of side " + n + " of " + this;
      }
      return lit;
    };


    /* Interacting with a Crystal does nothing - do nothing here */

    Crystal.prototype.click = function() {};

    return Crystal;

  })(Hex);

  this.Prism = (function(_super) {
    __extends(Prism, _super);

    Prism.ROTATE_CLOCKWISE = true;


    /* Constructs a Prism and puts it into board b
       @param b - the board this prism belongs to
       @param l - the location of this prism in (row, col) in the board
       @param colors - the colors of this prism, in clockwise order starting with the top. Can be null, then set later.
       @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length != SIDES.
     */

    function Prism(board, loc, colors) {
      Prism.__super__.constructor.call(this, board, loc);
      this.setColorCircle(colors);
    }


    /* Returns the colors of this prism, clockwise from the current top */

    Prism.prototype.colorArray = function() {
      return colorCircle.toArray();
    };


    /* Allows setting the ColorCircle, but only if it isn't set yet (is null).
       @throws IllegalArgumentException if the colorCircle is currently non-null
     */

    Prism.prototype.setColorCircle = function(colors) {
      if (this.availableColors !== void 0 && this.availableColors !== null && this.availableColors.length > 0) {
        return;
      }
      this.colorCircle = ColorCircle.fromArray(colors);
    };

    Prism.prototype.rotate = function() {
      this.board.moves++;
      this.colorCircle = this.colorCircle.getPrevious();
      this.light();
    };

    Prism.prototype.rotateCounter = function() {
      this.board.moves++;
      this.colorCircle = this.colorCircle.getNext();
      this.light();
    };


    /* @Override
        Returns the colorCircle at the correct index for the color of a given side
     */

    Prism.prototype.colorOfSide = function(n) {
      if (n < 0 || n > Hex.SIDES - 1) {
        throw "Illegal Side Number " + n;
      }
      return this.colorCircle.toArray()[n];
    };


    /* @Override
       Tries to find light by looking at all neighbor hexes that this isn't providing light to
       Tries to stay the same color of light if multiple are avaliable. Otherwise chooses arbitrarily.
       Returns the color this is lit at the end of the procedure, false otherwise
     */

    Prism.prototype.light = function() {
      this.pruneLighters();
      this.stopProvidingLight();
      this.findLightProviders(Color.NONE);
      this.provideLight();
      this.update();
    };


    /* Default behavior for a Prism is to rotate. Rotates clockwise if ROTATE_CLOCKWISE, rotates counterclockwise otherwise. */

    Prism.prototype.click = function() {
      if (Prism.ROTATE_CLOCKWISE) {
        this.rotate();
      } else {
        this.rotateCounter();
      }
    };

    return Prism;

  })(Hex);

  this.Spark = (function(_super) {
    __extends(Spark, _super);


    /* Constructs a Spark and puts it into board b
       @param b - the board this spark belongs to
       @param l - the location of this spark in (row, col) in the board
       @param colors - the colors of this spark, in clockwise order starting with the top. Can be null, then set later.
       @throws IllegalArgumentException - if there is already hex at row,col, or row,col is OOB, or if colors is nonnull and length == 0.
     */

    function Spark(board, loc, colors) {
      Spark.__super__.constructor.call(this, board, loc);
      this.setAvailableColors(colors);
    }


    /* Returns the current color of this spark */

    Spark.prototype.getColor = function() {
      return this.availableColors.getColor();
    };


    /* Makes this spark use the next available color. Relights and redraws */

    Spark.prototype.useNextColor = function() {
      this.availableColors = this.availableColors.getNext();
      this.light();
      this.update();
    };


    /* Returns the available colors of this spark. */

    Spark.prototype.getavailableColors = function() {
      return this.availableColors.toArray();
    };


    /*Allows setting the ColorCircle, but only if it isn't set yet (is null). Don't call otherwise
     */

    Spark.prototype.setAvailableColors = function(colors) {
      if (this.availableColors !== void 0 && this.availableColors !== null && this.availableColors.length > 0) {
        return;
      }
      this.availableColors = ColorCircle.fromArray(colors);
    };


    /* @Override
        Sparks always find light because they always light themselves. No setting of fields neccesary
     */

    Spark.prototype.light = function() {
      this.stopProvidingLight();
      this.provideLight();
    };


    /* @Override
        Default behavior for a spark is to switch to the next available color
     */

    Spark.prototype.click = function() {
      this.useNextColor();
    };


    /* @Override
        Overrides Hex isLit() because Sparks are always lit
     */

    Spark.prototype.isLit = function() {
      var l;
      l = [];
      l.push(this.getColor());
      return l;
    };

    return Spark;

  })(Hex);

}).call(this);
