### The various window-level values used. Kinda a constants file
requires: none
 ###

## A helper 
@typeIsArray = Array.isArray || ( value ) -> return {}.toString.call( value ) is '[object Array]'

@BOARDNAME = "board01" ## Most recent board loaded. Initial value is default
@initted  = false       ## True if a full init process has occured. False until then
@gameOn   = true       ## True if the board should respond to clicks, false otherwise (false when help is up)
@showWinContainer = true  ## True if the win container should be shown when the player wins
@difficulty = @Game.MEDIUM  ## Difficulty the player is currently on

@siteprefix = "" ## For the git site, routing assets. prepended onto all asset requests