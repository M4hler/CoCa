package com.coca.server.services;

import com.coca.server.events.RollResultEvent;
import com.coca.server.states.NoActiveRoll;
import com.coca.server.states.RollState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RollEventService {
    private final GeneratorService generatorService;
    private final MessageSenderService messageSenderService;
    private final BladeRunnerService bladeRunnerService;
    private final Map<String, RollState> rollStates;

    public RollEventService(GeneratorService generatorService, MessageSenderService messageSenderService, BladeRunnerService bladeRunnerService) {
        this.generatorService = generatorService;
        this.messageSenderService = messageSenderService;
        this.bladeRunnerService = bladeRunnerService;
        rollStates = new HashMap<>();
    }

    public void rollDice(String name, String attribute, String skill, int attributeDie, int skillDie, int bonusDie) {
        if(!rollStates.containsKey(name) || rollStates.get(name) == null) {
            var isHuman = bladeRunnerService.getBladeRunner(name).isHuman();
            var noActiveRoll = new NoActiveRoll(generatorService, isHuman, attribute, skill, attributeDie, skillDie, bonusDie);
            rollStates.put(name, noActiveRoll);
        }

        var state = rollStates.get(name);
        var result = state.roll();
        rollStates.put(name, state.transition());
        messageSenderService.sendToAll(new RollResultEvent(name, result));
    }

    public void pushRoll(String name) {
        var state = rollStates.get(name);
        var result = state.roll();
        rollStates.put(name, state.transition());
        messageSenderService.sendToAll(new RollResultEvent(name, result));
    }

    public void accept(String name) {
        rollStates.put(name, null);
    }
}
