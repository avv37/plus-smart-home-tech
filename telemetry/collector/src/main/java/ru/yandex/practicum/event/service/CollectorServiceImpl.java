package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.mapper.HubEventAvroMapper;
import ru.yandex.practicum.event.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.event.model.hub.base.HubEvent;
import ru.yandex.practicum.event.model.sensor.base.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {

    private final HubEventAvroMapper hubEventAvroMapper;
    private final SensorEventAvroMapper sensorEventAvroMapper;
    private final Producer<String, SpecificRecordBase> kafkaProducer;

    @Value("${kafka.collector.sensor-topic}")
    private String sensorTopic;

    @Value("${kafka.collector.hub-topic}")
    private String hubTopic;

    @Override
    public void sendSensorEvent(SensorEvent sensorEvent) {
        log.info("Получен SensorEvent event: {}", sensorEvent.toString());
        String hubId = sensorEvent.getHubId();
        Long timestamp = sensorEvent.getTimestamp().toEpochMilli();
        SensorEventAvro message = sensorEventAvroMapper.toSensorEventAvro(sensorEvent);
        log.info("SensorEventAvro message = {}", message);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                sensorTopic,
                null,
                timestamp,
                hubId,
                message);
        send(record, sensorEvent.getType().toString());
    }

    @Override
    public void sendHubEvent(HubEvent hubEvent) {
        log.info("Получен HubEventAvro event: {}", hubEvent.toString());
        String hubId = hubEvent.getHubId();
        Long timestamp = hubEvent.getTimestamp().toEpochMilli();
        HubEventAvro message = hubEventAvroMapper.toHubEventAvro(hubEvent);
        log.info("HubEventAvro message = {}", message);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                hubTopic,
                null,
                timestamp,
                hubId,
                message);
        send(record, hubEvent.getType().toString());
    }

    private void send(ProducerRecord<String, SpecificRecordBase> record, String type) {
        Future<RecordMetadata> future = kafkaProducer.send(record);
        kafkaProducer.flush();
        try {
            RecordMetadata metadata = future.get();
            log.info("Событие {} было успешно сохранёно в топик {} в партицию {} со смещением {}",
                    type, metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Не удалось записать событие {} в топик {}", type, sensorTopic, e);
        }
    }

}
