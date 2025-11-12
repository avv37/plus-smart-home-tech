package ru.yandex.practicum.event.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor.event.SwitchSensorEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class SwitchSensorEventHandler extends AbstractSensorEventHandler implements SensorEventHandler {

    public SwitchSensorEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEventProto eventProto) {
        SwitchSensorEvent sensorEvent = new SwitchSensorEvent();
        sensorEvent.setState(eventProto.getSwitchSensor().getState());
        setSensorEventFields(eventProto, sensorEvent);
    }
}
