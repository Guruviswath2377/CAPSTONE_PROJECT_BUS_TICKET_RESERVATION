package com.example.busreservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.busreservation.model.*;
import com.example.busreservation.repository.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
public class BusRouteController {

    @Autowired
    private BusRepository busRepo;

    @Autowired
    private RouteRepository routeRepo;

    @PostMapping("/buses")
    @PreAuthorize("hasRole('ADMIN')")
    public Bus createBus(@RequestBody Bus bus) {
        return busRepo.save(bus);
    }

    @PostMapping("/routes")
    @PreAuthorize("hasRole('ADMIN')")
    public Route createRoute(@RequestBody Route route) {
        return routeRepo.save(route);
    } // ← this brace was missing

    @GetMapping("/buses")
    @PreAuthorize("hasRole('ADMIN')")
    public java.util.List<Bus> listBuses() {
        return busRepo.findAll();
    }

    @GetMapping("/routes")
    @PreAuthorize("hasRole('ADMIN')")
    public java.util.List<Route> listRoutes() {
        return routeRepo.findAll();
    }
}
