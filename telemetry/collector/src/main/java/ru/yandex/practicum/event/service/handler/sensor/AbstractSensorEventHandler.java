package ru.yandex.practicum.event.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.event.model.sensor.base.SensorEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class AbstractSensorEventHandler {
    private final CollectorService collectorService;

    protected void setSensorEventFields(SensorEventProto proto, SensorEvent event) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        ));
        collectorService.sendSensorEvent(event);
    }
}
