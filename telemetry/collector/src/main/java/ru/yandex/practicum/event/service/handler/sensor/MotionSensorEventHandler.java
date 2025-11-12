package ru.yandex.practicum.event.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor.event.MotionSensorEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class MotionSensorEventHandler extends AbstractSensorEventHandler implements SensorEventHandler {
    public MotionSensorEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public void handle(SensorEventProto eventProto) {
        MotionSensorEvent sensorEvent = new MotionSensorEvent();
        sensorEvent.setMotion(eventProto.getMotionSensor().getMotion());
        sensorEvent.setVoltage(eventProto.getMotionSensor().getVoltage());
        sensorEvent.setLinkQuality(eventProto.getMotionSensor().getLinkQuality());
        setSensorEventFields(eventProto, sensorEvent);
    }
}
