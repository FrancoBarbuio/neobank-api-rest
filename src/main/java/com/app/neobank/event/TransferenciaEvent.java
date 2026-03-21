package com.app.neobank.event;

import java.math.BigDecimal;

public record TransferenciaEvent(
        String cuentaOrigen,
        String cuentaDestino,
        BigDecimal monto
) {}