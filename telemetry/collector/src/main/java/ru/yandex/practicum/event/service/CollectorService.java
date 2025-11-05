package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.model.hub.base.HubEvent;
import ru.yandex.practicum.event.model.sensor.base.SensorEvent;

public interface CollectorService {
    void sendSensorEvent(SensorEvent sensorEvent);

    void sendHubEvent(HubEvent hubEvent);
}
