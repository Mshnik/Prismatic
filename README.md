
## Prismatic  

### Core Rules and Mechanics
Prismatic is a puzzle game based on a hexagonal board. There are three basic types of tiles in Prismatic: Prisms, Sparks, and Crystals.

The core of Prismatic are (unsurprisingly), prisms. A prism is a tile, adorned with up to 6 colored gems, each gem taking up a side of the hexagon. Players are able to rotate prisms, thereby changing the board state. There are 8 colors of gems, and thus colors of light: Red, Blue, Green, Orange, Purple, Cyan, Yellow, and Pink.

Prisms receive light (and become lit) by matching gems with an adjacent hex that is lit. Lit prisms then supply light through all other gems of a similar color. With some special exceptions, a prism can only be lit with a single color of light at a time. A prism can only receive light from a single prism at a time, but can supply light to any number of prisms.

The second type of tile in Prismatic is Sparks. Sparks provide light to adjacent prims of a single color to adjacent hexes, and are the starting point of all light paths. In some scenarios, players are able to change a spark’s color, while in others the color of the spark is fixed.

The final type of tile is Crystals. Crystals are the goal pieces in Prismatic – they receive light from adjacent prisms. Crystals will light with any color of light, but in some game modes lighting crystals with particular colors of light may convey greater value. In some game modes, simply lighting a crystal is the goal, and in others lighting crystals gives points towards a goal.

Some game modes may contain special tiles that go beyond the scope of the three basic types of tiles described above. How and where these tiles are used will be described in their respective game modes.

### Puzzle Mode
The first game mode in Prismatic is a classic puzzle mode. The player is presented with full vision of a board and no time limit. The goal is to fulfill the light requirements (lighting a certain number of crystals with given color combinations) with only a certain number of moves. In the context of puzzle mode, a “move” is a single 60-degree rotation of a single prism, either clockwise or counterclockwise. Sparks that allow color changes may be changed without costing a move, but all color requirements must be fulfilled simultaneously to clear the level—for example, if the color requirements of a level are one cyan crystal and one red crystal, both crystals must be lit their respective colors simultaneously.

### Exploration Mode
The second game mode in Prismatic is a time-based expansion mode, tentatively titled exploration. In this game mode, all tiles aside from a central spark are initially hidden. Whenever a tile becomes lit, it reveals all adjacent tiles that are unlock-able by that color of light—some tiles may not be revealed unless an adjacent tile is a specific color of light. Whenever a crystal is lit in exploration mode, it turns into a spark of the color it was lit, thus furthering the player’s exploration of the map. The map in exploration mode is “infinitely” large -- more map is created whenever the player reveals an edge.

Each game of exploration has a time limit in which the player attempts to reveal as many tiles as possible – 1 point per tile revealed. However, lighting crystals and revealing certain randomly distributed tiles on the map give extra time or extra points. The player can also pause the game to look at the map and formulate a plan, though cannot make any move while the game is paused.

There is a limit to how far any single color can go without reaching a dead end of adjacent prisms, so the player must carefully decide when to switch the color of the central spark and abandon their progress to take a new route of exploration. That said, prisms that become unlit this way do not become hidden again – once a tile is revealed in exploration mode, it stays revealed until the game ends.

### Battle Mode
The final (for now) and by far the most ambitious game mode in Prismatic is a two-player battle mode. The map is a larger hexagon (radius 3-5) with a single crystal at the center. Each player controls three sparks on opposing corners of the map—player one controls the top left, bottom left, and middle right sparks, and player two controls the remaining. Between the three sparks controlled by each player every color is represented.

The goal in battle is to beat your opponent to a set point total, or to gain more points than your opponent in a given time limit. Players gain points by lighting the central crystal with light from their sparks. Colors of light in battle mode each have a value, with a higher number corresponding to a higher valued light. The ranking determines the flow rate of points per second to the player who controls the crystal – for example, if you control the crystal with orange light, and orange light has a value of 5, you are gaining 5 points per second. Additionally, the ranking shows which colors will override the current lighting of the crystal. If I connect my red spark, which has a value of 7 to the crystal, your orange light will stop flowing and I will start gaining 7 points per second. However, the crucial twist is that the color rankings are fluid – For every 20 points gained by a given color of light, its value decreases by 1 and the value of all other colors of light increase by 1. My red light, initially my trump card and a vital source of points, decays and becomes both venerable and worthless. Additionally, this means that late game light is likely more valuable than early game light, thus battle mode rewards long-term planning such as setting up multiple paths and leaving them unlit until the time is right.

Both players have an “action bar” that fills over time. When it reaches a certain level, the player can deplete the bar to perform one move, rotating a single prism or changing the color of a spark they control. The overall speed of the action bar can be set in the game setup, but certain moves also cost different amounts of action. Rotating a prism you are lighting costs less (takes less long) than rotating a prism no player is lighting, which costs less than rotating a prism your opponent is lighting. This means that while interfering with your opponent can certainly be useful, it must be done strategically as you are at a disadvantage when undoing your opponent’s progress.

Finally, special “mission” tiles may appear periodically. They will replace a traditional prism on the map. When lit with a required color of light, they are replaced with a regular prism again and convey a benefit on the player that lit it. These benefits may be one- shot bonuses, such as flat point bonuses, immediately filling their action bar, immediately draining their opponent’s action bar. They may be continuous bonuses, such as an increased charge rate, or an increased action capacity. Finally, they may be one-shot activate bonuses, such as locking tiles, whirl-winding the map (rotating all prisms a random amount) or bombing the map (replacing all prisms with new prisms).

## Code and Implementations

The main/first implementation of Prismatic is for web deployment, using webGL as a framework. More on the details to come.

A simple java/swing implementation is in the javaSwing folder, and should be loadable by any IDE. This implementation will probably be unused in final production, except for perhaps a dev-only level editor. Two classes have main methods - use randomPuzzle's main to show a random puzzle, and loadedGame's main to load boards from memory. 
