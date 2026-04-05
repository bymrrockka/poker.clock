# Poker Clock App

Poker Clock App is a Telegram bot for managing poker games. It helps organizers track game entries, withdrawals,
bounties, prizes, and related operations, then generates payout summaries so everyone can clearly see how the money in a
game was distributed.

The project is split into two layers:

- **`core`** — business logic for games, payouts, calculations, and domain rules
- **`telegram`** — Telegram adapter that exposes the core functionality through bot commands

This separation keeps the domain logic reusable for future interfaces, including a web application.

## Features

- Create and manage poker games
- Register entries and withdrawals
- Track bounties and prizes
- Calculate and publish payout distribution messages
- Create automatic polls for game-related workflows
- Automatically pin and unpin important game messages
- Cancel the last recorded operation with `/cancel`

## Tech Stack

- Java 21
- Kotlin 2.3
- Spring MVC
- PostgreSQL
- Gradle
- Telegram bot integration
- Docker / Docker Compose

## Repository Structure

- `core/` — game and payout logic
- `telegram/` — Telegram bot adapter and REST-related integration
- `.github/workflows/` — build and deployment workflows
- `compose.yaml` — local container setup
- `scripts/` — helper scripts for deployment

## Prerequisites

To work with the project locally, you need:

- Java 21
- Docker
- Docker Compose
- Gradle wrapper support via `./gradlew`

## Configuration

The application uses environment variables for local configuration.

### Environment variables

| Variable       | Purpose                                              |
|----------------|------------------------------------------------------|
| `PROFILES`     | Active Spring profiles (default production,postgres) |
| `BOT_NAME`     | Telegram bot display name                            |
| `BOT_NICKNAME` | Telegram bot nickname                                |
| `BOT_TOKEN`    | Telegram bot token                                   |
| `DB_URL`       | Database JDBC URL                                    |
| `DB_USER`      | Database username                                    |
| `DB_PASSWORD`  | Database password                                    |

## Running Locally

### Build

The project can be built with `./gradlew build`.

### Tests

To run tests locally, Docker must be installed and available on your machine.

The same build command is used for verification: `./gradlew build`.

### Docker Compose

Start PostgreSQL with `docker compose up --build`.

### Run app

Execute `./gradlew :telegram:bootRun`

## Database

The project uses **PostgreSQL** for persistence.

Stored data is limited to operational game records and Telegram nicknames used for identifying participants in games and
commands.

## Bot Commands

### Commands table

| Command            | Description                                     |
|--------------------|-------------------------------------------------|
| `/game`            | Start a new game flow                           |
| `/entry`           | Add player entry to the last started game       |
| `/bounty`          | Record a bounty transaction                     |
| `/withdrawal`      | Record a cash-game withdrawal                   |
| `/pp`              | Store a tournament prize pool                   |
| `/fp`              | Store final tournament places                   |
| `/calculate`       | Calculate game payouts                          |
| `/game_stats`      | Show game statistics                            |
| `/player_stats`    | Show player statistics for a game               |
| `/my_stats`        | Show your chat statistics                       |
| `/help`            | Show help for a command                         |
| `/start`           | Show bot introduction                           |
| `/create_poll`     | Create a scheduled poll                         |
| `/stop_poll`       | Stop a scheduled poll                           |

### User-facing game flows

The main game flows are covered by the test scenarios in the Telegram test package. These flows include:

- creating games
- registering entries
- handling withdrawals
- adding bounties
- handling prize-related payouts
- generating payout summaries
- working with automatic polls used for game creation and coordination

## Cancel Command

The `/cancel` command is intended for administrative use.

It works by replying to a previously created bot message and cancelling the associated operation, such as:

- an entry
- a bounty
- a withdrawal

If the command is not used as a reply, cancellation is rejected.

## Payouts

After a game is processed, the bot creates a payout message describing how funds were distributed among players. This
message is meant to make the final result transparent and easy to verify.

## Polls

The bot supports automatic polls that can be used during game creation or coordination. Polls can be scheduled and later
stopped when no longer needed.

## Development Notes

- Keep poker game rules and payout logic inside `core`
- Keep Telegram-specific behavior inside `telegram`
- The adapter is intentionally separate so other interfaces can reuse the same core logic later
- No user data is stored except Telegram nicknames

## Testing and Local Verification

Recommended local verification is `./gradlew build`.

This is the primary command for compiling the project and running tests.

## Troubleshooting

### Tests fail because Docker is missing

Make sure Docker is installed and running locally. Some tests require it.

### PostgreSQL connection fails

Check that:

- PostgreSQL is running
- connection settings are correct
- the expected local environment variables are set