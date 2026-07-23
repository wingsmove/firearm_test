package com.firearm.simulator.api;

import java.time.Instant;
import java.util.List;

public record SimulatorSnapshot(
        String firingSystem,
        String caliber,
        String ammoType,
        boolean malfunctioned,
        MagazineSnapshot magazine,
        ChamberSnapshot chamber,
        BoltSnapshot bolt,
        List<SimulatorEvent> events
) {
    public record MagazineSnapshot(
            boolean inserted,
            String state,
            int capacity,
            int rounds
    ) {
    }

    public record ChamberSnapshot(
            String state,
            AmmunitionSnapshot ammunition
    ) {
    }

    public record BoltSnapshot(String state) {
    }

    public record AmmunitionSnapshot(
            String caliber,
            String ammoType,
            String state
    ) {
    }

    public record SimulatorEvent(long sequence, Instant occurredAt, String action, String message) {
    }
}
