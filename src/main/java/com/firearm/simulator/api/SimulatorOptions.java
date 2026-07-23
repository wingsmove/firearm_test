package com.firearm.simulator.api;

import java.util.List;

public record SimulatorOptions(
        List<String> firingSystems,
        List<String> calibers,
        List<String> ammoTypes,
        List<String> actions
) {
}
