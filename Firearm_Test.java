import FunctionClass.Ammunition;
import FunctionClass.AutoLoadClosedBoltFirearms;
import FunctionClass.Bolt;
import FunctionClass.Chamber;
import FunctionClass.Firearm;
import FunctionClass.Magazine;
import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;

public class Firearm_Test {

    private static final int MAG_CAPACITY = 17;

    public static void main(String[] args) {
        scenarioFireBeforeChambering();
        scenarioPartialLoad(3, 5);
        scenarioFullLoad(MAG_CAPACITY, 5);
        scenarioOverLoadAttempt();
    }

    private static Firearm buildGlock17(int roundsToLoad) {
        Ammunition round = new Ammunition(Caliber._9mm, AmmoType.FMJ);
        Magazine magazine = new Magazine(MAG_CAPACITY, Caliber._9mm);
        for (int i = 0; i < roundsToLoad; i++) {
            magazine.load1Round(round);
        }
        Chamber chamber = new Chamber(Caliber._9mm, null);
        Bolt bolt = new Bolt();
        Firearm gun = new AutoLoadClosedBoltFirearms(magazine, chamber, bolt);
        System.out.println("Magazine state: " + magazine.getState()
                + " (" + magazine.getCurrentCapacity() + "/" + magazine.getCapacity() + ")");
        return gun;
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(" " + title);
        System.out.println("==================================================");
    }

    private static void scenarioFireBeforeChambering() {
        banner("Scenario 1: fire with empty chamber (no chambering)");
        Firearm gun = buildGlock17(5);
        System.out.println("-- attempting to fire before chambering --");
        gun.fire();
        System.out.println("Chamber state: " + gun.getChamber().getState()
                + ", Bolt state: " + gun.getBolt().getState());
    }

    private static void scenarioPartialLoad(int rounds, int fireAttempts) {
        banner("Scenario 2: partial load " + rounds + " rounds, " + fireAttempts + " fire attempts");
        Firearm gun = buildGlock17(rounds);
        System.out.println("-- chambering first round --");
        gun.cycle();
        for (int shot = 1; shot <= fireAttempts; shot++) {
            System.out.println("-- shot " + shot + " (mag left: "
                    + gun.getMagazine().getCurrentCapacity() + ") --");
            gun.fire();
            gun.cycle();
        }
        System.out.println("Final magazine state: " + gun.getMagazine().getState()
                + ", Chamber state: " + gun.getChamber().getState()
                + ", Bolt state: " + gun.getBolt().getState());
    }

    private static void scenarioFullLoad(int rounds, int fireAttempts) {
        banner("Scenario 3: full load " + rounds + " rounds, " + fireAttempts + " fire attempts");
        Firearm gun = buildGlock17(rounds);
        System.out.println("-- chambering first round --");
        gun.cycle();
        for (int shot = 1; shot <= fireAttempts; shot++) {
            System.out.println("-- shot " + shot + " (mag left: "
                    + gun.getMagazine().getCurrentCapacity() + ") --");
            gun.fire();
            gun.cycle();
        }
        System.out.println("Final magazine state: " + gun.getMagazine().getState()
                + " (" + gun.getMagazine().getCurrentCapacity() + "/"
                + gun.getMagazine().getCapacity() + ")");
    }

    private static void scenarioOverLoadAttempt() {
        banner("Scenario 4: attempt to overload beyond capacity");
        Ammunition round = new Ammunition(Caliber._9mm, AmmoType.FMJ);
        Magazine magazine = new Magazine(MAG_CAPACITY, Caliber._9mm);
        System.out.println("-- loading " + (MAG_CAPACITY + 2) + " rounds into a "
                + MAG_CAPACITY + "-round magazine --");
        for (int i = 0; i < MAG_CAPACITY + 2; i++) {
            magazine.load1Round(round);
        }
        System.out.println("Final magazine state: " + magazine.getState()
                + " (" + magazine.getCurrentCapacity() + "/" + magazine.getCapacity() + ")");
    }
}
