# firearm_test

A Java **state machine that simulates how a firearm works**. Using an object-oriented design, it currently models a closed-bolt, auto-loading pistol (think Glock 17) through the full cycle of loading, chambering, firing, and cycling.
Other Firearms to be added in the future. Customizable firearm may be added in the future.

## Features

- **Ammunition modeling**: every round records its caliber and ammo type.
- **State-machine driven**: the magazine, chamber, and bolt each maintain their own state and transition.
- **Full operating cycle**: supports `fire()` and `cycle()` for a closed-bolt auto-loading firearm.
- **Malfunction clearing**: `clearMalfunction()` resets each component out of its failure state.
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
            └── FirearmTest.java         # JUnit 5 tests for the operating cycle
```

## Component States

| Component | States |
| --- | --- |
| Magazine | `EMPTY` / `LOADED` / `FULL` / `MALFUNCTIONED` |
| Chamber  | `EMPTY` / `LOADED` / `FIRED` / `MALFUNCTIONED` |
| Bolt     | `OPEN` / `CLOSED` / `MALFUNCTIONED` |

## How It Works

1. Load `Ammunition` into a `Magazine` whose caliber matches the round.
2. Call `cycle()`: bolt opens → old round is cleared from the chamber → a fresh round is fed from the magazine → bolt closes.
3. Call `fire()`: the shot is only released (`Bang!`) when the **bolt is closed**, the **chamber is loaded**, and there is **no malfunction**.
4. Repeat `fire()` + `cycle()` for sustained fire; once the magazine runs dry, the bolt holds open.

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

The behavioral checks live in `src/test/java/FunctionClass/FirearmTest.java` as **JUnit 5** tests (no longer hard-coded in `main`). Run them with:

```bash
./mvnw test
```

The four covered scenarios (simulating a Glock 17 with a 17-round magazine):

1. **Fire on empty chamber** – pulling the trigger without chambering a round; firing is blocked and no round is consumed.
2. **Partial load** – load 3 rounds, fire 5 times, and verify the bolt locks back once the magazine runs dry.
3. **Full load** – load 17 rounds, fire 5 times, and verify exactly the chambered round plus 5 fired rounds are gone.
4. **Overload attempt** – stuff 19 rounds into a 17-round magazine and verify capacity is never exceeded.

> Note: the tests live in package `FunctionClass` so they can assert on the package-private state enums (`MagazineState`, `ChamberState`, `BoltState`).

## Roadmap

- More firearm types (open-bolt, bolt-action rifles, etc.).
- Richer malfunction types and clearing procedures.
- More detailed simulation of internal operation.