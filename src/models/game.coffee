class @Game

  # Difficulty enum
  @_difficulty = 
    EASY : 0
    MEDIUM : 1
    HARD : 2

  @difficultyCount : () -> Object.keys(@_difficulty).length
  @difficultyValues : () -> Object.keys(@_difficulty)

  @EASY = @_difficulty.EASY
  @MEDIUM = @_difficulty.MEDIUM
  @HARD = @_difficulty.HARD

  @asString : (difficulty) ->
    switch(difficulty)
      when @EASY
        return "easy"
      when @MEDIUM
        return "medium"
      when @HARD
        return "hard"
      else
        return "NO SUCH DIFFICULTY"

