package FunctionClass;

import FunctionClass.Enums.Caliber;

public class Chamber {

    public enum ChamberState {
        EMPTY,
        LOADED,
        FIRED,
        MALFUNCTIONED
    }

    private ChamberState state;
    private Caliber caliber;
    private Ammunition ammunition;

    public Chamber(Caliber caliber, Ammunition ammunition) {
        this.caliber = caliber;
        this.ammunition = ammunition;
        this.state = (ammunition == null) ? ChamberState.EMPTY : ChamberState.LOADED;
    }

    public void load(Ammunition ammunition) {
        if (malfunctioned()) {
            return;
        }
        if (ammunition == null) {
            System.out.println("No ammunition provided to load!");
            return;
        }
        if (ammunition.getCaliber() != caliber) {
            System.out.println("Caliber mismatch! Chamber malfunction!");
            malfunction();
            return;
        }
        if (state == ChamberState.LOADED) {
            System.out.println("Chamber already loaded! Double feed!");
            malfunction();
            return;
        }
        System.out.println("Loading ammunition into chamber...");
        this.state = ChamberState.LOADED;
        this.ammunition = ammunition;
    }

    public void unload() {
        if (!malfunctioned()) {
            System.out.println("Unloading ammunition from chamber...");
            this.state = ChamberState.EMPTY;
            this.ammunition = null;
        }
    }

    public void fire() {
        if (malfunctioned()) {
            return;
        }
        if (state != ChamberState.LOADED) {
            System.out.println("Chamber is not loaded! Cannot fire!");
            return;
        }
        if (ammunition.getAmmoState() != Ammunition.AmmoState.UNFIRED) {
            System.out.println("Round already fired! Cannot fire!");
            return;
        }
        System.out.println("Bang!");
        ammunition.setAmmoState(Ammunition.AmmoState.FIRED);
        this.state = ChamberState.FIRED;
    }

    public void malfunction() {
        System.out.println("Chamber set to malfunctioned!");
        this.state = ChamberState.MALFUNCTIONED;
    }

    public void clearMalfunction() {
        System.out.println("Chamber cleared of malfunction!");
        this.state = ChamberState.EMPTY;
        this.ammunition = null;
    }

    public ChamberState getState() {
        return this.state;
    }

    public Ammunition getAmmunition() {
        return this.ammunition;
    }

    public boolean malfunctioned() {
        if (this.state == ChamberState.MALFUNCTIONED) {
            System.out.println("Chamber is malfunctioned!");
            return true;
        } else {
            return false;
        }
    }

    public void checkAmmunition() {
        System.out.println("Checking ammunition...");
        if (this.ammunition == null) {
            System.out.println("No ammunition in the chamber!");
        } else {
            System.out.println("Ammunition in the chamber: " + this.ammunition.toString());
        }
    }

}
