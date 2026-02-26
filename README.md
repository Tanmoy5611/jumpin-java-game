# Jumpin' Java Game

A JavaFX-based board game application developed as part of the Applied Computer Science program at Karel de Grote University (KdG), Belgium.

**Jumpin' Java** is a structured board game implementation featuring AI opponents, statistics tracking, sound integration, and a multi-screen JavaFX interface following a layered MVC-inspired architecture.

---

## Features

- Fully playable board game
- Two AI modes:
    - **Simple AI**
    - **Advanced AI** (rule-based decision system)
- Game statistics tracking with PostgreSQL integration
- Sound effects (click, drop, win)
- Custom styling using external CSS
- Multi-screen JavaFX interface:
    - Start Screen
    - Game Screen
    - Statistics Screen
- Clean separation of concerns using MVC principles

---

## Architecture Overview

The project follows a layered structure to ensure modularity and maintainability:

### Presentation Layer
- JavaFX Views
- Screen navigation logic
- UI styling and sound integration

### Domain Model
- Core game mechanics
- Board state management
- Player and piece logic

### AI Layer
- Rule-based move evaluation
- Strategy-driven decision logic

### Data Access Layer
- `StatisticsDAO`
- JDBC integration with PostgreSQL
- Persistent game history storage

This architecture promotes:
- Clean code organization
- Scalability
- Maintainability
- Separation of responsibilities

---

## Technologies Used

- Java 21 (OpenJDK)
- JavaFX 21
- PostgreSQL
- JDBC
- IntelliJ IDEA
- CSS

---

## How To Run

### 1. Requirements

- JDK 21 installed
- JavaFX SDK (download from: https://openjfx.io/)
- PostgreSQL (optional, for statistics feature)

---

### 2. JavaFX Configuration

This project is **not Maven-based**, so JavaFX must be configured manually.

In IntelliJ IDEA:

1. Add the JavaFX SDK as a project library
2. Open **Run → Edit Configurations**
3. Add the following VM options:

```
--module-path /path/to/javafx/lib
--add-modules javafx.controls,javafx.media
```

Replace `/path/to/javafx/lib` with the actual location of your JavaFX SDK.

---

### 3. Main Class

Run:

```
JumpinMain.java
```

This launches the JavaFX application.

---

## Database Setup (Optional - For Statistics)

To enable persistent statistics storage:

1. Install PostgreSQL
2. Create a database (e.g. `jumpin_game`)
3. Update the connection settings in `StatisticsDAO.java`
4. Example connection string:

```java
jdbc:postgresql://localhost:5432/jumpin_game
```

If PostgreSQL is not configured, the game will still run, but statistics saving may not function.

---

## Learning Objectives

This project demonstrates:

- Object-Oriented Programming (OOP)
- MVC architectural principles
- Rule-based AI implementation
- JavaFX UI development
- JDBC database integration
- Clean package structuring
- Multi-layer system design

---

## Author

**Tanmoy Das**  
Bachelor of Applied Computer Science (AI)  
Karel de Grote University - Belgium

---

> This project represents a complete end-to-end Java desktop application combining UI, AI logic, and database persistence within a structured architecture.