package ru.yandex.practicum.event.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor.event.LightSensorEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class LightSensorEventHandler extends AbstractSensorEventHandler implements SensorEventHandler {
    public LightSensorEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto eventProto) {
        LightSensorEvent sensorEvent = new LightSensorEvent();
        sensorEvent.setLinkQuality(eventProto.getLightSensor().getLinkQuality());
        sensorEvent.setLuminosity(eventProto.getLightSensor().getLuminosity());
        setSensorEventFields(eventProto, sensorEvent);
    }
}
