package ru.yandex.practicum.event.service.handler.hub;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.event.model.hub.base.HubEvent;
import ru.yandex.practicum.event.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class AbstractHubEventHandler {
    private final CollectorService collectorService;
    protected void setHubEventFields(HubEventProto proto, HubEvent event) {
        event.setHubId(proto.getHubId());
        event.setTimestamp(Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        ));
        collectorService.sendHubEvent(event);
    }
}
