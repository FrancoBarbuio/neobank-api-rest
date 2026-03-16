package com.app.neobank.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CUENTA_CORRIENTE")
public class CuentaCorriente extends Cuenta {

    @Column(name = "limite_sobregiro", precision = 15, scale = 2)
    private BigDecimal limiteSobregiro;

    public CuentaCorriente() {}

    public CuentaCorriente(Cliente titular, BigDecimal limiteSobregiro) {
        super(titular);
        this.limiteSobregiro = limiteSobregiro;
    }

    public BigDecimal getLimiteSobregiro() { return limiteSobregiro; }
    public void setLimiteSobregiro(BigDecimal limiteSobregiro) { this.limiteSobregiro = limiteSobregiro; }

    @Override
    public void retirar(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor a cero.");
        }

        BigDecimal fondosDisponibles = getSaldo().add(limiteSobregiro);

        if (monto.compareTo(fondosDisponibles) > 0) {
            throw new IllegalStateException("Operación rechazada: Fondos insuficientes y límite de sobregiro excedido.");
        }

        restarSaldo(monto);
    }
}