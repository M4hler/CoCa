package com.cthulhu.listeners;

import com.cthulhu.annotations.CoCaListener;
import com.cthulhu.events.RollEvent;
import com.cthulhu.services.BladeRunnerService;
import com.cthulhu.services.RollEventService;
import org.springframework.stereotype.Service;

@Service
@CoCaListener(handles = RollEvent.class)
public class RollEventListener extends CustomListener<RollEvent> {
    private final BladeRunnerService bladeRunnerService;
    private final RollEventService rollEventService;

    public RollEventListener(BladeRunnerService bladeRunnerService, RollEventService rollEventService) {
        this.bladeRunnerService = bladeRunnerService;
        this.rollEventService = rollEventService;
    }

    @Override
    public void handle(RollEvent event) {
        var bladeRunner = bladeRunnerService.getBladeRunner(event.getBladeRunner());
        var attributeDie = bladeRunner.getAttributeValueForSkill(event.getSkill());
        var skillDie = bladeRunner.getSkillValue(event.getSkill());
        var bonusDie = event.getBonusDie() != null ? event.getBonusDie() : 0;
        rollEventService.rollDice(attributeDie, skillDie, bonusDie);
    }
}
