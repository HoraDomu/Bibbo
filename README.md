<div align="center">
  <img src="assets/logo.svg" width="900" alt="Bibbo" />
</div>

<br/>

<div align="center">

A native desktop knowledge graph. Write nodes. Connections emerge.

**[horadomu.github.io/BibboPage](https://horadomu.github.io/BibboPage)** &nbsp;·&nbsp; Private beta &nbsp;·&nbsp; Built by [HoraDomu](https://github.com/HoraDomu)

</div>

---

## Stack

| Layer | Tech |
|---|---|
| Language | Java 21 |
| UI | JavaFX 21 (Canvas-based rendering) |
| Database | SQLite via sqlite-jdbc 3.45.3.0 |
| Full-text search | SQLite FTS5 |
| Physics | Barnes-Hut O(n log n) quad-tree |
| Distribution | jpackage native installer (JVM bundled) |
| Build | Gradle 8.14 + Gradle wrapper |

---

## Build

```bash
# Requires Java 21
./gradlew run          # run locally
./gradlew installDist  # build distribution
```

Windows: use `gradlew.bat` instead of `./gradlew`.

Releasing: push a `v*` tag → GitHub Actions builds `.msi`, `.dmg` (arm64 + intel), `.deb`.

---

## Structure

```
src/main/java/com/bibbo/
  BibboApp.java          # main app — rendering, physics, input, writing mode
  Main.java              # JavaFX entry point
  db/Database.java       # SQLite — CRUD, FTS5 index, edge persistence
  model/Node.java        # node data class
  model/Edge.java        # edge data class
  util/QuadTree.java     # Barnes-Hut quad-tree for physics
  util/Utils.java        # normalize, parseLinks, nodeRadius, dataDir, etc.
src/main/resources/com/bibbo/
  app.css                # writing overlay dark styles
  logo.svg
```

---

## Data

| Platform | Location |
|---|---|
| Windows | `%APPDATA%\Bibbo\bibbo.db` |
| macOS | `~/Library/Application Support/Bibbo/bibbo.db` |
| Linux | `~/.local/share/Bibbo/bibbo.db` |

Single SQLite file. No cloud, no sync, no account.

---

## Keybinds

| Key | Action |
|---|---|
| `Ctrl+N` | New node |
| `[[Title]]` | Link to another node |
| `Esc` | Save and return |
| Click node | Enter local view |
| Click node again | Open writing mode |
| Click edge | Living Connections — see why they're linked |
| Scroll | Zoom |
| `Ctrl+K` | Search |
| `Ctrl+E` | Export to `.md` |
| `Ctrl+I` | Import `.md` folder |

---

## License

You may use Bibbo freely. You may not copy, fork, redistribute, or use the source code in any other project. See [LICENSE](LICENSE).

© 2026 HoraDomu. All rights reserved.
