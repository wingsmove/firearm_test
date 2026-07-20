package FunctionClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;

class AutoLoadOpenBoltFirearmsTest {

    private static final int MAG_CAPACITY = 32;

    private Firearm buildOpenBolt(int roundsToLoad) {
        Magazine magazine = new Magazine(MAG_CAPACITY, Caliber._9mm);
        for (int i = 0; i < roundsToLoad; i++) {
            magazine.load1Round(new Ammunition(Caliber._9mm, AmmoType.FMJ));
        }
        Chamber chamber = new Chamber(Caliber._9mm, null);
        Bolt bolt = new Bolt();
        return new AutoLoadOpenBoltFirearms(magazine, chamber, bolt);
    }

    @Test
    @DisplayName("A freshly built gun (bolt closed) cannot fire until charged")
    void cannotFireUntilCharged() {
        Firearm gun = buildOpenBolt(5);

        gun.fire();

        assertEquals(Bolt.BoltState.CLOSED, gun.getBolt().getState());
        assertEquals(Chamber.ChamberState.EMPTY, gun.getChamber().getState(),
                "Nothing should chamber when the bolt is still closed");
        assertEquals(5, gun.getMagazine().getCurrentCapacity(),
                "No round should be consumed before charging");
    }

    @Test
    @DisplayName("Charging opens the bolt, then firing feeds and fires from open bolt")
    void firesFromOpenBoltAfterCharging() {
        Firearm gun = buildOpenBolt(5);

        gun.cycle();
        assertEquals(Bolt.BoltState.OPEN, gun.getBolt().getState(),
                "Cycling an open-bolt gun leaves the bolt held to the rear");
        assertEquals(Chamber.ChamberState.EMPTY, gun.getChamber().getState());

        gun.fire();

        assertEquals(Chamber.ChamberState.FIRED, gun.getChamber().getState());
        assertEquals(Bolt.BoltState.CLOSED, gun.getBolt().getState(),
                "The bolt runs forward to feed and fire");
        assertEquals(4, gun.getMagazine().getCurrentCapacity(),
                "Firing from an open bolt strips one round from the magazine");
        assertEquals(Ammunition.AmmoState.FIRED, gun.getChamber().getAmmunition().getAmmoState());
    }

    @Test
    @DisplayName("Cycling after a shot ejects the case and reopens the bolt")
    void cycleAfterShotEjectsAndReopens() {
        Firearm gun = buildOpenBolt(5);

        gun.cycle();
        gun.fire();
        gun.cycle();

        assertEquals(Bolt.BoltState.OPEN, gun.getBolt().getState());
        assertEquals(Chamber.ChamberState.EMPTY, gun.getChamber().getState());
        assertNull(gun.getChamber().getAmmunition(),
                "The spent case should be extracted when the bolt reopens");
    }

    @Test
    @DisplayName("Each charge+fire consumes exactly one round")
    void eachShotConsumesOneRound() {
        int rounds = 3;
        Firearm gun = buildOpenBolt(rounds);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            for (int shot = 0; shot < rounds; shot++) {
                gun.cycle();
                gun.fire();
            }
        } finally {
            System.setOut(originalOut);
        }

        long bangs = captured.toString().lines().filter(line -> line.contains("Bang!")).count();
        assertEquals(rounds, bangs, "Every round should fire once");
        assertEquals(0, gun.getMagazine().getCurrentCapacity(),
                "All rounds should have been fed and fired");
        assertEquals(Magazine.MagazineState.EMPTY, gun.getMagazine().getState());
    }

    @Test
    @DisplayName("No magazine + empty chamber: firing closes the bolt but nothing happens")
    void noMagazineEmptyChamberClosesBoltOnly() {
        Firearm gun = buildOpenBolt(5);
        gun.removeMagazine();

        gun.cycle();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            gun.fire();
        } finally {
            System.setOut(originalOut);
        }

        assertEquals(Bolt.BoltState.CLOSED, gun.getBolt().getState(),
                "The bolt runs forward and closes on an empty chamber");
        assertEquals(Chamber.ChamberState.EMPTY, gun.getChamber().getState());
        assertEquals(0, captured.toString().lines().filter(line -> line.contains("Bang!")).count(),
                "Nothing should fire without a round");
    }

    @Test
    @DisplayName("No magazine but hand-loaded: firing fires the chambered round")
    void handloadWithoutMagazineFires() {
        Firearm gun = buildOpenBolt(0);
        gun.removeMagazine();
        gun.chamberLoad(new Ammunition(Caliber._9mm, AmmoType.FMJ));

        gun.fire();

        assertEquals(Chamber.ChamberState.FIRED, gun.getChamber().getState(),
                "A hand-loaded round should fire even without a magazine");
        assertEquals(Bolt.BoltState.CLOSED, gun.getBolt().getState());
        assertEquals(Ammunition.AmmoState.FIRED, gun.getChamber().getAmmunition().getAmmoState());

        gun.cycle();
        assertEquals(Bolt.BoltState.OPEN, gun.getBolt().getState());
        assertEquals(Chamber.ChamberState.EMPTY, gun.getChamber().getState(),
                "Cycling afterwards ejects the spent case normally");
    }

    @Test
    @DisplayName("Magazine inserted + hand-loaded: firing causes a double-feed malfunction")
    void handloadWithMagazineCausesMalfunction() {
        Firearm gun = buildOpenBolt(5);
        gun.chamberLoad(new Ammunition(Caliber._9mm, AmmoType.FMJ));

        gun.fire();

        assertEquals(Chamber.ChamberState.MALFUNCTIONED, gun.getChamber().getState(),
                "Feeding onto an already chambered round is a double feed");
        assertTrue(gun.malfunctioned(),
                "The firearm should report a malfunction");
    }

    @Test
    @DisplayName("Full-auto dump: a full magazine fires every round, then runs dry safely")
    void fullMagazineDumpFiresEveryRound() {
        Firearm gun = buildOpenBolt(MAG_CAPACITY);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            for (int shot = 0; shot < MAG_CAPACITY; shot++) {
                gun.cycle();
                gun.fire();
            }
            // keep pulling the trigger after the magazine is empty
            gun.cycle();
            gun.fire();
            gun.cycle();
            gun.fire();
        } finally {
            System.setOut(originalOut);
        }

        long bangs = captured.toString().lines().filter(line -> line.contains("Bang!")).count();
        assertEquals(MAG_CAPACITY, bangs, "Exactly one bang per loaded round, and no more");
        assertEquals(0, gun.getMagazine().getCurrentCapacity());
        assertEquals(Magazine.MagazineState.EMPTY, gun.getMagazine().getState());
    }

    @Test
    @DisplayName("Hand-loading the wrong caliber malfunctions the chamber and blocks firing")
    void caliberMismatchHandloadMalfunctions() {
        Firearm gun = buildOpenBolt(0);
        gun.removeMagazine();

        gun.chamberLoad(new Ammunition(Caliber._45ACP, AmmoType.FMJ));
        assertEquals(Chamber.ChamberState.MALFUNCTIONED, gun.getChamber().getState(),
                "A caliber mismatch must malfunction the chamber");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            gun.fire();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(gun.malfunctioned());
        assertEquals(0, captured.toString().lines().filter(line -> line.contains("Bang!")).count(),
                "A malfunctioned gun must not fire");
    }

    @Test
    @DisplayName("Clearing a double-feed malfunction lets the gun fire again")
    void clearMalfunctionRecoversFromDoubleFeed() {
        Firearm gun = buildOpenBolt(5);
        gun.chamberLoad(new Ammunition(Caliber._9mm, AmmoType.FMJ));
        gun.fire(); // magazine inserted + hand-loaded round => double feed
        assertTrue(gun.malfunctioned());

        gun.clearMalfunction();
        assertFalse(gun.malfunctioned(), "The jam should be cleared");

        gun.cycle();
        gun.fire();

        assertEquals(Chamber.ChamberState.FIRED, gun.getChamber().getState(),
                "After clearing the jam the gun should fire normally again");
    }
}
