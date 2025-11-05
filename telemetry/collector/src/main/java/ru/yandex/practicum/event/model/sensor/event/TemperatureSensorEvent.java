package ru.yandex.practicum.event.model.sensor.event;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.event.model.sensor.base.SensorEventType;
import ru.yandex.practicum.event.model.sensor.base.SensorEvent;

@Getter
@Setter
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {
    @NotNull
    private Integer temperatureC;
    @NotNull
    private Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
