package com.rivada.meshnetworkapp.services;


import com.rivada.meshnetworkapp.entities.Connection;
import com.rivada.meshnetworkapp.entities.Node;
import com.rivada.meshnetworkapp.repositories.ConnectionRepository;
import com.rivada.meshnetworkapp.repositories.NodeRepository;
import com.rivada.meshnetworkapp.requests.CityInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ConnectionRepository connectionRepository;
    private static final Logger logger = LogManager.getLogger();

    private final GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<String> getAllNodes() {
        Iterable<Node> iterableNodes = nodeRepository.findAll();
        List<String> allNodes = new ArrayList<>();
        for (Node node : iterableNodes) {
            allNodes.add(node.getName());
        }
        return Collections.unmodifiableList(allNodes);
    }

    /**
     * Creates a new node based on the city information provided.
     *
     * @param city the city information containing coordinates
     */
    public void createNode(CityInfo city) {
        double latitude = city.getCoordinates().get(0);
        double longitude = city.getCoordinates().get(1);

        // Validate latitude and longitude range
        Assert.isTrue(latitude >= -90 && latitude <= 90,
                "Latitude must be within the range -90 to 90 degrees");

        Assert.isTrue(longitude >= -180 && longitude <= 180,
                "Longitude must be within the range -180 to 180 degrees");

        Point cityPoint = geoFactory.createPoint(new Coordinate(latitude, longitude));

        // Check if a node with the same coordinates already exists
        Optional<Node> existingNode = nodeRepository.findByCoordinates(cityPoint);
        if (existingNode.isPresent()) {
            throw new IllegalArgumentException("Node for this location already exists.");
        } else {
            nodeRepository.save(new Node(city.getCityName(), cityPoint));
        }
    }

    /**
     * Deletes a node with the given ID.
     *
     * @param nodeId the ID of the node to delete
     * @throws IllegalArgumentException if the node with the given ID does not exist
     * @throws SQLException             if the node with the given ID already has connections
     * @throws SQLException             if there is an error deleting the node
     */
    public void deleteNode(Long nodeId) throws SQLException {
        if (!nodeRepository.existsById(nodeId)) {
            throw new IllegalArgumentException("Node with id " + nodeId + " does not exist.");
        }

        // Check if the node has any connections
        List<Connection> connections = connectionRepository.findBySourceOrDestination(nodeId, nodeId);
        if (!connections.isEmpty()) {
            throw new SQLException("Node with id " + nodeId + " already has connections. Please remove connections first.");
        }
        logger.debug("Deleting node with id {}", nodeId);
        nodeRepository.deleteById(nodeId);
    }
}
