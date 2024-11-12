package com.orexample.ortools_app.domain.dto;

import java.util.List;

import com.orexample.ortools_app.domain.entity.Cliente;
import com.orexample.ortools_app.domain.entity.Vehiculo;


public class VrpRequest {
    private List<Cliente> clientes;
    private List<Vehiculo> vehiculos;
    public List<Cliente> getClientes() {
        return clientes;
    }
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }
    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    
}
