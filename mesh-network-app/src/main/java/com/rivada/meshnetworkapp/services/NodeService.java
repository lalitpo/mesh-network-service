package com.rivada.meshnetworkapp.services;


import com.rivada.meshnetworkapp.entities.Node;
import com.rivada.meshnetworkapp.repositories.ConnectionRepository;
import com.rivada.meshnetworkapp.repositories.NodeRepository;
import com.rivada.meshnetworkapp.requests.CityInfo;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    private final GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);
    @Autowired
    private ConnectionRepository connectionRepository;

    public List<String> getAllNodes() {
        Iterable<Node> iterableNodes = nodeRepository.findAll();
        List<String> allNodes = new ArrayList<>();
        for (Node node : iterableNodes) {
            allNodes.add(node.getName());
        }
        return allNodes;
    }

    public void createNode(CityInfo city) {
        double latitude = city.getCoordinates().get(0);
        double longitude = city.getCoordinates().get(1);

        Assert.isTrue(latitude >= -90 && latitude <= 90,
                "Latitude must be within the range -90 to 90 degrees");

        Assert.isTrue(longitude >= -180 && longitude <= 180,
                "Longitude must be within the range -180 to 180 degrees");

        Point cityPoint = geoFactory.createPoint(new Coordinate(latitude, longitude));

        Optional<Node> existingNode = nodeRepository.findByCoordinates(cityPoint);
        if (existingNode.isPresent()) {
            throw new IllegalArgumentException("Node for this location already exists.");
        } else {
            Node node = new Node();
            node.setName(city.getCityName());
            node.setCoordinates(cityPoint);
            nodeRepository.save(node);
        }
    }

    public void deleteNode(Long nodeId) throws SQLException {
        if (!nodeRepository.existsById(nodeId)) {
            throw new IllegalArgumentException("Node with id " + nodeId + " does not exist.");
        } else if (!connectionRepository.findBySourceOrDestination(nodeId, nodeId).isEmpty()) {
            throw new SQLException("Node with id " + nodeId + " already has connections. Please remove connections first.");
        }
        nodeRepository.deleteById(nodeId);
    }
}
