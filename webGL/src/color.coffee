Color = 
  NONE : 0
  RED : 1
  BLUE : 2
  GREEN : 3
  ORANGE : 4
  PURPLE : 5
  CYAN : 6
  YELLOW : 7
  PINK : 8

Color.count() -> Object.keys(Color).length

Color.values() -> Object.keys(Color)

Color.subValues(offset, n) ->
  c = Color.count
  Color.values.splice(offset %% c, (offset + n) %% c)

Color.noneArray(length) ->
  Color.NONE for i in [0 ... (length - 1)] by 1
