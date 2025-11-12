package ru.yandex.practicum.event.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor.event.TemperatureSensorEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class TemperatureSensorEventHandler extends AbstractSensorEventHandler implements SensorEventHandler {
    public TemperatureSensorEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto eventProto) {
        TemperatureSensorEvent sensorEvent = new TemperatureSensorEvent();
        sensorEvent.setTemperatureC(eventProto.getTemperatureSensor().getTemperatureC());
        sensorEvent.setTemperatureF(eventProto.getTemperatureSensor().getTemperatureF());
        setSensorEventFields(eventProto, sensorEvent);
    }
}
