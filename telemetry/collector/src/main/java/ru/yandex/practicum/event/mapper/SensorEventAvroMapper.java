package ru.yandex.practicum.event.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor.base.SensorEvent;
import ru.yandex.practicum.event.model.sensor.event.ClimateSensorEvent;
import ru.yandex.practicum.event.model.sensor.event.LightSensorEvent;
import ru.yandex.practicum.event.model.sensor.event.MotionSensorEvent;
import ru.yandex.practicum.event.model.sensor.event.SwitchSensorEvent;
import ru.yandex.practicum.event.model.sensor.event.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
@Slf4j
public class SensorEventAvroMapper {
    public SensorEventAvro toSensorEventAvro(SensorEvent event) {
        log.info("toSensorEventAvro: id={}, type={}, hubId={}, timestamp={}",
                event.getId(), event.getType(), event.getHubId(), event.getTimestamp());
        log.debug(event.toString());

        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp());
        switch (event.getType()) {
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent sensorEvent = (MotionSensorEvent) event;
                MotionSensorAvro eventAvro = MotionSensorAvro.newBuilder()
                        .setLinkQuality(sensorEvent.getLinkQuality())
                        .setMotion(sensorEvent.getMotion())
                        .setVoltage(sensorEvent.getVoltage())
                        .build();
                builder.setPayload(eventAvro);
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent sensorEvent = (LightSensorEvent) event;
                LightSensorAvro eventAvro = LightSensorAvro.newBuilder()
                        .setLinkQuality(sensorEvent.getLinkQuality())
                        .setLuminosity(sensorEvent.getLuminosity())
                        .build();
                builder.setPayload(eventAvro);
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent sensorEvent = (SwitchSensorEvent) event;
                SwitchSensorAvro eventAvro = SwitchSensorAvro.newBuilder()
                        .setState(sensorEvent.getState())
                        .build();
                builder.setPayload(eventAvro);

            }
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent sensorEvent = (ClimateSensorEvent) event;
                ClimateSensorAvro eventAvro = ClimateSensorAvro.newBuilder()
                        .setCo2Level(sensorEvent.getCo2Level())
                        .setHumidity(sensorEvent.getHumidity())
                        .setTemperatureC(sensorEvent.getTemperatureC())
                        .build();
                builder.setPayload(eventAvro);

            }
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent sensorEvent = (TemperatureSensorEvent) event;
                TemperatureSensorAvro eventAvro = TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(sensorEvent.getTemperatureC())
                        .setTemperatureF(sensorEvent.getTemperatureF())
                        .build();
                builder.setPayload(eventAvro);
            }
            default -> {
                throw new IllegalArgumentException("Нет обработчика для события " + event.getType());
            }

        }
        return builder.build();

    }
}
