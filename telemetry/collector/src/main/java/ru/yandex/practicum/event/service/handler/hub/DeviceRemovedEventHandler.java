package ru.yandex.practicum.event.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.hub.event.DeviceRemovedEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public class DeviceRemovedEventHandler extends AbstractHubEventHandler implements HubEventHandler {
    public DeviceRemovedEventHandler(CollectorService collectorService) {
        super(collectorService);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto eventProto) {
        DeviceRemovedEvent hubEvent = new DeviceRemovedEvent();
        hubEvent.setId(eventProto.getDeviceRemoved().getId());
        setHubEventFields(eventProto, hubEvent);
    }
}
