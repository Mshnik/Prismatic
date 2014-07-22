// Generated by CoffeeScript 1.7.1
var ColorCircle,
  __modulo = function(a, b) { return (a % b + +b) % b; };

ColorCircle = (function() {

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
    for (i = _i = 0, _ref = l(-1); _i <= _ref; i = _i += 1) {
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
    var i, _i, _ref, _results;
    if (length <= 0) {
      throw new IllegalArgumentException("Can't make Color Array of length " + length + " for color circle");
    }
    if (maxColors <= 0) {
      throw new IllegalArgumentException("Can't make Color Array of length using at most " + maxColors + " colors");
    }
    _results = [];
    for (i = _i = 0, _ref = length - 1; _i <= _ref; i = _i += 1) {
      _results.push(Color.values()[1 + int(Math.random() * (Math.min(maxColors, Color.values().length - 1)))]);
    }
    return _results;
  };


  /* Creates a random color circle of the given length. Uses at most maxColors (or 6 if maxColors > 6) colors, never uses NONE.
      throws IllegalArgumentException if length <= 0 or maxColors <= 0
   */

  ColorCircle.random = function(length, maxColors) {
    return this.fromArray(this.randomArray(length, maxColors));
  };


  /* Constructs a color circle with the given inputs. Should not be used outside of this file - use helpers */

  function ColorCircle(col, prev, next) {
    this.color = col;
    this.prev = prev;
    this.next = next;
    this.size = 0;
  }


  /* Returns the color of this link */

  ColorCircle.prototype.getColor = function() {
    return this.color;
  };


  /* Returns the next link */

  ColorCircle.prototype.getNext = function() {
    return this.next;
  };


  /* Returns the previous link */

  ColorCircle.prototype.getPrevious = function() {
    return this.previous;
  };


  /* Returns the size of this circle */

  ColorCircle.prototype.getSize = function() {
    return this.size;
  };


  /* Converts this to an array of colors */

  ColorCircle.prototype.toArray = function() {
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
  };


  /* Returns a string representation of the colorCircle starting with this */

  ColorCircle.prototype.toString = function() {
    return this.toArray.toString();
  };


  /* Two color circles are equal if their sizes are equald and they have the same color at every position */

  ColorCircle.prototype.equals = function(o) {
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
  };

  return ColorCircle;

})();
