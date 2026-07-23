package FunctionClass;

import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;

public class Ammunition {
    private Caliber caliber;
    private AmmoType ammoType;
    private AmmoState ammoState;

    public enum AmmoState {
        UNFIRED,
        FIRED
    }

    public Ammunition(Caliber caliber, AmmoType ammoType) {
        this(caliber, ammoType, AmmoState.UNFIRED);
    }

    public Ammunition(Caliber caliber, AmmoType ammoType, AmmoState ammoState) {
        this.caliber = caliber;
        this.ammoType = ammoType;
        this.ammoState = ammoState;
    }

    public Caliber getCaliber() {
        return caliber;
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }

    public AmmoState getAmmoState() {
        return ammoState;
    }

    public void setAmmoState(AmmoState ammoState) {
        this.ammoState = ammoState;
    }
    
    public String toString() {
        return "Ammunition: " + caliber + " " + ammoType;
    }
}
