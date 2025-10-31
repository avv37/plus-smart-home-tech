package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.mapper.HubEventAvroMapper;
import ru.yandex.practicum.event.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.event.model.hub.base.HubEvent;
import ru.yandex.practicum.event.model.sensor.base.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;


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
        SensorEventAvro message = sensorEventAvroMapper.toSensorEventAvro(sensorEvent);
        log.info("SensorEventAvro message = {}", message);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(sensorTopic, message);
        kafkaProducer.send(record);
    }

    @Override
    public void sendHubEvent(HubEvent hubEvent) {
        log.info("Получен HubEventAvro event: {}", hubEvent.toString());
        HubEventAvro message = hubEventAvroMapper.toHubEventAvro(hubEvent);
        log.info("HubEventAvro message = {}", message);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(hubTopic, message);
        kafkaProducer.send(record);
    }
}
