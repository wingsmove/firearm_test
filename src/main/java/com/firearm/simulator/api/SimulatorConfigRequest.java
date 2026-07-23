package com.firearm.simulator.api;

import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;
import com.firearm.simulator.model.FiringSystem;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SimulatorConfigRequest(
        @NotNull FiringSystem firingSystem,
        @NotNull Caliber caliber,
        @NotNull AmmoType ammoType,
        @Min(1) @Max(100) int magazineCapacity,
        @Min(0) @Max(100) int initialRounds
) {
    public static SimulatorConfigRequest defaults() {
        return new SimulatorConfigRequest(FiringSystem.CLOSED_BOLT, Caliber._9mm, AmmoType.FMJ, 17, 5);
    }
}
