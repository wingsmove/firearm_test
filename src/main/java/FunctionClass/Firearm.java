package FunctionClass;

public abstract class Firearm {
    protected Magazine magazine;
    protected Chamber chamber;
    protected Bolt bolt;
    protected boolean magInserted;

    public Firearm(Magazine magazine, Chamber chamber, Bolt bolt) {
        this.magazine = magazine;
        this.chamber = chamber;
        this.bolt = bolt;
    }

    public boolean malfunctioned() {
        boolean magazineMalfunctioned = magazine != null && magazine.malfunctioned();
        if (magazineMalfunctioned || chamber.malfunctioned() || bolt.malfunctioned()) {
            System.out.println("Firearm is malfunctioned!");
            return true;
        } else {
            return false;
        }
    }

    public void clearMalfunction() {
        if (magazine != null && magazine.malfunctioned()) {
            System.out.println("Magazine is malfunctioned! Clearing malfunction...");
            magazine.clearMalfunction();
        }
        if (chamber.malfunctioned()) {
            System.out.println("Chamber is malfunctioned! Clearing malfunction...");
            chamber.clearMalfunction();
        }
        if (bolt.malfunctioned()) {
            System.out.println("Bolt is malfunctioned! Clearing malfunction...");
            bolt.clearMalfunction();
        }
    }

    public Magazine getMagazine() {
        return magazine;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public Bolt getBolt() {
        return bolt;
    }

    /**
     * Opens the bolt and clears the chamber. Whenever the bolt is retracted the
     * extractor pulls the round/case out of the chamber, so an open bolt should
     * always leave the chamber empty (unless the firearm is malfunctioned).
     */
    public void openBolt() {
        if (!malfunctioned()) {
            bolt.open();
            chamber.unload();
        }
    }

    public void closeBolt() {
        if (!malfunctioned()) {
            bolt.close();
            if(magInserted) {
                chamber.load(magazine.unload1Round());
            }
        }
    }
    public abstract void fire();

    public abstract void cycle();

    public abstract void insertMagazine(Magazine magazine);

    public abstract void removeMagazine();

    public abstract void chamberLoad(Ammunition ammunition);
}
