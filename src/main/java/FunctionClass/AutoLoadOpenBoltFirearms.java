package FunctionClass;

public class AutoLoadOpenBoltFirearms extends Firearm {

    public AutoLoadOpenBoltFirearms(Magazine magazine, Chamber chamber, Bolt bolt) {
        super(magazine, chamber, bolt);
        this.magInserted = (magazine != null);
    }

    @Override
    public void fire() {
        if (!malfunctioned() && bolt.getState() == Bolt.BoltState.OPEN) {
            System.out.println("Firing firearm...");
            // The bolt runs forward. closeBolt() feeds from the magazine when one
            // is inserted; if the chamber is already hand-loaded this triggers a
            // double feed (malfunction) via Chamber.load(). With no magazine and an
            // empty chamber the bolt simply closes on nothing.
            closeBolt();
        }
    }

    @Override
    public void cycle() {
        if (!malfunctioned()) {
            System.out.println("Cycling firearm...");
            openBolt();
            System.out.println("Chamber unloaded...");
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
        }
    }

    @Override
    public void closeBolt() {
        if (!malfunctioned()) {
            if(magInserted) {
                chamber.load(magazine.unload1Round());
            }
            if (!malfunctioned() && bolt.getState() == Bolt.BoltState.OPEN) {
                System.out.println("Bolt closed! Firing firearm...");
                chamber.fire();
            }
            bolt.close();


        }
    }
}
