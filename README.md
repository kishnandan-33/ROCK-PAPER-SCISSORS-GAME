# Rock Paper Scissors Game - JavaFX

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-21-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-lightblue)

A feature-rich Rock Paper Scissors game with both single-player (vs AI) and multiplayer (LAN) modes, built with JavaFX and MySQL.

## Features

🎮 **Game Modes**:
- Play against AI with adjustable difficulty
- LAN multiplayer (host/client) functionality

📊 **Data Tracking**:
- Player statistics and win/loss records
- Match history with detailed results
- Leaderboard system

🌐 **Database**:
- MySQL backend for persistent data storage
- Automatic table initialization

## Technologies Used

- **Frontend**: JavaFX 21
- **Backend**: Java 17
- **Database**: MySQL 8.0
- **Build Tool**: Maven

## Installation

### Prerequisites
- Java 17+ JDK
- MySQL Server 8.0+
- Maven 3.6+

### Setup Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/kishnandan-33/ROCK-PAPER-SCISSORS-GAME.git
   cd ROCK-PAPER-SCISSORS-GAME/RockPaperScissorsFX

2.**Database Configuration**:
Create a database (or use existing one)

Update connection details in :

- DBUtil.java file
  

e.g.
```java
private static final String DB_URL = "jdbc:mysql://your-host:3306/your-database";
private static final String DB_USER = "your-username";
private static final String DB_PASSWORD = "your-password";
```

3.**Build and Run**:

```bash
  mvn clean install
  mvn javafx:run
```

## Project Structure

ROCK-PAPER-SCISSORS-GAME/
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── rockpaperscissors/
│   │   │           ├── Main.java
│   │   │           ├── Game.java
│   │   │           ├── Player.java
│   │   │           ├── AIPlayer.java
│   │   │           ├── Multiplayer.java
│   │   │           ├── Database.java
│   │   │           └── Leaderboard.java
│   │   └── resources/
│   │       └── images/
│   │           └── game_icons.png
├── imgs/
│   └── game_icons.png
├── pom.xml
└── .gitignore



## Screenshots

![username](imgs/Screenshot%202025-05-25%20220530.png)

![main menu](imgs/Screenshot%202025-05-25%20220542.png)

![play with bot](imgs/Screenshot%202025-05-25%20220558.png)

![Leaderboard](imgs/Screenshot%202025-05-25%20220617.png)


**How to Play**
- Single Player Mode:
  
  Select "Play with Bot"
  
  Choose your move (Rock, Paper, or Scissors)

  View your stats and win percentage

- Multiplayer Mode:

  Host: Click "Start as Host"

  Client: Enter host IP and click "Connect"

  Take turns making moves

  Results are saved to match history



