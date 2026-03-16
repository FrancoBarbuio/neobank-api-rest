package com.app.neobank.controller;

import com.app.neobank.modelo.Cliente;
import com.app.neobank.repository.ClienteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Cliente> obtenerTodosLosClientes() {
        return repository.findAll();
    }

    @PostMapping
    public Cliente crearCliente(@RequestBody Cliente nuevoCliente) {
        return repository.save(nuevoCliente);
    }


}