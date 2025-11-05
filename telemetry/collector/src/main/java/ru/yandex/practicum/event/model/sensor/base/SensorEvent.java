package ru.yandex.practicum.event.model.sensor.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.event.model.sensor.event.ClimateSensorEvent;
import ru.yandex.practicum.event.model.sensor.event.LightSensorEvent;
import ru.yandex.practicum.event.model.sensor.event.MotionSensorEvent;
import ru.yandex.practicum.event.model.sensor.event.SwitchSensorEvent;
import ru.yandex.practicum.event.model.sensor.event.TemperatureSensorEvent;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = SensorEventType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT")
})
@Getter
@Setter
@ToString
public abstract class SensorEvent {
    @NotBlank
    private String id;
    @NotBlank
    private String hubId;

    private Instant timestamp = Instant.now();

    @NotNull
    public abstract SensorEventType getType();
}
