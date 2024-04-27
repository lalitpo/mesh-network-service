package com.rivada.meshnetworkapp.services;


import com.rivada.meshnetworkapp.entities.Node;
import com.rivada.meshnetworkapp.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    public List<Node> getAllNodes() {
        Iterable<Node> iterableNodes = nodeRepository.findAll();
        List<Node> allNodes = new ArrayList<>();
        for (Node node : iterableNodes) {
            allNodes.add(node);
        }
        return allNodes;
    }

    public void createNode(Node node) {
        nodeRepository.save(node);
    }

    public void deleteNode(String nodeId) {
        nodeRepository.deleteById(nodeId);
    }
}
