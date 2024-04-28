package com.rivada.meshnetworkapp.services;

import com.rivada.meshnetworkapp.entities.Connection;
import com.rivada.meshnetworkapp.entities.Node;
import com.rivada.meshnetworkapp.repositories.ConnectionRepository;
import com.rivada.meshnetworkapp.repositories.NodeRepository;
import com.rivada.meshnetworkapp.response.Route;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConnectionService {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    /**
     * Retrieves the connections for a given node and sorts them based on the specified criteria.
     *
     * @param nodeId the ID of the node for which to retrieve connections
     * @param sort   the sorting option for the connections ('D' for distance, any other character to sort by node/city name)
     * @return a map containing the connections sorted by either distance or node/city name
     * @throws IllegalArgumentException if the specified node is not found
     */
    public Map<String, Double> getConnections(Long nodeId, char sort) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found."));

        List<Connection> connectionsInfo = connectionRepository.findBySourceOrDestination(node.getId(), node.getId());
        Map<String, Double> connectionsMap = connectionsInfo.stream()
                .collect(Collectors.toMap(
                        connection -> connection.getSource().equals(node) ? connection.getDestination().getName() : connection.getSource().getName(),
                        Connection::getDistance,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));

        return sort == 'D' ? getConnectionsSortedByDistance(connectionsMap) : getConnectionsSortedByNode(connectionsMap);
    }

    public Route getOptimalRoute(Long sourceNodeId, Long destinationNodeId) {

        Optional<Node> srcNode = nodeRepository.findById(sourceNodeId);
        Optional<Node> destNode = nodeRepository.findById(destinationNodeId);

        if (srcNode.isPresent() && destNode.isPresent()) {
            // Check if a direct connection is present
            Optional<Connection> conn1 = connectionRepository.findBySourceAndDestination(srcNode.get().getId(), destNode.get().getId());
            // Direct connection is present
            if (conn1.isPresent()) {
                return new Route(List.of(srcNode.get().getName(),
                        destNode.get().getName()),
                        conn1.get().getDistance());
            } else {
                // Find the shortest indirect path using Dijkstra's algorithm
                return getShortestRoute(srcNode.get(), destNode.get());
            }
        } else {
            throw new IllegalArgumentException("Source or destination node not found.");
        }
    }

    public Route getShortestRoute(Node srcNode, Node destNode) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> predecessors = new HashMap<>();

        // Priority queue to store the nodes in the order of their distances from the source node
        PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> {
            double distanceA = distances.getOrDefault(a, Double.MAX_VALUE);
            double distanceB = distances.getOrDefault(b, Double.MAX_VALUE);
            return Double.compare(distanceA, distanceB);
        });

        // Set the distance of the source node to 0 and add it to the priority queue
        distances.put(srcNode, 0.0);
        queue.offer(srcNode);

        Set<Node> visited = new HashSet<>();                                // Set to store the visited nodes

        // Perform Dijkstra's algorithm to find the shortest path
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            // If the current node is the destination node, we have found the shortest path
            if (currentNode.equals(destNode)) {
                break;
            }

            // Get the connections from the current node to its neighbors
            List<Connection> connections = connectionRepository.findBySourceOrDestination(currentNode.getId(), currentNode.getId());
            for (Connection connection : connections) {
                Node neighbor = connection.getSource().equals(currentNode) ? connection.getDestination() : connection.getSource();
                // If the neighbor has already been visited, skip it
                if (visited.contains(neighbor)) {
                    continue;
                }

                // Calculate the distance from the source node to the neighbor through the current node
                double distance = distances.getOrDefault(currentNode, Double.MAX_VALUE) + connection.getDistance();
                if (distance < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distances.put(neighbor, distance);
                    predecessors.put(neighbor, currentNode);
                    queue.offer(neighbor);
                }
            }
            // Mark the current node as visited
            visited.add(currentNode);
        }

        // If a path from the source node to the destination node exists, construct the shortest route
        if (predecessors.containsKey(destNode)) {
            List<String> path = new ArrayList<>();
            Node current = destNode;
            while (current != null) {
                path.add(0, current.getName());
                current = predecessors.get(current);
            }
            double totalDistance = distances.get(destNode);
            return new Route(path, totalDistance);
        } else {
            throw new IllegalArgumentException("No indirect connection found between the given nodes.");
        }
    }

    /**
     * Establishes a connection between two nodes.
     *
     * @param srcId  The ID of the source node.
     * @param destId The ID of the destination node.
     * @return The distance of the connection.
     * @throws IllegalArgumentException If the source and destination nodes are the same, or if either the source or
     *                                  destination node is not found, or if the connection limit is exceeded for either the source or destination node,
     *                                  or if the connection already exists between the source and destination nodes.
     */
    public Double establishConnection(Long srcId, Long destId) {

        // Check if source and destination nodes are the same
        if (Objects.equals(srcId, destId)) {
            throw new IllegalArgumentException("Source and destination nodes cannot be the same.");
        }
        Node sourceNode = nodeRepository.findById(srcId)
                .orElseThrow(() -> new IllegalArgumentException("Source node not found."));
        Node destinationNode = nodeRepository.findById(destId)
                .orElseThrow(() -> new IllegalArgumentException("Destination node not found."));

        // Check if the connection limit is exceeded for either the source or destination node
        checkConnectionLimit(srcId, destId);
        // Check if the connection already exists between the source and destination nodes
        checkExistingConnection(srcId, destId);

        double distance = calculateDistance(sourceNode, destinationNode);

        Connection connection = new Connection(sourceNode, destinationNode, distance);
        connectionRepository.save(connection);
        return distance;
    }

    private void checkExistingConnection(Long srcId, Long destId) {
        Optional<Connection> connCount = connectionRepository.findBySourceAndDestination(srcId, destId);
        if (connCount.isPresent()) {
            throw new IllegalArgumentException("There is already a connection exists between the nodes.");
        }
    }

    private void checkConnectionLimit(Long sourceNode, Long destinationNode) {
        int sourceConnCount = connectionRepository.countById(sourceNode);
        int destConnCount = connectionRepository.countById(destinationNode);
        if (sourceConnCount == 4 || destConnCount == 4) {
            throw new IllegalArgumentException("Connection limit exceeded for source or destination node.");
        }
    }

    /**
     * Calculates the distance between two nodes based using the Haversine formula.
     *
     * @param sourceNode The source node.
     * @param destinationNode The destination node.
     * @return The distance between the two nodes in kilometers.
     */
    public double calculateDistance(Node sourceNode, Node destinationNode) {

        Point sourcePoint = sourceNode.getCoordinates();
        Point destPoint = destinationNode.getCoordinates();

        double lat1Rad = Math.toRadians(sourcePoint.getY());
        double lat2Rad = Math.toRadians(destPoint.getY());
        double lon1Rad = Math.toRadians(sourcePoint.getX());
        double lon2Rad = Math.toRadians(destPoint.getX());

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);

        return Math.sqrt(x * x + y * y) * 6371;
    }

    private LinkedHashMap<String, Double> getConnectionsSortedByDistance(Map<String, Double> hashMap) {

        return hashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll
                );
    }

    private LinkedHashMap<String, Double> getConnectionsSortedByNode(Map<String, Double> hashMap) {
        return hashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll
                );
    }
}


