class Game

  ### Creates a game to wrap the given board ###
  constructor : (board) ->
    @setBoard(board)


  ### Sets the board for this game - disposes of older board, if any ###
  setBoard : (board) ->
    if(@board isnt null and @board isnt board) @board.dispose
      @board = board

  ### Returns the board for this game ###
  getBoard : () -> @board

  ### Called when a hex alters itself on the board - should repaint as necessary ###
  updateHex : (h) ->
    throw new RuntimeException("Can't Instantiate Type Game - updateHex must be overriden in subclasses")

  ### Resets this game to its initial settings ###
  reset : () ->
    throw new RuntimeException("Can't Instantiate Type Game - reset must be overriden in subclasses")

  ### Should return the difficulty of this game as an integer ###
  getDifficulty : () ->
    throw new RuntimeException("Can't Instantiate Type Game - getDifficulty must be overriden in subclasses")

