package FunctionClass;

import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;

public class Ammunition {
    private Caliber caliber;
    private AmmoType ammoType;

    public Ammunition(Caliber caliber, AmmoType ammoType) {
        this.caliber = caliber;
        this.ammoType = ammoType;
    }

    public Caliber getCaliber() {
        return caliber;
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }

    public String toString() {
        return "Ammunition: " + caliber + " " + ammoType;
    }
}
