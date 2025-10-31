package ru.yandex.practicum.event.model.hub.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.event.model.hub.base.enums.ActionType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAction {

    private String sensorId;

    private ActionType type;

    private Integer value;
}
