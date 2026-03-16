package com.app.neobank.service;

import com.app.neobank.dto.OperacionDTO;
import com.app.neobank.modelo.CajaAhorro;
import com.app.neobank.modelo.Cliente;
import com.app.neobank.repository.ClienteRepository;
import com.app.neobank.repository.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private CuentaService cuentaService;

    @Test
    void depositar_DebeAumentarSaldo_CuandoCuentaExiste() {
        // ARRANGE
        String numeroCuenta = "UUID-FALSO-123";
        OperacionDTO operacion = new OperacionDTO(new BigDecimal("500.00"));

        // cliente y una cuenta ficticia en memoria
        Cliente cliente = new Cliente("Ana", "Gomez", "12345678", "ana@correo.com");
        CajaAhorro cuentaSimulada = new CajaAhorro(cliente, new BigDecimal("0.05"));
        cuentaSimulada.setNumeroCuenta(numeroCuenta);
        cuentaSimulada.setSaldo(new BigDecimal("1000.00")); // Saldo inicial

        // Cuando el servicio pida buscar esta cuenta devuelve nuestra cuenta simulada
        when(cuentaRepository.findById(numeroCuenta)).thenReturn(Optional.of(cuentaSimulada));

        // Cuando el servicio intente guardar devuelve la misma cuenta
        when(cuentaRepository.save(any(CajaAhorro.class))).thenReturn(cuentaSimulada);

        // ACT
        // Llamamos al metodo que queremos probar
        var cuentaActualizada = cuentaService.depositar(numeroCuenta, operacion);

        // ASSERT
        // Comprobamos que el servicio sumó correctamente
        assertEquals(new BigDecimal("1500.00"), cuentaActualizada.getSaldo(), "El saldo no se sumó correctamente");

        // Comprobamos que el servicio intentó guardar el cambio en la base de datos 1 vez
        verify(cuentaRepository, times(1)).save(cuentaSimulada);
    }
}