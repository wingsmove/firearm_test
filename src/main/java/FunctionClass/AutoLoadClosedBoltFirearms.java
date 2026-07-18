package FunctionClass;

public class AutoLoadClosedBoltFirearms extends Firearm {

    public AutoLoadClosedBoltFirearms(Magazine magazine, Chamber chamber, Bolt bolt) {
        super(magazine, chamber, bolt);
        this.magInserted = (magazine != null);
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
            openBolt();
            System.out.println("Chamber unloaded...");
            if (!magInserted) {
                System.out.println("Magazine is not inserted! Bolt closes, but does not load chamber.");
                closeBolt();
            } else if (magazine.getState() != Magazine.MagazineState.EMPTY) {
                System.out.println("Loading chamber with 1 round from magazine...");
                closeBolt();
            } else {
                System.out.println("Magazine is empty! Bolt holds open!");
            }
        }
    }

    @Override
    public void insertMagazine(Magazine magazine) {
        System.out.println("Inserting magazine...");
        this.magazine = magazine;
        this.magInserted = true;
    }

    @Override
    public void removeMagazine() {
        System.out.println("Removing magazine...");
        this.magazine = null;
        this.magInserted = false;
    }

    @Override
    public void chamberLoad(Ammunition ammunition) {
        if (!malfunctioned()) {
            System.out.println("Hand-loading a single round into the chamber...");
            openBolt();
            chamber.load(ammunition);
            bolt.close();
        }
    }

}
