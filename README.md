# Rock Paper Scissors Game - JavaFX

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-21-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-lightblue)

A feature-rich Rock Paper Scissors game with both single-player (vs AI) and multiplayer (LAN) modes, built with JavaFX and MySQL.

## Features

🎮 **Game Modes**:
- Rock, Paper, Scissors game with score tracking
- LAN multiplayer (host/client) functionality
- User vs Computer gameplay
  

📊 **Data Tracking**:
- Player statistics and win/loss records
- Match history with detailed results
- Leaderboard system

🌐 **Database**:
- MySQL backend for persistent data storage
- Automatic table initialization

  
## Extra Features
- Rock, Paper, Scissors game with score tracking
- User vs Computer gameplay
- Real-time score display
- Background music
- Animation on Win/loss

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

### Setup Instructions

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
<br>

## Project Structure
```
RockPaperScissorsFX/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controllers/          # JavaFX controller classes handling UI logic
│   │   │   │   ├── MainMenuController.java
│   │   │   │   ├── BotGameController.java
│   │   │   │   ├── MultiplayerMenuController.java
│   │   │   │   ├── LeaderboardController.java
│   │   │   │   ├── MatchHistoryController.java
│   │   │   │   └── ...               # other controller classes
│   │   │   │
│   │   │   ├── models/               # Data model and utility classes
│   │   │   │   ├── DBUtil.java       # Database connection utilities
│   │   │   │   ├── Utils.java        # Utility helper methods (error dialogs, etc.)
│   │   │   │   └── DatabaseInitializer.java  # Database setup helper
│   │   │   │
│   │   │   ├── RockPaperScissorsApp.java  # Main application entry point
│   │   │   └── ...                   # any other core classes
│   │   │
│   │   └── resources/
│   │       ├── fxml/                 # FXML files defining UI layouts
│   │       │   ├── MainMenu.fxml
│   │       │   ├── BotGame.fxml
│   │       │   ├── MultiplayerMenu.fxml
│   │       │   ├── Leaderboard.fxml
│   │       │   ├── MatchHistory.fxml
│   │       │   └── ...
│   │       │
│   │       ├── images/               # Image and GIF assets
│   │       │   ├── win.gif
│   │       │   ├── loss.gif
│   │       │   └── ...
│   │       │
│   │       ├── sounds/               # Audio files for sound effects
│   │           ├── click.wav
│   │           ├── mclick.wav
│   │           ├── le.wav
│   │           └── cry.wav
│
├── README.md                       # Project documentation file       
└── pom.xml       # Build configuration files (if using Maven/Gradle)

```
<br>

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



## Screenshots

![Register](imgs/Screenshot%202025-06-10%20103827.png)

![registerd](imgs/Screenshot%202025-06-10%20103844.png)

![Login](imgs/Screenshot%202025-06-10%20103910.png)

![Main Menu](imgs/Screenshot%202025-06-10%20103932.png)

![Play with Bot](imgs/Screenshot%202025-06-10%20104005.png)

![Loss animation](imgs/Screenshot%202025-05-25%20220542.png)

![win animation](imgs/Screenshot%202025-06-10%20104035.png)

![host menu](imgs/Screenshot%202025-06-10%20104118.png)

![host](imgs/Screenshot%202025-06-10%20104128.png)

![Leaderboard](imgs/Screenshot%202025-06-10%20104158.png)

![video](imgs/2025-06-10%2012-00-02.mp4)


