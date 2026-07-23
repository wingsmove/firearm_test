package com.firearm.simulator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import FunctionClass.Enums.AmmoType;
import FunctionClass.Enums.Caliber;
import com.firearm.simulator.api.ActionRequest;
import com.firearm.simulator.api.SimulatorConfigRequest;
import com.firearm.simulator.api.SimulatorSnapshot;
import com.firearm.simulator.model.FiringSystem;
import com.firearm.simulator.model.SimulatorAction;

class SimulatorServiceTest {

    private final SimulatorService service = new SimulatorService();

    @Test
    void closedBoltSessionCyclesAndFiresThroughTheApiLayer() {
        service.reset(new SimulatorConfigRequest(
                FiringSystem.CLOSED_BOLT,
                Caliber._9mm,
                AmmoType.FMJ,
                17,
                3));

        SimulatorSnapshot chambered = service.perform(SimulatorAction.CYCLE, null);
        assertEquals("LOADED", chambered.chamber().state());
        assertEquals(2, chambered.magazine().rounds());

        SimulatorSnapshot fired = service.perform(SimulatorAction.FIRE, null);
        assertEquals("FIRED", fired.chamber().state());
        assertEquals("FIRED", fired.chamber().ammunition().state());
    }

    @Test
    void wrongCaliberRoundCreatesAndClearsAMalfunction() {
        service.reset(SimulatorConfigRequest.defaults());

        SimulatorSnapshot malfunctioned = service.perform(
                SimulatorAction.LOAD_MAGAZINE_FMJ,
                new ActionRequest(Caliber._45ACP, AmmoType.FMJ));

        assertTrue(malfunctioned.malfunctioned());
        assertEquals("MALFUNCTIONED", malfunctioned.magazine().state());

        SimulatorSnapshot cleared = service.perform(SimulatorAction.CLEAR_MALFUNCTION, null);
        assertFalse(cleared.malfunctioned());
        assertEquals("EMPTY", cleared.magazine().state());
    }
}
