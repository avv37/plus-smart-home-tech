package ru.yandex.practicum.event.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.hub.base.DeviceAction;
import ru.yandex.practicum.event.model.hub.base.ScenarioCondition;
import ru.yandex.practicum.event.model.hub.base.enums.ActionType;
import ru.yandex.practicum.event.model.hub.base.enums.ConditionOperation;
import ru.yandex.practicum.event.model.hub.base.enums.ConditionType;
import ru.yandex.practicum.event.model.hub.event.ScenarioAddedEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventHandler extends AbstractHubEventHandler implements HubEventHandler {
    public ScenarioAddedEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto eventProto) {
        ScenarioAddedEvent hubEvent = new ScenarioAddedEvent();
        hubEvent.setName(eventProto.getScenarioAdded().getName());

        // Маппинг conditions
        List<ScenarioCondition> conditions = eventProto.getScenarioAdded().getConditionsList().stream()
                .map(this::mapScenarioCondition)
                .collect(Collectors.toList());
        hubEvent.setConditions(conditions);

        // Маппинг actions
        List<DeviceAction> actions = eventProto.getScenarioAdded().getActionsList().stream()
                .map(this::mapDeviceAction)
                .collect(Collectors.toList());
        hubEvent.setActions(actions);

        setHubEventFields(eventProto, hubEvent);
    }

    private ScenarioCondition mapScenarioCondition(ScenarioConditionProto proto) {
        ScenarioCondition condition = new ScenarioCondition();
        condition.setSensorId(proto.getSensorId());

        // Маппинг enum
        condition.setType(ConditionType.valueOf(proto.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(proto.getOperation().name()));

        // Маппинг value
        switch (proto.getValueCase()) {
            case BOOL_VALUE:
                condition.setValue(proto.getBoolValue());
                break;
            case INT_VALUE:
                condition.setValue(proto.getIntValue());
                break;
            case VALUE_NOT_SET:
                condition.setValue(null);
                break;
        }

        return condition;
    }

    private DeviceAction mapDeviceAction(DeviceActionProto proto) {
        DeviceAction action = new DeviceAction();
        action.setSensorId(proto.getSensorId());
        action.setType(ActionType.valueOf(proto.getType().name()));

        if (proto.hasValue()) {
            action.setValue(proto.getValue());
        }

        return action;
    }
}
