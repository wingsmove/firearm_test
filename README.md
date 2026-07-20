# firearm_test

A Java **state machine that simulates how a firearm works**. Using an object-oriented design, it models auto-loading firearms — both **closed-bolt** (think Glock 17) and **open-bolt** (think a submachine gun) — through the full cycle of loading, chambering, firing, and cycling, including common malfunctions.
More firearm types and customizable firearms may be added in the future.

## Features

- **Two firing systems**: `AutoLoadClosedBoltFirearms` (fires from a closed bolt) and `AutoLoadOpenBoltFirearms` (fires from an open bolt), both extending the shared `Firearm` base.
- **Ammunition modeling**: every round records its caliber, ammo type, and firing state (`UNFIRED` / `FIRED`), so a spent round is never fired twice.
- **State-machine driven**: the magazine, chamber, and bolt each maintain their own state and transition.
- **Coordinated bolt operations**: `openBolt()` retracts the bolt and extracts the chambered round (an open bolt always leaves the chamber empty, unless malfunctioned); `closeBolt()` closes the bolt and feeds a round when a magazine is inserted.
- **Magazine management**: `insertMagazine()` / `removeMagazine()` track whether a magazine is present (`magInserted`); removing a magazine sets it to `null`.
- **Manual chambering**: `chamberLoad()` hand-loads a single round directly into the chamber (no magazine required).
- **Malfunction handling**: caliber mismatches and double feeds set a component to `MALFUNCTIONED`; `clearMalfunction()` resets each component out of its failure state.
- **Extensible architecture**: `Firearm` is an abstract base class, making it easy to add more firearm types in the future (bolt-action, etc.).

## Project Structure

A standard Maven layout:

```
firearm_test/
├── pom.xml                              # Maven build (JUnit 5, compiler, surefire)
├── mvnw / mvnw.cmd                      # Maven Wrapper (no global Maven needed)
├── .mvn/wrapper/                        # Wrapper configuration
└── src/
    ├── main/java/
    │   ├── Firearm_Test.java            # Entry point: small runnable demo
    │   └── FunctionClass/
    │       ├── Firearm.java                     # Abstract base: magazine/chamber/bolt + shared operations
    │       ├── AutoLoadClosedBoltFirearms.java  # Fires from a closed bolt
    │       ├── AutoLoadOpenBoltFirearms.java    # Fires from an open bolt
    │       ├── Magazine.java                    # Magazine: load/unload, capacity & state management
    │       ├── Chamber.java                     # Chamber: chambering/firing/ejection & state management
    │       ├── Bolt.java                        # Bolt: open/closed/malfunction state
    │       ├── Ammunition.java                  # Ammunition: caliber + ammo type + fired state
    │       └── Enums/
    │           ├── Caliber.java                 # Caliber enum (9mm, 45ACP, 762x51, etc.)
    │           └── AmmoType.java                # Ammo type enum (HP, FMJ, AP)
    └── test/java/
        └── FunctionClass/
            ├── FirearmTest.java                 # JUnit 5 tests for the closed-bolt firearm
            └── AutoLoadOpenBoltFirearmsTest.java # JUnit 5 tests for the open-bolt firearm
```

## Component States

| Component | States |
| --- | --- |
| Magazine | `EMPTY` / `LOADED` / `FULL` / `MALFUNCTIONED` |
| Chamber  | `EMPTY` / `LOADED` / `FIRED` / `MALFUNCTIONED` |
| Bolt     | `OPEN` / `CLOSED` / `MALFUNCTIONED` |
| Ammunition | `UNFIRED` / `FIRED` |

## How It Works

Load `Ammunition` into a `Magazine` whose caliber matches the round, then put the magazine in the firearm (passing it to the constructor counts as inserted, or call `insertMagazine()`). From there the two firing systems differ:

**Closed bolt** (`AutoLoadClosedBoltFirearms`) — the bolt rests closed on a chambered round.

1. `cycle()` to chamber the first round (`openBolt()` extracts anything in the chamber → `closeBolt()` feeds a fresh round and closes).
2. `fire()` releases the shot when the **bolt is closed**, the **chamber is loaded**, and there is **no malfunction**; `Chamber.fire()` then checks the round is `UNFIRED` and marks it `FIRED`.
3. Repeat `fire()` + `cycle()`; once the magazine runs dry, the bolt holds open.

**Open bolt** (`AutoLoadOpenBoltFirearms`) — the bolt is held to the rear and ready state is *open*.

1. `cycle()` to charge (hold the bolt open and clear the chamber).
2. `fire()` runs the bolt forward: `closeBolt()` strips and chambers a round from the magazine, then `Chamber.fire()` fires it. Edge cases: with no magazine and an empty chamber the bolt just closes on nothing;
3. `cycle()` again to eject the case and reopen the bolt for the next shot.

### Key operations (`Firearm`)

| Method | Behavior |
| --- | --- |
| `fire()` | *(abstract)* Fires according to the firing system (closed-bolt vs open-bolt). |
| `cycle()` | *(abstract)* Runs the action: chamber/eject as appropriate for the type. |
| `openBolt()` | Opens the bolt and empties the chamber (extracts the round). |
| `closeBolt()` | Closes the bolt; feeds from the magazine if one is inserted. |
| `insertMagazine(mag)` / `removeMagazine()` | Insert/remove a magazine; toggles `magInserted`. Removing a magazine sets it to `null`. |
| `chamberLoad(round)` | Hand-loads a single round into the chamber without a magazine. |
| `clearMalfunction()` | Clears malfunctions on the magazine, chamber, and bolt. |

## Build & Run

Built with [Maven](https://maven.apache.org/). The bundled **Maven Wrapper** (`mvnw` / `mvnw.cmd`) downloads the correct Maven version automatically, so only a JDK 17+ is required. Use `mvn` directly if you already have Maven installed.

```bash
# Compile and package (Windows: use .\mvnw.cmd)
./mvnw package

# Run the demo
./mvnw exec:java
```

## Demo

The `main` method in `Firearm_Test.java` runs a short demonstration: it builds a Glock 17 (9mm, 17-round magazine), loads 5 rounds, chambers the first one, and fires three shots, printing the running state to the console.

## Testing

The behavioral checks live under `src/test/java/FunctionClass/` as **JUnit 5** tests, split by firing system:

- `FirearmTest.java` — the closed-bolt firearm
- `AutoLoadOpenBoltFirearmsTest.java` — the open-bolt firearm

Run the whole suite with:

```bash
./mvnw test
```

### Closed-bolt coverage (`FirearmTest`)

- **Firing & cycling** — fire on empty chamber is blocked; partial load locks the bolt back when empty; full load consumes exactly the rounds fired; overloading is capped at capacity.
- **Ammunition state** — a round goes `UNFIRED` → `FIRED`; distinct rounds each fire exactly once (regression guard against reusing one `Ammunition` instance).
- **Bolt behavior** — `openBolt()` empties the chamber; firing with the bolt open does nothing.
- **Magazine handling** — the constructor counts a supplied magazine as inserted; after `removeMagazine()` cycling feeds nothing; re-inserting restores feeding; `chamberLoad()` hand-loads and fires a single round.
- **Extreme cases** — a wrong-caliber load jams the magazine and recovers after `clearMalfunction()`; repeated dry-firing on an empty gun stays safely locked open.

### Open-bolt coverage (`AutoLoadOpenBoltFirearmsTest`)

- **Charging & firing** — cannot fire until charged (bolt open); charging then firing feeds and fires from the open bolt; cycling after a shot ejects the case and reopens; each charge+fire consumes one round.
- **No-magazine edge cases** — empty chamber + no magazine just closes the bolt; hand-loaded + no magazine fires; hand-loaded **and** a magazine inserted causes a double-feed malfunction.
- **Extreme cases** — a full-magazine dump fires every round then runs dry safely; a wrong-caliber hand-load malfunctions the chamber; clearing a double-feed jam restores normal firing.

> Note: the tests live in package `FunctionClass` so they can assert on the package-private state enums (`MagazineState`, `ChamberState`, `BoltState`, `AmmoState`).

## Roadmap

- More firearm types (bolt-action rifles, revolvers, etc.).
- Richer malfunction types and clearing procedures.
- More detailed simulation of internal operation.
