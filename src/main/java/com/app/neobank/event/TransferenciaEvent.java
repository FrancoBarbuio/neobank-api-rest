package com.app.neobank.event;

import java.math.BigDecimal;

// Un Record es perfecto para eventos porque los eventos en Kafka son Inmutables (no pueden cambiar)
public record TransferenciaEvent(
        String cuentaOrigen,
        String cuentaDestino,
        BigDecimal monto
) {}