package ru.yandex.practicum.event.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor.event.ClimateSensorEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class ClimateSensorEventHandler extends AbstractSensorEventHandler implements SensorEventHandler {
    public ClimateSensorEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto eventProto) {
        ClimateSensorEvent sensorEvent = new ClimateSensorEvent();
        sensorEvent.setHumidity(eventProto.getClimateSensor().getHumidity());
        sensorEvent.setTemperatureC(eventProto.getClimateSensor().getTemperatureC());
        sensorEvent.setCo2Level(eventProto.getClimateSensor().getCo2Level());
        setSensorEventFields(eventProto, sensorEvent);
    }
}
