# firearm_test

A Java **state machine that simulates how a firearm works**. Using an object-oriented design, it currently models a closed-bolt, auto-loading pistol (think Glock 17) through the full cycle of loading, chambering, firing, and cycling.
Other Firearms to be added in the future. Customizable firearm may be added in the future.

## Features

- **Ammunition modeling**: every round records its caliber, ammo type, and firing state (`UNFIRED` / `FIRED`), so a spent round is never fired twice.
- **State-machine driven**: the magazine, chamber, and bolt each maintain their own state and transition.
- **Full operating cycle**: supports `fire()` and `cycle()` for a closed-bolt auto-loading firearm.
- **Coordinated bolt operations**: `openBolt()` retracts the bolt and extracts the chambered round (an open bolt always leaves the chamber empty, unless malfunctioned); `closeBolt()` closes the bolt and feeds a round when a magazine is inserted.
- **Magazine management**: `insertMagazine()` / `removeMagazine()` track whether a magazine is present (`magInserted`); with no magazine the bolt still closes but nothing is chambered.
- **Manual chambering**: `chamberLoad()` hand-loads a single round directly into the chamber (no magazine required).
- **Malfunction handling**: caliber mismatches, double feeds, etc. set a component to `MALFUNCTIONED`; `clearMalfunction()` resets each component out of its failure state.
- **Extensible architecture**: `Firearm` is an abstract base class, making it easy to add more firearm types in the future (open-bolt, bolt-action, etc.).

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
    │       ├── Firearm.java                     # Abstract base: holds magazine/chamber/bolt, defines fire/cycle
    │       ├── AutoLoadClosedBoltFirearms.java  # Concrete closed-bolt auto-loading implementation
    │       ├── Magazine.java                    # Magazine: load/unload, capacity & state management
    │       ├── Chamber.java                     # Chamber: chambering/firing/ejection & state management
    │       ├── Bolt.java                        # Bolt: open/closed/malfunction state
    │       ├── Ammunition.java                  # Ammunition: caliber + ammo type
    │       └── Enums/
    │           ├── Caliber.java                 # Caliber enum (9mm, 45ACP, 762x51, etc.)
    │           └── AmmoType.java                # Ammo type enum (HP, FMJ, AP)
    └── test/java/
        └── FunctionClass/
            └── FirearmTest.java         # JUnit 5 tests (operating cycle, ammo state, magazine handling)
```

## Component States

| Component | States |
| --- | --- |
| Magazine | `EMPTY` / `LOADED` / `FULL` / `MALFUNCTIONED` |
| Chamber  | `EMPTY` / `LOADED` / `FIRED` / `MALFUNCTIONED` |
| Bolt     | `OPEN` / `CLOSED` / `MALFUNCTIONED` |
| Ammunition | `UNFIRED` / `FIRED` |

## How It Works

1. Load `Ammunition` into a `Magazine` whose caliber matches the round, then put the magazine in the firearm (passing it to the constructor counts as inserted, or call `insertMagazine()`).
2. Call `cycle()`: `openBolt()` retracts the bolt and extracts any chambered round → `closeBolt()` closes the bolt and, if a magazine is inserted, feeds a fresh round into the chamber.
3. Call `fire()`: the shot is only released (`Bang!`) when the **bolt is closed**, the **chamber is loaded**, there is **no malfunction**, and the **round is unfired**. The fired round is then marked `FIRED`.
4. Repeat `fire()` + `cycle()` for sustained fire; once the magazine runs dry, the bolt holds open.

### Key operations (`Firearm`)

| Method | Behavior |
| --- | --- |
| `fire()` | Fires if in battery (bolt closed, chamber loaded, unfired round). |
| `cycle()` | Ejects the spent round and chambers the next one from the magazine. |
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

The behavioral checks live in `src/test/java/FunctionClass/FirearmTest.java` as **JUnit 5** tests. Run them with:

```bash
./mvnw test
```

The covered scenarios (simulating a Glock 17 with a 17-round magazine):

**Firing & cycling**

- **Fire on empty chamber** – pulling the trigger without chambering a round is blocked and no round is consumed.
- **Partial load** – load 3 rounds, fire 5 times, and verify the bolt locks back once the magazine runs dry.
- **Full load** – load 17 rounds, fire 5 times, and verify exactly the chambered round plus 5 fired rounds are gone.
- **Overload attempt** – stuff 19 rounds into a 17-round magazine and verify capacity is never exceeded.

**Ammunition state**

- **Round marked fired** – a chambered round is `UNFIRED`, then `FIRED` after firing.
- **Each round fires once** – distinct rounds each fire exactly once (regression guard against reusing a single `Ammunition` instance).

**Bolt behavior**

- **Opening the bolt empties the chamber** – `openBolt()` extracts the chambered round.
- **Firing with the bolt open does nothing** – an out-of-battery bolt cannot fire.

**Magazine handling**

- **Constructor inserts the magazine** – a gun built with a magazine feeds on cycle.
- **No magazine → no feed** – after `removeMagazine()`, cycling closes the bolt but chambers nothing and consumes no rounds.
- **Re-inserting feeds again** – `insertMagazine()` restores feeding.
- **Hand-loading** – `chamberLoad()` chambers and fires a single round with no magazine present.

> Note: the tests live in package `FunctionClass` so they can assert on the package-private state enums (`MagazineState`, `ChamberState`, `BoltState`, `AmmoState`).

## Roadmap

- More firearm types (open-bolt, bolt-action rifles, etc.).
- Richer malfunction types and clearing procedures.
- More detailed simulation of internal operation.
