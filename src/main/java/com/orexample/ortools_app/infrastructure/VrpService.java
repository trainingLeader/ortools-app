package com.orexample.ortools_app.infrastructure;

import com.google.ortools.constraintsolver.*;
import com.orexample.ortools_app.domain.entity.Cliente;
import com.orexample.ortools_app.domain.entity.Vehiculo;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VrpService {
    

    static {
        SpringBootOrToolsNativeLoader.loadNativeLibraries();
    }

    public String resolverVrp(List<Cliente> clientes, List<Vehiculo> vehiculos) {
        // 1. Preparar los datos
        int numVehiculos = vehiculos.size();
        int numClientes = clientes.size();
        int[][] distanceMatrix = crearMatrizDistancias(clientes);

        // 2. Configurar el Administrador y el Modelo de Ruteo
        RoutingIndexManager manager = new RoutingIndexManager(distanceMatrix.length, numVehiculos, 0);
        RoutingModel routing = new RoutingModel(manager);

        // 3. Definir la función de distancia
        int transitCallbackIndex = routing.registerTransitCallback((fromIndex, toIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            return distanceMatrix[fromNode][toNode];
        });
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // 4. Configurar restricciones de capacidad
        int[] demandas = clientes.stream().mapToInt(Cliente::getDemanda).toArray();
        long[] capacidades = vehiculos.stream().mapToLong(Vehiculo::getCapacidad).toArray();
        int demandCallbackIndex = routing.registerUnaryTransitCallback(index -> demandas[manager.indexToNode(index)]);
        routing.addDimensionWithVehicleCapacity(demandCallbackIndex, 0, capacidades, true, "Capacidad");

        // 5. Configurar parámetros de búsqueda
        RoutingSearchParameters searchParameters = RoutingSearchParameters.newBuilder()
            .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
            .build();

        // 6. Resolver
        Assignment solution = routing.solveWithParameters(searchParameters);

        // 7. Imprimir la solución
        if (solution != null) {
            return printSolution(routing, manager, solution, vehiculos);
        } else {
            return "No se encontró una solución.";
        }
    }

    private int[][] crearMatrizDistancias(List<Cliente> clientes) {
        int numClientes = clientes.size();
        int[][] distanceMatrix = new int[numClientes + 1][numClientes + 1]; // +1 para el depósito
        for (int i = 0; i < numClientes; i++) {
            for (int j = 0; j < numClientes; j++) {
                if (i != j) {
                    distanceMatrix[i][j] = calcularDistancia(clientes.get(i), clientes.get(j));
                }
            }
        }
        return distanceMatrix;
    }

    private int calcularDistancia(Cliente a, Cliente b) {
        // Implementa la lógica de cálculo de distancia entre puntos a y b
        return (int) Math.sqrt(Math.pow(a.getLatitud() - b.getLatitud(), 2)
                + Math.pow(a.getLongitud() - b.getLongitud(), 2));
    }

    private String printSolution(RoutingModel routing, RoutingIndexManager manager,
                                 Assignment solution, List<Vehiculo> vehiculos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vehiculos.size(); i++) {
            sb.append("Ruta para el vehículo ").append(i).append(":\n");
            long index = routing.start(i);
            while (!routing.isEnd(index)) {
                sb.append(manager.indexToNode((int) index)).append(" -> ");
                index = solution.value(routing.nextVar(index));
            }
            sb.append(manager.indexToNode((int) index)).append("\n");
        }
        return sb.toString();
    }
}