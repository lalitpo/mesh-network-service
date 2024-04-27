package com.rivada.meshnetworkapp.services;

import com.rivada.meshnetworkapp.entities.Connection;
import com.rivada.meshnetworkapp.entities.Node;
import com.rivada.meshnetworkapp.entities.Route;
import com.rivada.meshnetworkapp.repositories.ConnectionRepository;
import com.rivada.meshnetworkapp.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConnectionService {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ConnectionRepository connectionRepository;


    public List<Connection> getConnections(String nodeId) {
        Optional<Node> nodeInfo = nodeRepository.findById(nodeId);
        if (nodeInfo.isPresent()) {
            Node node = nodeInfo.get();
            return connectionRepository.findBySourceOrDestination(node, node);
        } else {
            throw new IllegalArgumentException("Source node not found");
        }
    }

    public Route getOptimalRoute(String sourceNodeId, String destinationNodeId) {

        Optional<Node> srcNode = nodeRepository.findById(sourceNodeId);
        Optional<Node> destNode = nodeRepository.findById(destinationNodeId);

        if (srcNode.isPresent() && destNode.isPresent()) {
            Optional<Connection> conn1 = connectionRepository.findBySourceAndDestination(srcNode.get().getName(), destNode.get().getName());
            if (conn1.isPresent()) {
                Route route = new Route();
                route.setDistance(conn1.get().getDistance());
                route.setPath(List.of(srcNode.get(), destNode.get()));
                return route;
            } else {
                // Find the shortest indirect path using Dijkstra's algorithm
                Map<Node, Double> distances = new HashMap<>();
                Map<Node, Node> predecessors = new HashMap<>();
                PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(a -> distances.getOrDefault(a, Double.MAX_VALUE)));

                distances.put(srcNode.get(), 0.0);
                queue.offer(srcNode.get());

                while (!queue.isEmpty()) {
                    Node currentNode = queue.poll();
                    if (currentNode.equals(destNode.get())) {
                        break;
                    }

                    List<Connection> connections = connectionRepository.findBySourceOrDestination(currentNode, currentNode);
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

                if (predecessors.containsKey(destNode.get())) {
                    List<Node> path = new ArrayList<>();
                    Node current = destNode.get();
                    while (current != null) {
                        path.add(0, current);
                        current = predecessors.get(current);
                    }
                    double totalDistance = distances.get(destNode.get());
                    Route route = new Route();
                    route.setDistance(totalDistance);
                    route.setPath(path);
                    return route;
                } else {
                    throw new IllegalArgumentException("No indirect connection found between the given nodes.");
                }
            }
        } else {
            throw new IllegalArgumentException("Source or destination node not found.");
        }
    }


    public Double establishConnection(String srcId, String destId) {

        Optional<Node> srcNode = nodeRepository.findById(srcId);
        Optional<Node> destNode = nodeRepository.findById(destId);

        if (srcNode.isPresent() && destNode.isPresent()) {
            Node sourceNode = srcNode.get();
            Node destinationNode = destNode.get();

            Point sourcePoint = sourceNode.getCoordinates();
            Point destPoint = destinationNode.getCoordinates();

            double distance = connectionRepository.calculateDistance(sourcePoint, destPoint);

            Connection connection = new Connection();
            connection.setSource(sourceNode);
            connection.setDestination(destinationNode);
            connection.setDistance(distance);

            connectionRepository.save(connection);

            return distance;
        } else {
            throw new IllegalArgumentException("Source or destination node not found");
        }
    }

   /* public List<Connection> getAllConnectionsSortedByDistance() {

    }

    public List<Connection> getAllConnectionsSortedByNode() {
    }*/
}


