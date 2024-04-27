package com.rivada.meshnetworkapp.controllers;

import com.rivada.meshnetworkapp.entities.Node;
import com.rivada.meshnetworkapp.services.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nodes")
public class ManageNodesController {

    @Autowired
    private NodeService nodeService;

    @GetMapping("/getAllNodes")
    public List<Node> getAllNodes() {
        return nodeService.getAllNodes();
    }

    @PostMapping("/createNode")
    public void createNode(@RequestBody Node node) {
        nodeService.createNode(node);
    }

    @DeleteMapping("/deleteNode")
    public void deleteNode(@RequestParam String nodeId) {
        nodeService.deleteNode(nodeId);
    }
}

