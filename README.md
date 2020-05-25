# Parkour Minigame
## Remake of CubeCraft's Parkour Minigame

pls don't steal kthx

Tested (and recommended) Minecraft (server) version: **1.11.2**

Config files not included.

Instructions below

How to set up:

1. In the plugin folder (*plugins\ParkourMinigame*) create a file named config.yml and add a lobby location. This will be the location to where players get teleported when they finish the game.

## Example:

```
lobby:
  world: world
  x: -140
  y: 77
  z: 135
  yaw: 45
  pitch: 15
  ```
  
2. In the plugin folder (*plugins\ParkourMinigame*) create a folder named gamedata, and in there create a .yml file for each game world you have, with the world's name.

3. Add the following values to that file:
```
name: name of the world (will appear in scoreboard)
minPlayers: the minimum players required for the game to start
maxPlayers: the maximum players the game can have
x-range-1: x coordinate of the edge of the parkour
x-range-2: x coordinate of the other side of the edge of the parkour
y-range-1: same thing for the y coordinate
y-range-2: same thing for the y coordinate
z-range-1: same thing for the z coordinate
z-range-2: same thing for the z coordinate
spawn: the spawn location for when you fall and when the game starts
lobbyspawn: the spawn location for the game lobby (the place where you go when you join the game and have to wait for players)
```

## Example:

```
name: farm
minPlayers: 2
maxPlayers: 10

# The first should be the lower number and the second the higher
x-range-1: -6
x-range-2: 28
y-range-1: 1
y-range-2: 9
z-range-1: -10
z-range-2: 21

# Make sure the y coordinate of the spawns is higher than the y-range-1. This is due to the plugin teleporting you to the spawn location again if you fall lower than the y coordinate.
spawn:
  world: farm
  x: 8.5
  y: 4
  z: 8.5
  yaw: -90
  pitch: 0
lobbyspawn:
  world: farm
  x: 10
  y: 4       
  z: -22
  yaw: 0
  pitch: 0
```


# that's it.
## put the plugin in the plugins folder, start the server, (import the game world (/mv import world normal) if not imported) and use /pm to see the commands
