package com.app.neobank.dto;

import java.math.BigDecimal;

public record CuentaDTO(
        Integer clienteId,
        String tipoCuenta, // CAJA_AHORRO o CUENTA_CORRIENTE
        BigDecimal limiteSobregiro,
        BigDecimal tasaInteres
) {}