package FunctionClass;

public abstract class Firearm {
    protected Magazine magazine;
    protected Chamber chamber;
    protected Bolt bolt;

    public Firearm(Magazine magazine, Chamber chamber, Bolt bolt) {
        this.magazine = magazine;
        this.chamber = chamber;
        this.bolt = bolt;
    }

    public boolean malfunctioned() {
        if (magazine.malfunctioned() || chamber.malfunctioned() || bolt.malfunctioned()) {
            System.out.println("Firearm is malfunctioned!");
            return true;
        } else {
            return false;
        }
    }

    public void clearMalfunction() {
        if (magazine.malfunctioned()) {
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

    public abstract void fire();

    public abstract void cycle();

}
