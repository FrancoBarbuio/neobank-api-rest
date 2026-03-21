package com.app.neobank.service;

import com.app.neobank.event.TransferenciaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificacionConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificacionConsumer.class);
    private final ObjectMapper objectMapper;

    public NotificacionConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "neobank-transferencias", groupId = "neobank-group")
    public void consumirEventoDeTransferencia(String mensajeJsonCrudo) {
        log.info("Consumidor Kafka: ¡Ha llegado un nuevo mensaje! -> {}", mensajeJsonCrudo);

        try {
            TransferenciaEvent evento = objectMapper.readValue(mensajeJsonCrudo, TransferenciaEvent.class);
            log.info("ÉXITO: Preparando envío de email para la cuenta origen: {}", evento.cuentaOrigen());
            log.info("Notificando que se transfirieron: ${}", evento.monto());
            log.info("--------------------------------------------------");

        } catch (Exception e) {
            log.error("Error grave: No se pudo interpretar el mensaje de Kafka. ¿Es un JSON válido?", e);
        }
    }
}