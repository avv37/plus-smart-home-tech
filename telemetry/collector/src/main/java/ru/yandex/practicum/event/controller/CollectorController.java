package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.model.hub.base.HubEvent;
import ru.yandex.practicum.event.model.sensor.base.SensorEvent;
import ru.yandex.practicum.event.service.CollectorService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@Slf4j
public class CollectorController {

    private final CollectorService collectorService;

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Получен HubEvent event: {}", event.toString());
        collectorService.sendHubEvent(event);

    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Получен SensorEvent event: {}", event.toString());
        collectorService.sendSensorEvent(event);

    }
}
