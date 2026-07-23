package FunctionClass;

import java.util.ArrayList;

import FunctionClass.Enums.Caliber;

public class Magazine {

    public enum MagazineState {
        EMPTY,
        LOADED,
        FULL,
        MALFUNCTIONED
    }

    private MagazineState state;
    int capacity;
    int currentCapacity;
    ArrayList<Ammunition> ammunition;
    private Caliber caliber;

    public Magazine(int capacity, Caliber caliber) {
        this.capacity = capacity;
        this.currentCapacity = 0;
        this.state = MagazineState.EMPTY;
        this.ammunition = new ArrayList<Ammunition>();
        this.caliber = caliber;
    }

    public void load1Round(Ammunition ammunition) {
        if (ammunition == null) {
            System.out.println("No ammunition provided to load!");
            return;
        }
        if (ammunition.getCaliber() != caliber) {
            System.out.println("Caliber mismatch! Magazine malfunction!");
            malfunction();
            return;
        }
        if (!malfunctioned()) {
            if (state == MagazineState.FULL) {
                System.out.println("Magazine is full!");
            } else {
                System.out.println("Loading 1 round into magazine...");
                this.ammunition.add(ammunition);
                currentCapacity++;
                state = (currentCapacity == capacity) ? MagazineState.FULL : MagazineState.LOADED;
            }
        }
    }

    public void loadToCapacity(Ammunition ammunition) {
        if (!malfunctioned()) {
            if (state == MagazineState.FULL) {
                System.out.println("Magazine is full!");
            } else {
                System.out.println("Loading magazine to capacity...");
                while (currentCapacity < capacity && !malfunctioned()) {
                    load1Round(ammunition);
                }
            }
        }
    }

    public Ammunition unload1Round() {
        if (!malfunctioned()) {
            if (state == MagazineState.EMPTY) {
                System.out.println("Magazine is empty!");
            } else {
                System.out.println("Unloading 1 round from magazine...");
                Ammunition unloadedAmmunition = ammunition.get(ammunition.size() - 1);
                ammunition.remove(ammunition.size() - 1);
                currentCapacity--;
                state = (currentCapacity == 0) ? MagazineState.EMPTY : MagazineState.LOADED;
                return unloadedAmmunition;
            }
        }
        return null;
    }

    public void unloadAll() {
        System.out.println("Unloading all ammunition from magazine...");
        state = MagazineState.EMPTY;
        currentCapacity = 0;
        ammunition.clear();
    }

    public void malfunction() {
        System.out.println("Magazine set to malfunctioned!");
        state = MagazineState.MALFUNCTIONED;
    }

    public void clearMalfunction() {
        System.out.println("Magazine cleared of malfunction!");
        state = MagazineState.EMPTY;
        currentCapacity = 0;
        ammunition.clear();
    }

    public MagazineState getState() {
        return state;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public boolean malfunctioned() {
        if (state == MagazineState.MALFUNCTIONED) {
            System.out.println("Magazine is malfunctioned!");
            return true;
        } else {
            return false;
        }
    }

}
