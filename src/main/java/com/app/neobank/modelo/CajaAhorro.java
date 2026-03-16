package com.app.neobank.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CAJA_AHORRO")
public class CajaAhorro extends Cuenta {

    @Column(name = "tasa_interes", precision = 5, scale = 4)
    private BigDecimal tasaInteres;

    public CajaAhorro() {}

    public CajaAhorro(Cliente titular, BigDecimal tasaInteres) {
        super(titular);
        this.tasaInteres = tasaInteres;
    }

    public BigDecimal getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(BigDecimal tasaInteres) { this.tasaInteres = tasaInteres; }

    @Override
    public void retirar(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor a cero.");
        }
        if (getSaldo().compareTo(monto) < 0) {
            throw new IllegalStateException("Saldo insuficiente para realizar el retiro en la Caja de Ahorro.");
        }

        restarSaldo(monto);
    }
}