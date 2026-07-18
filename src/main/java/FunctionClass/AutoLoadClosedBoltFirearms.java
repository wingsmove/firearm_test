package FunctionClass;

public class AutoLoadClosedBoltFirearms extends Firearm {

    public AutoLoadClosedBoltFirearms(Magazine magazine, Chamber chamber, Bolt bolt) {
        super(magazine, chamber, bolt);
    }

    @Override
    public void fire() {
        if (!malfunctioned()
                && bolt.getState() == Bolt.BoltState.CLOSED
                && chamber.getState() == Chamber.ChamberState.LOADED
                && chamber.getAmmunition().getAmmoState() == Ammunition.AmmoState.UNFIRED) {
            System.out.println("Firing firearm...");
            chamber.getAmmunition().setAmmoState(Ammunition.AmmoState.FIRED);
            chamber.fire();
        }
    }

    @Override
    public void cycle() {
        if (!malfunctioned()) {
            System.out.println("Cycling firearm...");
            bolt.open();
            chamber.unload();
            System.out.println("Chamber unloaded...");
            if (magazine.getState() != Magazine.MagazineState.EMPTY) {
                System.out.println("Loading chamber with 1 round from magazine...");
                chamber.load(magazine.unload1Round());
                bolt.close();
            } else {
                System.out.println("Magazine is empty! Bolt holds open!");
            }
        }
    }

}
