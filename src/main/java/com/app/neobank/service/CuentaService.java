package com.app.neobank.service;

import com.app.neobank.dto.CuentaDTO;
import com.app.neobank.dto.OperacionDTO;
import com.app.neobank.dto.TransaccionResponseDTO;
import com.app.neobank.dto.TransferenciaRequestDTO;
import com.app.neobank.modelo.*;
import com.app.neobank.repository.ClienteRepository;
import com.app.neobank.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.app.neobank.event.TransferenciaEvent;
import com.app.neobank.service.TransferenciaProducer;

@Service
public class CuentaService {

    private static final Logger log = LoggerFactory.getLogger(CuentaService.class);

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    // 1. Agregamos la variable del Productor
    private final TransferenciaProducer transferenciaProducer;

    // 2. Lo agregamos al constructor para que Spring lo inyecte
    public CuentaService(CuentaRepository cuentaRepository,
                         ClienteRepository clienteRepository,
                         TransferenciaProducer transferenciaProducer) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
        this.transferenciaProducer = transferenciaProducer;
    }

    public Page<Cuenta> obtenerTodasLasCuentas(int page, int size) {
        log.info("Consultando lista de cuentas - Página: {}, Tamaño: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        return cuentaRepository.findAll(pageable);
    }

    @Transactional
    public TransaccionResponseDTO realizarTransferencia(TransferenciaRequestDTO dto) {
        log.info("Iniciando transferencia de {} hacia {} por el monto de ${}",
                dto.cuentaOrigen(), dto.cuentaDestino(), dto.monto());

        if (dto.cuentaOrigen().equals(dto.cuentaDestino())) {
            log.warn("Transferencia rechazada: Intento de transferir a la misma cuenta.");
            throw new IllegalArgumentException("No puedes transferir dinero a la misma cuenta.");
        }

        Cuenta origen = cuentaRepository.findById(dto.cuentaOrigen())
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada."));
        Cuenta destino = cuentaRepository.findById(dto.cuentaDestino())
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada."));

        if (origen instanceof CajaAhorro ca && ca.getSaldo().compareTo(dto.monto()) < 0) {
            log.error("Transferencia fallida: Saldo insuficiente en la cuenta {}", origen.getNumeroCuenta());
            throw new IllegalStateException("Saldo insuficiente en la Caja de Ahorro para transferir.");
        } else if (origen instanceof CuentaCorriente cc) {
            BigDecimal fondosDisponibles = cc.getSaldo().add(cc.getLimiteSobregiro());
            if (dto.monto().compareTo(fondosDisponibles) > 0) {
                log.error("Transferencia fallida: Sobregiro excedido en la cuenta {}", origen.getNumeroCuenta());
                throw new IllegalStateException("Fondos y sobregiro insuficientes en Cuenta Corriente.");
            }
        }

        origen.enviarTransferencia(dto.monto(), destino, dto.descripcion());
        destino.recibirTransferencia(dto.monto(), origen, dto.descripcion());

        cuentaRepository.save(origen);
        cuentaRepository.save(destino);

        log.info("Transferencia exitosa completada.");

        // DISPARO A KAFKA
        TransferenciaEvent eventoKafka = new TransferenciaEvent(
                origen.getNumeroCuenta(),
                destino.getNumeroCuenta(),
                dto.monto()
        );
        transferenciaProducer.publicarEventoDeTransferencia(eventoKafka);

        return new TransaccionResponseDTO(
                null, java.time.LocalDateTime.now(), "TRANSFERENCIA",
                dto.monto(), dto.descripcion(), origen.getNumeroCuenta(), destino.getNumeroCuenta()
        );
    }

    public Cuenta crearCuenta(CuentaDTO dto) {
        log.info("Iniciando creación de cuenta tipo {} para el cliente ID {}", dto.tipoCuenta(), dto.clienteId());

        Cliente titular = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Error: Cliente no encontrado con ID " + dto.clienteId()));

        Cuenta nuevaCuenta;

        if ("CAJA_AHORRO".equalsIgnoreCase(dto.tipoCuenta())) {
            nuevaCuenta = new CajaAhorro(titular, dto.tasaInteres());
        } else if ("CUENTA_CORRIENTE".equalsIgnoreCase(dto.tipoCuenta())) {
            nuevaCuenta = new CuentaCorriente(titular, dto.limiteSobregiro());
        } else {
            log.error("Tipo de cuenta no válido recibido: {}", dto.tipoCuenta());
            throw new IllegalArgumentException("Tipo de cuenta no válido. Use CAJA_AHORRO o CUENTA_CORRIENTE");
        }

        Cuenta cuentaGuardada = cuentaRepository.save(nuevaCuenta);
        log.info("Cuenta creada exitosamente con el número: {}", cuentaGuardada.getNumeroCuenta());

        return cuentaGuardada;
    }

    @Transactional
    public Cuenta depositar(String numeroCuenta, OperacionDTO operacion) {
        log.info("Iniciando depósito de ${} en la cuenta {}", operacion.monto(), numeroCuenta);

        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.depositar(operacion.monto());
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        log.info("Depósito exitoso. Nuevo saldo: ${}", cuentaActualizada.getSaldo());
        return cuentaActualizada;
    }

    @Transactional
    public Cuenta retirar(String numeroCuenta, OperacionDTO operacion) {
        log.info("Iniciando retiro de ${} en la cuenta {}", operacion.monto(), numeroCuenta);

        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.retirar(operacion.monto());
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        log.info("Retiro exitoso. Nuevo saldo: ${}", cuentaActualizada.getSaldo());
        return cuentaActualizada;
    }

}