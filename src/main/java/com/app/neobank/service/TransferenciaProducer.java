package com.app.neobank.service;

import com.app.neobank.event.TransferenciaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransferenciaProducer {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String TOPIC = "neobank-transferencias";

    public TransferenciaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEventoDeTransferencia(TransferenciaEvent evento) {
        log.info("📡 Productor Kafka: Enviando evento asincrónico al topic [{}] -> {}", TOPIC, evento);
        // Disparamos el JSON hacia la red de Kafka
        kafkaTemplate.send(TOPIC, evento);
    }
}