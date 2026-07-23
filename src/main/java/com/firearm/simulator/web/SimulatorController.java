package com.firearm.simulator.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firearm.simulator.api.ActionRequest;
import com.firearm.simulator.api.SimulatorConfigRequest;
import com.firearm.simulator.api.SimulatorOptions;
import com.firearm.simulator.api.SimulatorSnapshot;
import com.firearm.simulator.model.SimulatorAction;
import com.firearm.simulator.service.SimulatorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/simulator")
public class SimulatorController {

    private final SimulatorService simulatorService;

    public SimulatorController(SimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    @GetMapping
    SimulatorSnapshot getSnapshot() {
        return simulatorService.snapshot();
    }

    @GetMapping("/options")
    SimulatorOptions getOptions() {
        return simulatorService.options();
    }

    @PostMapping("/reset")
    SimulatorSnapshot reset(@Valid @RequestBody SimulatorConfigRequest request) {
        return simulatorService.reset(request);
    }

    @PostMapping("/actions/{action}")
    SimulatorSnapshot perform(
            @PathVariable SimulatorAction action,
            @RequestBody(required = false) ActionRequest request) {
        return simulatorService.perform(action, request);
    }
}
