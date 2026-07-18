import FunctionClass.Ammunition;
import FunctionClass.AutoLoadClosedBoltFirearms;
import FunctionClass.Bolt;
import FunctionClass.Chamber;
import FunctionClass.Firearm;
import FunctionClass.Magazine;
import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;

/**
 * Small runnable demo of the firearm state machine.
 * The behavioral checks live in {@code FunctionClass.FirearmTest} (JUnit 5).
 */
public class Firearm_Test {

    private static final int MAG_CAPACITY = 17;

    public static void main(String[] args) {
        Magazine magazine = new Magazine(MAG_CAPACITY, Caliber._9mm);
        for (int i = 0; i < 5; i++) {
            magazine.load1Round(new Ammunition(Caliber._9mm, AmmoType.FMJ));
        }
        Firearm gun = new AutoLoadClosedBoltFirearms(magazine, new Chamber(Caliber._9mm, null), new Bolt());

        System.out.println("-- chambering first round --");
        gun.cycle();
        for (int shot = 1; shot <= 3; shot++) {
            System.out.println("-- shot " + shot + " (mag left: "
                    + gun.getMagazine().getCurrentCapacity() + ") --");
            gun.fire();
            gun.cycle();
        }

        System.out.println("Final magazine state: " + gun.getMagazine().getState()
                + " (" + gun.getMagazine().getCurrentCapacity() + "/" + gun.getMagazine().getCapacity() + ")"
                + ", Chamber: " + gun.getChamber().getState()
                + ", Bolt: " + gun.getBolt().getState());
    }
}
