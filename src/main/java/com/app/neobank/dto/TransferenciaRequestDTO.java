package com.app.neobank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(

        @NotBlank(message = "La cuenta origen no puede estar vacía")
        String cuentaOrigen,

        @NotBlank(message = "La cuenta destino no puede estar vacía")
        String cuentaDestino,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto a transferir debe ser mayor a cero")
        BigDecimal monto,

        String descripcion
) {}