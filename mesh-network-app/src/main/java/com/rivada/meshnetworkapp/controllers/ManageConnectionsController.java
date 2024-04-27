package com.rivada.meshnetworkapp.controllers;


import com.rivada.meshnetworkapp.entities.Connection;
import com.rivada.meshnetworkapp.entities.Route;
import com.rivada.meshnetworkapp.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/connections")
public class ManageConnectionsController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("setConnection")
    public Double establishConnection(@RequestParam("sourceNodeId") String sourceNodeId, @RequestParam("destinationNodeId") String destinationNodeId) {
        return connectionService.establishConnection(sourceNodeId, destinationNodeId);
    }

    @GetMapping("getConnections")
    public List<Connection> getConnections(@RequestParam String nodeId) {
        return connectionService.getConnections(nodeId);
    }

    @GetMapping("getOptimalRoute")
    public Route getOptimalRoute(@RequestParam("sourceNodeId") String sourceNodeId, @RequestParam("destinationNodeId") String destinationNodeId) {
        return connectionService.getOptimalRoute(sourceNodeId, destinationNodeId);
    }

}