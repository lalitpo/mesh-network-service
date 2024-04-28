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

@Service
public class ConnectionService {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ConnectionRepository connectionRepository;


    public Map<String, Double> getConnections(Long nodeId, char sort) {
        Optional<Node> nodeInfo = nodeRepository.findById(nodeId);
        if (nodeInfo.isPresent()) {
            Node node = nodeInfo.get();
            List<Connection> connectionsInfo = connectionRepository.findBySourceOrDestination(node.getId(), node.getId());
            Map<String, Double> connectionsMap = new LinkedHashMap<>();
            for (Connection connection : connectionsInfo) {
                String nodeName = connection.getSource().equals(node) ? connection.getDestination().getName() : connection.getSource().getName();
                connectionsMap.put(nodeName, connection.getDistance());
            }
            if (sort == 'D') {
                return getConnectionsSortedByDistance(connectionsMap);
            } else {
                return getConnectionsSortedByNode(connectionsMap);
            }
        } else {
            throw new IllegalArgumentException("Node not found.");
        }
    }

    public Route getOptimalRoute(Long sourceNodeId, Long destinationNodeId) {

        Optional<Node> srcNode = nodeRepository.findById(sourceNodeId);
        Optional<Node> destNode = nodeRepository.findById(destinationNodeId);

        if (srcNode.isPresent() && destNode.isPresent()) {
            Optional<Connection> conn1 = connectionRepository.findBySourceAndDestination(srcNode.get().getId(), destNode.get().getId());
            if (conn1.isPresent()) {
                return new Route(List.of(srcNode.get().getName(),
                        destNode.get().getName()),
                        conn1.get().getDistance());
            } else {
                return getShortestRoute(srcNode.get(), destNode.get());// Find the shortest indirect path using Dijkstra's algorithm
            }
        } else {
            throw new IllegalArgumentException("Source or destination node not found.");
        }
    }

    private Route getShortestRoute(Node srcNode, Node destNode) {

        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> predecessors = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(a -> distances.getOrDefault(a, Double.MAX_VALUE)));

        distances.put(srcNode, 0.0);
        queue.offer(srcNode);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            if (currentNode.equals(destNode)) {
                break;
            }

            List<Connection> connections = connectionRepository.findBySourceOrDestination(currentNode.getId(), currentNode.getId());
            for (Connection connection : connections) {
                Node neighbor = connection.getSource().equals(currentNode) ? connection.getDestination() : connection.getSource();
                double distance = distances.getOrDefault(currentNode, Double.MAX_VALUE) + connection.getDistance();
                if (distance < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distances.put(neighbor, distance);
                    predecessors.put(neighbor, currentNode);
                    queue.offer(neighbor);
                }
            }
        }

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

    public Double establishConnection(Long srcId, Long destId) {
        if (Objects.equals(srcId, destId)) {
            throw new IllegalArgumentException("Source and destination nodes cannot be the same.");
        }
        Optional<Node> srcNode = nodeRepository.findById(srcId);
        Optional<Node> destNode = nodeRepository.findById(destId);

        if (srcNode.isPresent() && destNode.isPresent()) {
            Node sourceNode = srcNode.get();
            Node destinationNode = destNode.get();

            checkConnectionLimit(srcId, destId);
            checkExistingConnection(srcId, destId);

            double distance = calculateDistance(sourceNode, destinationNode);

            Connection connection = new Connection();
            connection.setSource(sourceNode);
            connection.setDestination(destinationNode);
            connection.setDistance(distance);

            connectionRepository.save(connection);

            return distance;
        } else {
            throw new IllegalArgumentException("Source or destination node not found.");
        }
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

    private double calculateDistance(Node sourceNode, Node destinationNode) {

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


