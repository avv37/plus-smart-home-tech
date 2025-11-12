package ru.yandex.practicum.event.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.hub.base.enums.DeviceType;
import ru.yandex.practicum.event.model.hub.event.DeviceAddedEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public class DeviceAddedEventHandler extends AbstractHubEventHandler implements HubEventHandler {
    public DeviceAddedEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto eventProto) {
        DeviceAddedEvent hubEvent = new DeviceAddedEvent();
        hubEvent.setId(eventProto.getDeviceAdded().getId());

        DeviceTypeProto protoType = eventProto.getDeviceAdded().getType();
        DeviceType javaType = DeviceType.valueOf(protoType.name());
        hubEvent.setDeviceType(javaType);
        setHubEventFields(eventProto, hubEvent);
    }
}
