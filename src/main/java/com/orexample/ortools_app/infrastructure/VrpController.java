package com.orexample.ortools_app.infrastructure;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.orexample.ortools_app.domain.dto.VrpRequest;


@RestController
@RequestMapping("/api/vrp")
public class VrpController {

    @Autowired
    private VrpService vrpService;

    @PostMapping("/resolver")
    public String resolverVrp(@RequestBody VrpRequest request) {
        return vrpService.resolverVrp(request.getClientes(), request.getVehiculos());
    }
}
