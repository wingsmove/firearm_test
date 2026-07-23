package com.firearm.simulator.service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.springframework.stereotype.Service;

import FunctionClass.Ammunition;
import FunctionClass.AutoLoadClosedBoltFirearms;
import FunctionClass.AutoLoadOpenBoltFirearms;
import FunctionClass.Bolt;
import FunctionClass.Chamber;
import FunctionClass.Firearm;
import FunctionClass.Magazine;
import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;
import com.firearm.simulator.api.ActionRequest;
import com.firearm.simulator.api.SimulatorConfigRequest;
import com.firearm.simulator.api.SimulatorOptions;
import com.firearm.simulator.api.SimulatorSnapshot;
import com.firearm.simulator.api.SimulatorSnapshot.AmmunitionSnapshot;
import com.firearm.simulator.api.SimulatorSnapshot.BoltSnapshot;
import com.firearm.simulator.api.SimulatorSnapshot.ChamberSnapshot;
import com.firearm.simulator.api.SimulatorSnapshot.MagazineSnapshot;
import com.firearm.simulator.api.SimulatorSnapshot.SimulatorEvent;
import com.firearm.simulator.model.FiringSystem;
import com.firearm.simulator.model.SimulatorAction;

@Service
public class SimulatorService {

    private static final int MAX_EVENTS = 50;

    private final Deque<SimulatorEvent> events = new ArrayDeque<>();
    private Firearm firearm;
    private SimulatorConfigRequest config;
    private long eventSequence;

    public SimulatorService() {
        reset(SimulatorConfigRequest.defaults());
    }

    public synchronized SimulatorSnapshot reset(SimulatorConfigRequest request) {
        validate(request);

        Magazine magazine = new Magazine(request.magazineCapacity(), request.caliber());
        for (int i = 0; i < request.initialRounds(); i++) {
            magazine.load1Round(newRound(request.caliber(), request.ammoType()));
        }

        Chamber chamber = new Chamber(request.caliber(), null);
        Bolt bolt = new Bolt();
        firearm = request.firingSystem() == FiringSystem.OPEN_BOLT
                ? new AutoLoadOpenBoltFirearms(magazine, chamber, bolt)
                : new AutoLoadClosedBoltFirearms(magazine, chamber, bolt);
        config = request;
        events.clear();
        eventSequence = 0;
        addEvent("RESET", "Created a %s simulator with %d of %d rounds."
                .formatted(label(request.firingSystem()), request.initialRounds(), request.magazineCapacity()));
        return snapshot();
    }

    public synchronized SimulatorSnapshot perform(SimulatorAction action, ActionRequest request) {
        ActionRequest safeRequest = request == null ? new ActionRequest(null, null) : request;
        Caliber caliber = safeRequest.caliber() == null ? config.caliber() : safeRequest.caliber();
        AmmoType ammoType = safeRequest.ammoType() == null ? config.ammoType() : safeRequest.ammoType();

        String message = switch (action) {
            case FIRE -> {
                firearm.fire();
                yield "Pulled the trigger.";
            }
            case CYCLE -> {
                firearm.cycle();
                yield "Cycled the action.";
            }
            case OPEN_BOLT -> {
                firearm.openBolt();
                yield "Opened the bolt and extracted any chambered round.";
            }
            case CLOSE_BOLT -> {
                firearm.closeBolt();
                yield "Closed the bolt and attempted to feed a round.";
            }
            case LOAD_MAGAZINE_FMJ -> {
                requireMagazine();
                firearm.getMagazine().load1Round(newRound(caliber, AmmoType.FMJ));
                yield "Loaded one %s FMJ round into the magazine.".formatted(caliber);
            }
            case LOAD_MAGAZINE_AP -> {
                requireMagazine();
                firearm.getMagazine().load1Round(newRound(caliber, AmmoType.AP));
                yield "Loaded one %s AP round into the magazine.".formatted(caliber);
            }
            case LOAD_MAGAZINE_HP -> {
                requireMagazine();
                firearm.getMagazine().load1Round(newRound(caliber, AmmoType.HP));
                yield "Loaded one %s HP round into the magazine.".formatted(caliber);
            }
            case LOAD_MAGAZINE_TO_CAPACITY -> {
                requireMagazine();
                while (firearm.getMagazine().getCurrentCapacity() < firearm.getMagazine().getCapacity()
                        && firearm.getMagazine().getState() != Magazine.MagazineState.MALFUNCTIONED) {
                    firearm.getMagazine().load1Round(newRound(caliber, ammoType));
                }
                yield "Attempted to load the magazine to capacity.";
            }
            case INSERT_MAGAZINE -> {
                if (firearm.isMagazineInserted()) {
                    throw new IllegalStateException("A magazine is already inserted.");
                }
                Magazine magazine = new Magazine(config.magazineCapacity(), config.caliber());
                firearm.insertMagazine(magazine);
                yield "Inserted an empty magazine.";
            }
            case REMOVE_MAGAZINE -> {
                if (!firearm.isMagazineInserted()) {
                    throw new IllegalStateException("No magazine is currently inserted.");
                }
                firearm.removeMagazine();
                yield "Removed the magazine.";
            }
            case CHAMBER_LOAD -> {
                firearm.chamberLoad(newRound(caliber, ammoType));
                yield "Hand-loaded one %s %s round.".formatted(caliber, ammoType);
            }
            case CLEAR_MALFUNCTION -> {
                firearm.clearMalfunction();
                yield "Cleared component malfunctions.";
            }
        };

        addEvent(action.name(), message);
        return snapshot();
    }

    public synchronized SimulatorSnapshot snapshot() {
        Magazine magazine = firearm.getMagazine();
        MagazineSnapshot magazineSnapshot = magazine == null
                ? new MagazineSnapshot(false, "NOT_INSERTED", config.magazineCapacity(), 0)
                : new MagazineSnapshot(
                        firearm.isMagazineInserted(),
                        magazine.getState().name(),
                        magazine.getCapacity(),
                        magazine.getCurrentCapacity());

        Ammunition chamberedRound = firearm.getChamber().getAmmunition();
        AmmunitionSnapshot ammunitionSnapshot = chamberedRound == null
                ? null
                : new AmmunitionSnapshot(
                        chamberedRound.getCaliber().name(),
                        chamberedRound.getAmmoType().name(),
                        chamberedRound.getAmmoState().name());

        ChamberSnapshot chamberSnapshot = new ChamberSnapshot(
                firearm.getChamber().getState().name(),
                ammunitionSnapshot);
        BoltSnapshot boltSnapshot = new BoltSnapshot(firearm.getBolt().getState().name());

        boolean malfunctioned = magazine != null
                && magazine.getState() == Magazine.MagazineState.MALFUNCTIONED
                || firearm.getChamber().getState() == Chamber.ChamberState.MALFUNCTIONED
                || firearm.getBolt().getState() == Bolt.BoltState.MALFUNCTIONED;

        return new SimulatorSnapshot(
                config.firingSystem().name(),
                config.caliber().name(),
                config.ammoType().name(),
                malfunctioned,
                magazineSnapshot,
                chamberSnapshot,
                boltSnapshot,
                List.copyOf(events));
    }

    public SimulatorOptions options() {
        return new SimulatorOptions(
                names(FiringSystem.values()),
                names(Caliber.values()),
                names(AmmoType.values()),
                names(SimulatorAction.values()));
    }

    private void validate(SimulatorConfigRequest request) {
        if (request.initialRounds() > request.magazineCapacity()) {
            throw new IllegalArgumentException("Initial rounds cannot exceed magazine capacity.");
        }
    }

    private void requireMagazine() {
        if (!firearm.isMagazineInserted() || firearm.getMagazine() == null) {
            throw new IllegalStateException("Insert a magazine before loading it.");
        }
    }

    private Ammunition newRound(Caliber caliber, AmmoType ammoType) {
        return new Ammunition(caliber, ammoType);
    }

    private void addEvent(String action, String message) {
        events.addFirst(new SimulatorEvent(++eventSequence, Instant.now(), action, message));
        while (events.size() > MAX_EVENTS) {
            events.removeLast();
        }
    }

    private String label(FiringSystem firingSystem) {
        return firingSystem.name().toLowerCase().replace('_', '-');
    }

    private List<String> names(Enum<?>[] values) {
        return Arrays.stream(values).map(Enum::name).toList();
    }
}
