package com.app.neobank.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cuentas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_cuenta", discriminatorType = DiscriminatorType.STRING)
public abstract class Cuenta {

    @Id
    @Column(name = "numero_cuenta", length = 36)
    private String numeroCuenta;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente titular;

    public Cuenta() {}

    public Cuenta(Cliente titular) {
        this.numeroCuenta = UUID.randomUUID().toString();
        this.saldo = BigDecimal.ZERO;
        this.titular = titular;
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public Cliente getTitular() { return titular; }
    public void setTitular(Cliente titular) { this.titular = titular; }

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    private List<Transaccion> historial = new ArrayList<>();

    protected void registrarOperacion(TipoTransaccion tipo, BigDecimal monto, String descripcion, Cuenta destino) {
        Transaccion t = new Transaccion(LocalDateTime.now(), tipo, monto, descripcion, this, destino);
        this.historial.add(t);
    }

    public void depositar(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor a cero.");
        }
        this.saldo = this.saldo.add(monto);
        registrarOperacion(TipoTransaccion.DEPOSITO, monto, "Depósito por ventanilla", null);
    }

    protected void restarSaldo(BigDecimal monto) {
        this.saldo = this.saldo.subtract(monto);
        registrarOperacion(TipoTransaccion.RETIRO, monto, "Retiro de fondos", null);
    }

    public void enviarTransferencia(BigDecimal monto, Cuenta destino, String descripcion) {
        this.saldo = this.saldo.subtract(monto);
        registrarOperacion(TipoTransaccion.TRANSFERENCIA, monto, descripcion, destino);
    }

    public void recibirTransferencia(BigDecimal monto, Cuenta origen, String descripcion) {
        this.saldo = this.saldo.add(monto);
        registrarOperacion(TipoTransaccion.TRANSFERENCIA, monto, descripcion, origen);
    }

    public abstract void retirar(BigDecimal monto);

    public List<Transaccion> getHistorial() {
        return historial;
    }
    public void setHistorial(List<Transaccion> historial) {
        this.historial = historial;
    }
}