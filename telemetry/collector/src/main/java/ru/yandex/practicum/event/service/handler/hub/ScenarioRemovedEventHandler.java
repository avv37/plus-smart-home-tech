package ru.yandex.practicum.event.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.hub.event.ScenarioRemovedEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public class ScenarioRemovedEventHandler extends AbstractHubEventHandler implements HubEventHandler {
    public ScenarioRemovedEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto eventProto) {
        ScenarioRemovedEvent hubEvent = new ScenarioRemovedEvent();
        hubEvent.setName(eventProto.getScenarioRemoved().getName());
        setHubEventFields(eventProto, hubEvent);
    }
}
