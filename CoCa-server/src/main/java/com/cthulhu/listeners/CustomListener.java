package com.cthulhu.listeners;

import com.cthulhu.events.client.*;
import com.cthulhu.events.server.*;
import com.cthulhu.events.EventType;
import com.cthulhu.models.Investigator;
import com.cthulhu.services.DiceRollingService;
import com.cthulhu.services.InvestigatorService;
import com.cthulhu.services.LuckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;
import java.util.Map;

public class CustomListener implements MessageListener {
    private final String queueName;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final DiceRollingService diceRollingService;
    private final InvestigatorService investigatorService;
    private final LuckService luckService;

    public CustomListener(String queueName, JmsTemplate jmsTemplate, DiceRollingService diceRollingService,
                          InvestigatorService investigatorService, LuckService luckService) {
        objectMapper = new ObjectMapper();
        this.queueName = queueName;
        this.jmsTemplate = jmsTemplate;
        this.diceRollingService = diceRollingService;
        this.investigatorService = investigatorService;
        this.luckService = luckService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            String messageBody = message.getBody(String.class);
            JSONObject json = new JSONObject(messageBody);
            EventType type = EventType.valueOf(json.getString("eventType"));

            switch(type) {
                case ROLL -> {
                    EventRoll event = objectMapper.readValue(messageBody, EventRoll.class);
                    List<Investigator> investigatorTargets = investigatorService.getInvestigatorsWithNames(event.getInvestigatorTargets());
                    List<EventRollResult> rollResults = diceRollingService.rollTestsAgainstTargetValue(event.getDie(),
                            investigatorTargets, event.getTargetSkill(), event.getDifficulty(), event.getBonusDice(),
                            event.isAllowLuck(), event.isAllowPush());

                    for(EventRollResult e : rollResults) {
                        System.out.println("Rolled " + e.getResult() + " from " + event.getDie() + " for " + e.getInvestigatorName() +
                                " with success level of " + e.getGradation() + " at " + queueName);
                    }
                }
                case USE_LUCK -> {
                    EventUseLuck event = objectMapper.readValue(messageBody, EventUseLuck.class);
                    Investigator investigator = investigatorService.getInvestigatorByName(event.getInvestigatorName());
                    EventUseLuckResult eventResult = luckService.useLuck(investigator, event.getResult(), event.getGradation(), event.getTargetSkill());

                    System.out.println("Investigator " + eventResult.getInvestigatorName() + " used " + eventResult.getLuckUsed() +
                            " for skill " + eventResult.getTargetSkill() + " to achieve " + eventResult.getAchievedGradation());
                }
                case DEVELOP -> {
                    EventDevelop event = objectMapper.readValue(messageBody, EventDevelop.class);
                    Investigator investigator = investigatorService.getInvestigatorByName(event.getInvestigatorName());
                    EventDevelopResult eventResult = diceRollingService.rollDevelopTest(investigator, event.getTargetSkill());

                    System.out.println("Investigator " + eventResult.getInvestigatorName() + " got " + eventResult.getRollResult() +
                            " on develop roll for skill " + eventResult.getTargetSkill() + " and gained " + eventResult.getSkillGain());
                }
                case PUSH -> {
                    EventPush event = objectMapper.readValue(messageBody, EventPush.class);
                    Investigator investigator = investigatorService.getInvestigatorByName(event.getInvestigatorName());
                    EventPushResult eventResult = diceRollingService.rollPushTest(investigator, event.getPreviousGradation(),
                            event.getCurrentGradation(), event.getTargetSkill());

                    System.out.println("Investigator " + eventResult.getInvestigatorName() + " got " + eventResult.getRoll() +
                            " on push roll for skill " + eventResult.getTargetSkill() + " with previously achieved gradation of " +
                            eventResult.getPreviousGradation() + " and currently achieved " + eventResult.getAchievedGradation());
                }
                case DEVELOP_SKILLS -> {
                    EventDevelopSkills event = objectMapper.readValue(messageBody, EventDevelopSkills.class);
                    Investigator investigator = investigatorService.getInvestigatorByName(event.getInvestigatorName());
                    EventDevelopSkillsResult eventResult = new EventDevelopSkillsResult(investigator.getName(), investigator.getSuccessfullyUsedSkills());

                    System.out.println("Investigator " + eventResult.getInvestigatorName() + " successfully used following skills ");
                    for(Map.Entry<String, Integer> entry : investigator.getSuccessfullyUsedSkills().entrySet()) {
                        System.out.println(entry.getKey() + " used " + entry.getValue() + " times");
                    }
                }
                case INVESTIGATORS -> {
                    List<Investigator> investigators = investigatorService.getAllInvestigators();
                    EventInvestigatorsResult eventResult = new EventInvestigatorsResult(investigators);
                    eventResult.setEventType(EventType.INVESTIGATORS_RESULT);
                    eventResult.setTargetQueue("testQueue");
                    jmsTemplate.convertAndSend(eventResult.getTargetQueue(), eventResult);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
