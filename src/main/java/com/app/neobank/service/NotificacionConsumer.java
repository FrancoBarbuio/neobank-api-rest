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

    // Esta anotación es mágica. Deja a este método escuchando infinitamente el canal.
    @KafkaListener(topics = "neobank-transferencias", groupId = "neobank-group")
    public void consumirEventoDeTransferencia(String mensajeJsonCrudo) {
        log.info("Consumidor Kafka: ¡Ha llegado un nuevo mensaje! -> {}", mensajeJsonCrudo);

        try {
            // 1. Convertimos el JSON de texto nuevamente a nuestro Record de Java
            TransferenciaEvent evento = objectMapper.readValue(mensajeJsonCrudo, TransferenciaEvent.class);

            // 2. Aquí iría la lógica de negocio (ej. conectarse a SendGrid o AWS SES para mandar un email)
            log.info("ÉXITO: Preparando envío de email para la cuenta origen: {}", evento.cuentaOrigen());
            log.info("Notificando que se transfirieron: ${}", evento.monto());
            log.info("--------------------------------------------------");

        } catch (Exception e) {
            // Si el mensaje es una "Píldora Envenenada", caemos aquí, imprimimos el error,
            // pero la aplicación NO se rompe ni se bloquea. ¡Arquitectura Resiliente!
            log.error("Error grave: No se pudo interpretar el mensaje de Kafka. ¿Es un JSON válido?", e);
        }
    }
}