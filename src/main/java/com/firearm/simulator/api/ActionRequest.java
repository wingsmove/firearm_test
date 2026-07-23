package com.firearm.simulator.api;

import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;

public record ActionRequest(
        Caliber caliber,
        AmmoType ammoType
) {
}
