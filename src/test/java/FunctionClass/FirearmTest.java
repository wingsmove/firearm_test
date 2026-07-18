package FunctionClass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;

class FirearmTest {

    private static final int MAG_CAPACITY = 17;

    private Firearm buildGlock17(int roundsToLoad) {
        Magazine magazine = new Magazine(MAG_CAPACITY, Caliber._9mm);
        for (int i = 0; i < roundsToLoad; i++) {
            magazine.load1Round(new Ammunition(Caliber._9mm, AmmoType.FMJ));
        }
        Chamber chamber = new Chamber(Caliber._9mm, null);
        Bolt bolt = new Bolt();
        return new AutoLoadClosedBoltFirearms(magazine, chamber, bolt);
    }

    @Test
    @DisplayName("Firing before chambering does nothing")
    void fireBeforeChambering() {
        Firearm gun = buildGlock17(5);

        gun.fire();

        assertEquals(Chamber.ChamberState.EMPTY, gun.getChamber().getState(),
                "Chamber must stay empty when never chambered");
        assertEquals(Bolt.BoltState.CLOSED, gun.getBolt().getState());
        assertEquals(5, gun.getMagazine().getCurrentCapacity(),
                "No round should be consumed when firing on an empty chamber");
    }

    @Test
    @DisplayName("Partial load: 3 rounds, bolt locks back after the mag runs dry")
    void partialLoadLocksBoltBack() {
        Firearm gun = buildGlock17(3);

        gun.cycle();
        for (int shot = 0; shot < 5; shot++) {
            gun.fire();
            gun.cycle();
        }

        assertEquals(Magazine.MagazineState.EMPTY, gun.getMagazine().getState());
        assertEquals(0, gun.getMagazine().getCurrentCapacity());
        assertEquals(Chamber.ChamberState.EMPTY, gun.getChamber().getState());
        assertEquals(Bolt.BoltState.OPEN, gun.getBolt().getState(),
                "Bolt should hold open once the magazine is empty");
    }

    @Test
    @DisplayName("Full load: 17 rounds, 5 shots consume exactly 5 rounds")
    void fullLoadConsumesRounds() {
        Firearm gun = buildGlock17(MAG_CAPACITY);

        gun.cycle();
        for (int shot = 0; shot < 5; shot++) {
            gun.fire();
            gun.cycle();
        }

        assertEquals(Magazine.MagazineState.LOADED, gun.getMagazine().getState());
        assertEquals(MAG_CAPACITY - 1 - 5, gun.getMagazine().getCurrentCapacity(),
                "One round chambered plus five fired should be gone");
        assertEquals(Chamber.ChamberState.LOADED, gun.getChamber().getState());
        assertEquals(Bolt.BoltState.CLOSED, gun.getBolt().getState());
    }

    @Test
    @DisplayName("Overloading is capped at magazine capacity")
    void overloadIsCapped() {
        Magazine magazine = new Magazine(MAG_CAPACITY, Caliber._9mm);

        for (int i = 0; i < MAG_CAPACITY + 2; i++) {
            magazine.load1Round(new Ammunition(Caliber._9mm, AmmoType.FMJ));
        }

        assertEquals(Magazine.MagazineState.FULL, magazine.getState());
        assertEquals(MAG_CAPACITY, magazine.getCurrentCapacity(),
                "Magazine must never exceed its capacity");
    }

    @Test
    @DisplayName("A round is marked FIRED after firing")
    void firedRoundIsMarkedFired() {
        Firearm gun = buildGlock17(2);

        gun.cycle();
        assertEquals(Ammunition.AmmoState.UNFIRED, gun.getChamber().getAmmunition().getAmmoState(),
                "A freshly chambered round should be unfired");

        gun.fire();
        assertEquals(Ammunition.AmmoState.FIRED, gun.getChamber().getAmmunition().getAmmoState(),
                "The chambered round should be marked fired after firing");
    }

    @Test
    @DisplayName("Each distinct round fires exactly once")
    void eachRoundFiresIndependently() {
        int rounds = 3;
        Firearm gun = buildGlock17(rounds);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            gun.cycle();
            for (int shot = 0; shot < rounds; shot++) {
                gun.fire();
                gun.cycle();
            }
        } finally {
            System.setOut(originalOut);
        }

        long bangs = captured.toString().lines().filter(line -> line.contains("Bang!")).count();
        assertEquals(rounds, bangs,
                "Every distinct round should fire once (regression guard for shared ammo instances)");
    }
}
