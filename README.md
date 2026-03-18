# devtask 🖥️

A developer-first command-line task manager built with **Spring Boot** and **Spring Shell**. No browser, no Notion, no switching context —> just type `devtask` and manage your work right inside the terminal where you already live.

Tasks persist locally in an embedded H2 database. Output is color-coded with priority badges, overdue alerts, and a live progress dashboard.

```
devtask> add --title "Fix auth bug" --priority HIGH --tag backend --due 2025-06-01
✔ Task #1 added: "Fix auth bug"

devtask> list

  ID   TITLE                                PRIORITY   TAG          DUE          STATUS
  ──────────────────────────────────────────────────────────────────────────────────────
  1    Fix auth bug                         HIGH       backend      2025-06-01   ○ todo
  2    Write unit tests                     MEDIUM     -            -            ○ todo
  3    Update README                        LOW        docs         -            ✔ done
  ──────────────────────────────────────────────────────────────────────────────────────

devtask> stats

  ══════════ DEVTASK STATS ══════════
  Total tasks   : 3
  Completed     : 1
  Pending       : 2
  Progress      : 33.3%

  [████████░░░░░░░░░░░░] 33%
  ════════════════════════════════════
```

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Install as a Shell Command](#install-as-a-shell-command)
- [Commands Reference](#commands-reference)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Data Storage](#data-storage)
- [Development Guide](#development-guide)
- [Roadmap](#roadmap)
- [License](#license)

---

## Features

- **Add tasks** with title, priority, tag, and due date
- **Color-coded table output** — HIGH in red, MEDIUM in yellow, LOW in green, completed tasks dimmed
- **Overdue detection** — past-due tasks automatically highlighted in red
- **Filter views** — list by tag, priority, or show all including completed
- **Stats dashboard** — total, completed, pending count with ASCII progress bar
- **Persistent storage** — H2 file-based database survives restarts
- **Zero config** — no database server, no cloud account, no internet needed
- **Custom prompt** — colored `devtask>` shell prompt
- **Works on Mac, Linux, and Windows**

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Framework | Spring Boot | 3.2.5 |
| CLI | Spring Shell | 3.2.4 |
| Database | H2 Embedded (file mode) | Runtime |
| ORM | Spring Data JPA / Hibernate | - |
| Terminal Colors | Jansi | 2.4.1 |
| Boilerplate Reduction | Lombok | 1.18.32 |
| Build Tool | Maven | 3.8+ |
| Language | Java | 17+ |

---

## Prerequisites

Make sure these are installed before you begin.

**Java 17+**
```bash
java -version
# Required: openjdk 17.x.x or higher
```

**Maven 3.8+**
```bash
mvn -version
```

**Install Java if missing:**
- macOS: `brew install openjdk@17`
- Ubuntu/Debian: `sudo apt install openjdk-17-jdk`
- Windows: Download from [adoptium.net](https://adoptium.net)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/sattupp/devtask.git
cd devtask
```

### 2. Build the project

```bash
mvn clean package -DskipTests
```

This compiles the code and produces a fat JAR at `target/devtask-1.0.0.jar` with all dependencies bundled inside.

### 3. Run

```bash
java -jar target/devtask-1.0.0.jar
```

The interactive shell launches:

```
devtask>
```

Type `help` to see all available commands.

---

## Install as a Shell Command

Run `devtask` from anywhere without typing `java -jar ...` every time.

### macOS / Linux

```bash
# 1. Create a permanent location for the JAR
mkdir -p ~/.local/bin

# 2. Copy the built JAR there
cp target/devtask-1.0.0.jar ~/.local/bin/devtask.jar

# 3. Create a wrapper script
cat > ~/.local/bin/devtask << 'EOF'
#!/bin/bash
java -jar ~/.local/bin/devtask.jar "$@"
EOF

# 4. Make it executable
chmod +x ~/.local/bin/devtask

# 5. Add ~/.local/bin to PATH (add this line to your ~/.zshrc or ~/.bashrc)
export PATH="$HOME/.local/bin:$PATH"

# 6. Reload shell config
source ~/.zshrc   # or: source ~/.bashrc
```

Now run from anywhere:

```bash
devtask
```

### Windows (PowerShell)

```powershell
# 1. Create a tools folder
mkdir C:\tools

# 2. Copy the JAR
copy target\devtask-1.0.0.jar C:\tools\devtask.jar

# 3. Create a wrapper batch file
echo @echo off > C:\tools\devtask.bat
echo java -jar C:\tools\devtask.jar %* >> C:\tools\devtask.bat

# 4. Add C:\tools to your system PATH
# Go to: Settings → System → About → Advanced System Settings
# → Environment Variables → Edit "Path" → Add "C:\tools"
```

### Updating after code changes

```bash
mvn clean package -DskipTests
cp target/devtask-1.0.0.jar ~/.local/bin/devtask.jar
echo "✔ devtask updated!"
```

---

## Commands Reference

### `add` — Add a new task

```bash
add --title "Task name" [--priority MEDIUM] [--tag tagname] [--due yyyy-MM-dd]
```

| Option | Short | Default | Description |
|---|---|---|---|
| `--title` | `-t` | required | Task title |
| `--priority` | `-p` | `MEDIUM` | Priority level: `LOW`, `MEDIUM`, or `HIGH` |
| `--tag` | | none | Group label (e.g. `backend`, `docs`, `devops`) |
| `--due` | `-d` | none | Due date in `yyyy-MM-dd` format |

**Examples:**
```bash
add --title "Deploy to staging" --priority HIGH --tag devops --due 2025-06-15
add -t "Review PR #42" -p MEDIUM --tag backend
add -t "Update changelog" -p LOW
```

---

### `list` — List tasks

```bash
list [--all] [--tag tagname] [--priority HIGH]
```

| Option | Short | Default | Description |
|---|---|---|---|
| `--all` | `-a` | false | Show all tasks including completed |
| `--tag` | | none | Filter by tag |
| `--priority` | `-p` | none | Filter by priority level |

**Examples:**
```bash
list                       # pending tasks only (default)
list --all                 # all tasks including completed
list --tag backend         # only tasks tagged "backend"
list --priority HIGH       # only HIGH priority tasks
list -a --tag devops       # all devops tasks including done
```

---

### `done` — Mark a task as complete

```bash
done --id 3
done -i 3
```

---

### `delete` — Delete a task permanently

```bash
delete --id 2
delete -i 2
```

---

### `stats` — View the stats dashboard

```bash
stats
```

Shows total tasks, completed, pending, and an ASCII progress bar.

---

### `clear-done` — Remove all completed tasks

```bash
clear-done
```

---

### `help` — List all commands

```bash
help              # lists everything
help add          # detailed help for a specific command
```

---

## Project Structure

```
devtask/
├── src/
│   ├── main/
│   │   ├── java/com/devtask/
│   │   │   ├── DevtaskApplication.java          # Entry point, installs Jansi for colors
│   │   │   ├── config/
│   │   │   │   └── ShellPromptProvider.java      # Custom "devtask> " colored prompt
│   │   │   ├── model/
│   │   │   │   └── Task.java                     # JPA entity, Priority enum
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java           # Spring Data interface, auto-generated SQL
│   │   │   ├── service/
│   │   │   │   └── TaskService.java              # Business logic layer
│   │   │   ├── shell/
│   │   │   │   └── TaskCommands.java             # All CLI commands (@ShellComponent)
│   │   │   └── util/
│   │   │       └── ColorPrinter.java             # Jansi-powered colored terminal output
│   │   └── resources/
│   │       └── application.properties            # H2 config, logging, shell settings
│   └── test/
│       └── java/com/devtask/
│           └── DevtaskApplicationTests.java
├── .gitignore
├── .gitattributes
├── pom.xml
└── README.md
```

### Architecture

```
CLI Input  →  TaskCommands  →  TaskService  →  TaskRepository  →  H2 Database
                                                                        ↓
Terminal   ←  ColorPrinter  ←─────────────────────────────────── Task data
```

## Data Storage

Tasks are stored in a local H2 database file at:

```
~/.devtask/data.mv.db
```

This file is created automatically on first run. It is listed in `.gitignore` and is never committed to git.

**Reset all data:**
```bash
rm ~/.devtask/data.mv.db
```

---

## Development Guide

### Running in IntelliJ IDEA

1. Open project: `File → Open → select the devtask folder`
2. Wait for Maven to finish syncing (progress bar at the bottom)
3. Enable Lombok: `Settings → Build → Compiler → Annotation Processors → Enable annotation processing`
4. Run `DevtaskApplication.java` using the green ▶ button

### Daily Git workflow

```bash
git add .
git commit -m "feat: add search command"
git push
```

### Run tests

```bash
mvn test
```

### Build without running tests

```bash
mvn clean package -DskipTests
```

---

## Roadmap

- [ ] `search` — keyword search across task titles
- [ ] `today` — show only tasks due today or already overdue
- [ ] `edit` — update title, priority, or due date on an existing task
- [ ] `export` — export all tasks to a CSV file
- [ ] `report` — generate a weekly summary as a Markdown file
- [ ] Recurring tasks support
- [ ] REST API layer running alongside the CLI

---

## License

MIT License — free to use, modify, and distribute.

---

## Author

Built by [Satya](https://github.com/sattupp) as a hands-on personal project covering Spring Boot 3.x, Spring Shell, Spring Data JPA, H2 embedded database, Jansi terminal colors, Lombok, Maven, and Git —> deployed as a native shell command.
