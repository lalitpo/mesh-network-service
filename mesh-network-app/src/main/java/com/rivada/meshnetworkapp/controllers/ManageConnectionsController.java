package com.rivada.meshnetworkapp.controllers;


import com.rivada.meshnetworkapp.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/connections")
public class ManageConnectionsController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("setConnection")
    public ResponseEntity<Object> establishConnection(@RequestParam("sourceNodeId") Long sourceNodeId, @RequestParam("destinationNodeId") Long destinationNodeId) {
        try {
            return ResponseEntity.ok().body(connectionService.establishConnection(sourceNodeId, destinationNodeId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Connections could not be established: " + e.getMessage());
        }
    }

    @GetMapping("getConnections")
    public ResponseEntity<Object> getConnections(@RequestParam Long nodeId, @RequestParam(value = "sort", required = false, defaultValue = "N") char sort) {
        try {
            return ResponseEntity.ok().body(connectionService.getConnections(nodeId, sort));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Connections could not be retrieved for node: " + nodeId + " : " + e.getMessage());
        }
    }

    @GetMapping("getOptimalRoute")
    public ResponseEntity<Object> getOptimalRoute(@RequestParam("sourceNodeId") Long sourceNodeId, @RequestParam("destinationNodeId") Long destinationNodeId) {
        try {
            return ResponseEntity.ok().body(connectionService.getOptimalRoute(sourceNodeId, destinationNodeId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Optimal route could not be retrieved between the nodes:" + e.getMessage());
        }
    }

}