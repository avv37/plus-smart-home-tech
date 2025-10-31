package ru.yandex.practicum.event.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.hub.base.DeviceAction;
import ru.yandex.practicum.event.model.hub.base.HubEvent;
import ru.yandex.practicum.event.model.hub.base.ScenarioCondition;
import ru.yandex.practicum.event.model.hub.event.DeviceAddedEvent;
import ru.yandex.practicum.event.model.hub.event.DeviceRemovedEvent;
import ru.yandex.practicum.event.model.hub.event.ScenarioAddedEvent;
import ru.yandex.practicum.event.model.hub.event.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.stream.Collectors;

@Component
@Slf4j
public class HubEventAvroMapper {
    public HubEventAvro toHubEventAvro(HubEvent event) {
        log.info("toHubEventAvro: type={}, hubId={}, timestamp={}", event.getType(), event.getHubId(), event.getTimestamp());
        log.debug(event.toString());

        HubEventAvro.Builder builder = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp());

        switch (event.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;
                DeviceAddedEventAvro eventAvro = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(DeviceTypeAvro.valueOf(String.valueOf(deviceAddedEvent.getDeviceType())))
                        .build();
                builder.setPayload(eventAvro);
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
                DeviceRemovedEventAvro eventAvro = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemovedEvent.getId())
                        .build();
                builder.setPayload(eventAvro);
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
                ScenarioAddedEventAvro eventAvro = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAddedEvent.getName())
                        .setConditions(scenarioAddedEvent.getConditions().stream()
                                .map(this::toScenarioConditionAvro)
                                .collect(Collectors.toList()))
                        .setActions(scenarioAddedEvent.getActions().stream()
                                .map(this::toDeviceActionAvro)
                                .collect(Collectors.toList()))
                        .build();

                builder.setPayload(eventAvro);
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
                ScenarioRemovedEventAvro eventAvro = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build();

                builder.setPayload(eventAvro);
            }
            default -> {
                throw new IllegalArgumentException("Нет обработчика для события " + event.getType());
            }
        }

        return builder.build();
    }

    private DeviceActionAvro toDeviceActionAvro(DeviceAction deviceAction) {
        DeviceActionAvro builder = DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .setType(ActionTypeAvro.valueOf(String.valueOf(deviceAction.getType())))
                .build();
        return builder;
    }

    private ScenarioConditionAvro toScenarioConditionAvro(ScenarioCondition condition) {
        ScenarioConditionAvro builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(String.valueOf(condition.getType())))
                .setOperation(ConditionOperationAvro.valueOf(String.valueOf(condition.getOperation())))
                .setValue(condition.getValue())
                .build();
        return builder;
    }
}
