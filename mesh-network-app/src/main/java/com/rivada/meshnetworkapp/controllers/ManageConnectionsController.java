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

    /**
     * Establishes a connection between two nodes.
     *
     * @param sourceNodeId      the ID of the source node
     * @param destinationNodeId the ID of the destination node
     * @return a ResponseEntity containing the distance of the direct connection (between nodes), or an error message if the connection could not be established
     */
    @PostMapping("setConnection")
    public ResponseEntity<Object> establishConnection(@RequestParam("sourceNodeId") Long sourceNodeId, @RequestParam("destinationNodeId") Long destinationNodeId) {
        try {
            return ResponseEntity.ok().body(connectionService.establishConnection(sourceNodeId, destinationNodeId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Connections could not be established: " + e.getMessage());
        }
    }

    /**
     * Retrieves the connections for a given node.
     *
     * @param nodeId the ID of the node for which to retrieve connections
     * @param sort   the sorting option for the connections (optional, default is 'N' to sort by city name)
     * @return a ResponseEntity containing the list of each connection input node has, or an error message if the connections could not be retrieved
     */
    @GetMapping("getConnections")
    public ResponseEntity<Object> getConnections(@RequestParam Long nodeId, @RequestParam(value = "sort", required = false, defaultValue = "N") char sort) {
        try {
            return ResponseEntity.ok().body(connectionService.getConnections(nodeId, sort));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Connections could not be retrieved for node: " + nodeId + " : " + e.getMessage());
        }
    }

    /**
     * Retrieves the optimal route between two nodes.
     *
     * @param  sourceNodeId      the ID of the source node
     * @param  destinationNodeId the ID of the destination node
     * @return a ResponseEntity containing the optimal route between the nodes, or an error message if the route could not be retrieved
     */
    @GetMapping("getOptimalRoute")
    public ResponseEntity<Object> getOptimalRoute(@RequestParam("sourceNodeId") Long sourceNodeId, @RequestParam("destinationNodeId") Long destinationNodeId) {
        try {
            return ResponseEntity.ok().body(connectionService.getOptimalRoute(sourceNodeId, destinationNodeId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Optimal route could not be retrieved between the nodes:" + e.getMessage());
        }
    }

}