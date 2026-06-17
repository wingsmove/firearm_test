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

```
firearm_test/
├── Firearm_Test.java                    # Entry point with 4 demo scenarios
└── FunctionClass/
    ├── Firearm.java                     # Abstract base: holds magazine/chamber/bolt, defines fire/cycle
    ├── AutoLoadClosedBoltFirearms.java  # Concrete closed-bolt auto-loading implementation
    ├── Magazine.java                    # Magazine: load/unload, capacity & state management
    ├── Chamber.java                     # Chamber: chambering/firing/ejection & state management
    ├── Bolt.java                        # Bolt: open/closed/malfunction state
    ├── Ammunition.java                  # Ammunition: caliber + ammo type
    └── Enums/
        ├── Caliber.java                 # Caliber enum (9mm, 45ACP, 762x51, etc.)
        └── AmmoType.java                # Ammo type enum (HP, FMJ, AP)
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

Requires JDK 8 or newer. From the project root:

```bash
# Compile
javac -d out Firearm_Test.java FunctionClass/*.java FunctionClass/Enums/*.java

# Run
java -cp out Firearm_Test
```

## Demo Scenarios

The `main` method in `Firearm_Test.java` runs four scenarios:

It simulates an Glock 17 Pistol with a 17-round magazine.
1. **Fire on empty chamber** – pulling the trigger without chambering a round; firing is blocked.
2. **Partial load** – load 3 rounds, attempt 5 shots, and watch the bolt lock back when empty.
3. **Full load** – fill all 17 rounds and fire continuously.
4. **Overload attempt** – try to stuff 19 rounds into a 17-round magazine to verify overflow protection.

## Roadmap

- More firearm types (open-bolt, bolt-action rifles, etc.).
- Richer malfunction types and clearing procedures.
- More detailed simulation of internal operation.