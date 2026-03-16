package com.app.neobank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransaccionResponseDTO(
        Integer id,
        LocalDateTime fecha,
        String tipoTransaccion,
        BigDecimal monto,
        String descripcion,
        String cuentaOrigen,
        String cuentaDestino
) {}