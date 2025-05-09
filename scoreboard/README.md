## Live Score Board

### Overview

This application allows you to track live football matches. It supports the following main functions:
1. starting match
2. updating a match
3. finishing a match
4. getting summary

### How does it work

A [.json](scoreboard.json) acts as the application's memory. Every match start, update, or finish operation is persisted to this file, keeping the state consistent.

### How to use

#### Starting a match

- command: `start TeamA TeamB`
- default score values is `0-0` for both team
- default timestamp: `LocalDateTime.now()` (via `TimeProvider` abstraction)

#### Updating a match
- command: `update TeamA TeamB 2 3`
- the scores must be provided as arguments 
- timestamp is automatically set to the current time 
- error handling: If the match has not been started (i.e., it's missing in [.json](scoreboard.json)), an error is thrown.
### Finshing a match
- command: `finish TeamA TeamB`
- before finishing a match, it's expected that you’ve updated the score 
- timestamp is automatically added at update time 
- error handling: If the match hasn't started, an error is thrown.
### Displaying current board
- command: `summary`
- to overview the current status [.json](scoreboard.json) file is read.
- example output:

```
Current Status at 2025-05-09 09:22:02 is:
Mexico 0 - 5 Canada
Spain 10 - 2 Brazil
```

### Development

- language: Scala 3
- build Tool: Gradle 
- testing Frameworks: JUnit 5 and Mockito 
- JSON Serialization: Jackson (with Scala module)


