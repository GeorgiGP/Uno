# Uno 🔴🟢🟡🔵

A deck has a total of 108 cards:
- A total of 19 blue cards (cards from 0 /zero/ to 9, with cards from 1 to 9 appearing twice)
- A total of 19 green cards (cards from 0 /zero/ to 9, with cards from 1 to 9 appearing twice)
- A total of 19 yellow cards (cards from 0 /zero/ to 9, with cards from 1 to 9 appearing twice)
- A total of 19 red cards (cards from 0 /zero/ to 9, with cards from 1 to 9 appearing twice)
- A total of 8 "+2" cards - two of each color
- A total of 8 "reverse" cards - two of each color
- A total of 8 "skip" cards - two of each color
- A total of 4 "wild" cards (choose color)
- A total of 4 "+4" wild cards

### The goal of the game UNO is to play all your cards before the other players.

## MJT Game Rules

### 1. Starting the Game:
- Each player receives 7 random cards.
- The deck is placed face down, called the "draw pile."
- The top card is drawn and placed in a new pile, called the "discard pile."
- A random player starts first, and turns proceed clockwise.

### 2. During the Game:
- On your turn, you must find a matching card from your hand that corresponds to the top card on the "discard pile" by number, color, or symbol (action card).
- Example: If the top card on the discard pile is a "blue 8," the player must place a "blue" card or an "8" of any color.
- The player may also play an action card (see action cards section below), such as a blue "skip."

On your turn, you have two options:
Let’s assume you have the cards a1, a2, ..., an in your hand, forming set A := {a1, a2, ..., an}. From these, only a subset B can be played, where B ⊆ A.
- If |B| > 0, you may play any card from B. The turn then moves to the next player.
- If |B| = 0, you must draw a card. The turn then moves to the next player.

### 3. Action Cards (Jokers)
- "Wild" - The player who plays this card chooses the color for the next move. If drawn at the start of the game, the color is chosen randomly. This card can be played at any time.
- "+4" - The player who plays this card chooses the color for the next move, and the next player draws 4 cards and loses their turn. This card can be played at any time.
- "+2" - The next player draws 2 cards and skips their turn. This card must match the color or follow another "+2."
- "Reverse" - Reverses the order of play. This card must match the color or follow another "reverse."
- "Skip" - The next player skips their turn. This card must match the color or follow another "skip."
- No other special cards exist.

## Condition
The system should consist of two parts:
1. A server that stores ongoing games and their participants.
2. Clients that can participate in at most one game at a time.

## Disconnection Handling
- If a client loses connection to the server while participating in a game, they are not removed from the game.
- When they log in again, they continue playing with the cards they had before disconnection.

## Error Messages
- If the program is used incorrectly, an appropriate error message should be displayed.
- If a system error occurs, the user should only see relevant information. Technical details such as stack traces should be logged in a file.
- Example: Instead of displaying "IO exception occurred: connection reset," a more appropriate message would be "Unable to connect to the server. Try again later or contact the administrator with the logs in <path_to_logs_file>."
- Server errors should be logged, including additional details (such as which user request caused the error) and stack traces.

### Feature to be add in the future:
- "UNO" can only be declared when a player has exactly 1 card left.
- "STOP UNO" can be declared against a player with exactly 1 card who has not declared "UNO." The caught player must draw 2 cards.

## Client Commands

### 1. Registration

```bash
register --username=<username> --password=<password>
```

- Registers a new user with a username and password.
- The user is not automatically logged in.

### 2. Login

```bash
login --username=<username> --password=<password>
```

- Logs in with a username and password.
- A user can only be logged in to one account at a time.

### 3. Logout

```bash
logout
```

- Logs out the user. Only valid if the user is logged in.

### 4. List Games

```bash
list-games --status=<started/ended/available/all>
```

- Displays games with the specified status:
  - `started`: Ongoing games, including player count.
  - `ended`: Completed games, including player count.
  - `available`: Unstarted games with available slots.
  - `all`: Shows all games.

### 5. Create a Game

```bash
create-game --game-id=<game-id> --number-of-players=<number>
```

- Creates a new game with a unique `--game-id`.
- A game can have 2 to `--number-of-players` participants (default: 2).
- The creator is not automatically added and must join separately.

### 6. Join a Game

```bash
join --game-id=<game-id> --display-name=<display-name>
```

- Joins an available game. If full, an error is returned.
- The player can set a display name; otherwise, their registered username is used.

### 7. Start a Game

```bash
start
```

- Starts the game. Only the creator (who has joined) can do this.
- Players receive 7 cards.

### 8. Show Hand

```bash
show-hand
```

- Displays the player's current hand (each card has a unique ID).

### 9. Show Last Played Card

```bash
show-last-card
```

- Displays the top card of the discard pile.

### 10. Accept Effect

```bash
accept-effect
```

- If the last card was `+2`, the player draws 2 cards and loses their turn.
- If the last card was `+4`, the player draws 4 cards and loses their turn.

### 11. Play a Normal Card

```bash
play --card-id=<card-id>
```

- Plays a standard card (not wild `+4` or "Choose Color").

### 12. Play "Choose Color"

```bash
play-choose --card-id=<card-id> --color=<red/green/blue/yellow>
```

- Plays a "Choose Color" card, selecting the next color.

### 13. Play "+4"

```bash
play-plus-four --card-id=<card-id> --color=<color>
```

- Plays a "+4" card, selecting the next color. The next player draws 4 cards and skips their turn.

### 14. Show History

```bash
show-played-cards
```

- Displays played cards in order.

### 15. Leave a Game

```bash
leave
```

- Leaves the game. Remaining cards are shuffled into the draw pile.

### 16. Spectate a Game

```bash
spectate
```

- Allows a player who has finished playing to observe the game.

### 17. Draw a Card

```bash
draw
```

- Draws a card if no playable cards are in hand.

### 18. Game Summary

```bash
summary --game-id=<game-id>
```

- Returns a summary of a completed game.

## End of Game

```
The game ends when N-1 of N players run out of cards.
```

