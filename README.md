# Endless Driving Game

This repository contains the code for a simple **Endless Driving Game** designed as part of a homework assignment. The game is a three-lane road race where the player avoids obstacles and survives as long as possible.

## Features

- **Three-Lane Road:** The game takes place on a straight road divided into three lanes.
- **Movable Car:** The player's car can move left and right between lanes.
- **Dynamic Obstacles:** obstacles randomly appear on the road.
- **Constant Speed:** The game simulates forward movement by having the obstacles approach the player.
- **Crash Notification:** Visual and tactile feedback is provided upon crashing (via a Toast message and vibration).
- **Lives System:** Players start with 3 lives.
- **Endless Gameplay:** The game continues after losing lives for additional replayability.

## How to Play

1. Use the **left** and **right** buttons to move the car between lanes.
2. Avoid crashing into obstacles that come toward the car.
3. The game ends when all three lives are lost.

## Requirements

- Android Studio (for building and testing the app).
- Android device or emulator to play the game.

## Technical Notes

- The project does not use a `Canvas` for rendering the game. Movement is achieved by manipulating object positions on the screen (using X and Y coordinates).
- The game implements a **Toast message** for crash notifications and vibration feedback for a better user experience.

## Setup and Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/Adirdavi/MyGame.git
