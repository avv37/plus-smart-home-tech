package ru.yandex.practicum.event.model.hub.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.event.model.hub.base.enums.ConditionOperation;
import ru.yandex.practicum.event.model.hub.base.enums.ConditionType;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    @NotBlank
    private String sensorId;
    @NotNull
    private ConditionType type;
    @NotNull
    private ConditionOperation operation;

    private Object value;
}
