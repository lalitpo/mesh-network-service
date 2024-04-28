package com.rivada.meshnetworkapp.controllers;

import com.rivada.meshnetworkapp.requests.CityInfo;
import com.rivada.meshnetworkapp.services.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nodes")
public class ManageNodesController {

    @Autowired
    private NodeService nodeService;

    /**
     * Retrieves a list of all node names.
     *
     * @return A ResponseEntity containing a list of city names (names of nodes).
     */
    @GetMapping("/getAllNodes")
    public ResponseEntity<List<String>> getAllNodes() {
        return ResponseEntity.ok().body(nodeService.getAllNodes());
    }

    /**
     * Creates a new node with the given city details.
     *
     * @param cityDetails the details of the city to create a node for
     */
    @PostMapping("/createNode")
    public ResponseEntity<String> createNode(@RequestBody CityInfo cityDetails) {
        try {
            nodeService.createNode(cityDetails);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Node could not be created: " + e.getMessage());
        }
        return ResponseEntity.ok().body("Node created successfully");


    }

    /**
     * Deletes a node with the given ID.
     *
     * @param nodeId the ID of the node to delete
     */
    @DeleteMapping("/deleteNode")
    public ResponseEntity<String> deleteNode(@RequestParam Long nodeId) {
        try {
            nodeService.deleteNode(nodeId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Node could not be deleted: " + e.getMessage());
        }
        return ResponseEntity.ok().body("Node deleted successfully.");
    }
}

