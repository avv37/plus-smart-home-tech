package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SnapshotService {
    private final Map<String, SensorsSnapshotAvro> sensorsSnapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("updateState, event = {}", event);
        log.info("updateState, sensorsSnapshots = {}", sensorsSnapshots);
        SensorsSnapshotAvro snapshot = sensorsSnapshots.computeIfAbsent(event.getHubId(), hubId ->
                SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setSensorsState(new HashMap<>())
                    .setTimestamp(event.getTimestamp())
                    .build()
                );
        log.info("updateState, snapshot = {}", snapshot);
        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
        log.info("updateState, oldState = {}", oldState);
        if (oldState != null &&
                (oldState.getTimestamp().isAfter(event.getTimestamp()) ||
                        oldState.getData().equals(event.getPayload()))) {
            log.info("updateState return empty");
            return Optional.empty();
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        log.info("updateState, newState = {}", newState);
        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
        log.info("updateState, return snapshot = {}", snapshot);
        return Optional.of(snapshot);
    }

}
