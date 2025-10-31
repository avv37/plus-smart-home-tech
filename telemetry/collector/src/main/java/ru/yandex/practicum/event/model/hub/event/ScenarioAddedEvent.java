package ru.yandex.practicum.event.model.hub.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.event.model.hub.base.DeviceAction;
import ru.yandex.practicum.event.model.hub.base.HubEvent;
import ru.yandex.practicum.event.model.hub.base.HubEventType;
import ru.yandex.practicum.event.model.hub.base.ScenarioCondition;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    private String name;
    @NotEmpty
    private List<ScenarioCondition> conditions;
    @NotEmpty
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
