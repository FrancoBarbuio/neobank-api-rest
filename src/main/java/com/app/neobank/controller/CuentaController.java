package com.app.neobank.controller;

import com.app.neobank.dto.CuentaDTO;
import com.app.neobank.dto.OperacionDTO;
import com.app.neobank.dto.TransaccionResponseDTO;
import com.app.neobank.dto.TransferenciaRequestDTO;
import com.app.neobank.modelo.Cuenta;
import com.app.neobank.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public Page<Cuenta> listarCuentas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return cuentaService.obtenerTodasLasCuentas(page, size);
    }

    @PostMapping
    public Cuenta crearNuevaCuenta(@RequestBody CuentaDTO cuentaDTO) {
        return cuentaService.crearCuenta(cuentaDTO);
    }

    @PostMapping("/{numeroCuenta}/depositar")
    public Cuenta depositar(@PathVariable String numeroCuenta, @RequestBody OperacionDTO operacionDTO) {
        return cuentaService.depositar(numeroCuenta, operacionDTO);
    }

    @PostMapping("/{numeroCuenta}/retirar")
    public Cuenta retirar(@PathVariable String numeroCuenta, @RequestBody OperacionDTO operacionDTO) {
        return cuentaService.retirar(numeroCuenta, operacionDTO);
    }

    @PostMapping("/transferir")
    public TransaccionResponseDTO transferir(@Valid @RequestBody TransferenciaRequestDTO transferenciaDTO) {
        return cuentaService.realizarTransferencia(transferenciaDTO);
    }

}