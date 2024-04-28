package com.rivada.meshnetworkapp;

import com.rivada.meshnetworkapp.entities.Connection;
import com.rivada.meshnetworkapp.entities.Node;
import com.rivada.meshnetworkapp.repositories.ConnectionRepository;
import com.rivada.meshnetworkapp.repositories.NodeRepository;
import com.rivada.meshnetworkapp.response.Route;
import com.rivada.meshnetworkapp.services.ConnectionService;
import com.rivada.meshnetworkapp.services.NodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ManageConnectionServiceTests {
    private final GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final Node node1 = new Node("Node 1", geoFactory.createPoint(new Coordinate(-74.0060, 40.7128)));
    private final Node node2 = new Node("Node 2", geoFactory.createPoint(new Coordinate(31.2357, 30.0444)));
    private final Node node3 = new Node("Node 3", geoFactory.createPoint(new Coordinate(3.3792, 6.5244)));
    private final Node node4 = new Node("Node 4", geoFactory.createPoint(new Coordinate(-99.1332, 19.4326)));
    private final Node node5 = new Node("Node 5", geoFactory.createPoint(new Coordinate(-43.1729, -22.9068)));
    private final Long nodeId = 1L;
    @Mock
    private NodeRepository nodeRepositoryMock;
    @Mock
    private ConnectionRepository connectionRepositoryMock;
    @InjectMocks
    private ConnectionService connectionServiceMock;
    @InjectMocks
    private NodeService nodeServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenNodeNotFoundThenThrowException() {
        when(nodeRepositoryMock.findById(nodeId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> connectionServiceMock.getConnections(nodeId, 'D'),
                "Node not found.");
    }

    @Test
    void whenSortByDistanceThenReturnSortedConnections() throws NoSuchFieldException, IllegalAccessException {

        Field idField = Node.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(node1, nodeId);

        when(nodeRepositoryMock.findById(nodeId)).thenReturn(Optional.of(node1));
        when(nodeRepositoryMock.existsById(nodeId)).thenReturn(true);
        List<Connection> connections = List.of(
                new Connection(node1, node4, 50.0),
                new Connection(node1, node2, 100.0),
                new Connection(node1, node3, 200.0)
        );
        when(connectionRepositoryMock.findBySourceOrDestination(nodeId, nodeId)).thenReturn(connections);

        Map<String, Double> availableConn = connectionServiceMock.getConnections(nodeId, 'D');

        assertThat(availableConn).containsExactly(
                entry("Node 4", 50.0),
                entry("Node 2", 100.0),
                entry("Node 3", 200.0)
        );
    }

    @Test
    void whenSameSourceAndDestinationThenThrowsException() {
        Long srcId = nodeId;
        Long destId = nodeId;

        assertThrows(IllegalArgumentException.class, () -> connectionServiceMock.establishConnection(srcId, destId),
                "Source and destination nodes cannot be the same.");
    }

    @Test
    void whenSourceOrDestinationNotFoundThenThrowsException() {
        Long srcId = nodeId;
        Long destId = nodeId + 1;

        when(nodeRepositoryMock.findById(srcId)).thenReturn(Optional.empty());
        when(nodeRepositoryMock.findById(destId)).thenReturn(Optional.of(new Node()));

        assertThrows(IllegalArgumentException.class, () -> connectionServiceMock.establishConnection(srcId, destId),
                "Source or destination node not found.");
    }

    @Test
    void whenConnectionLimitExceededThenThrowException() {
        Long srcId = 1L;
        Long destId = 2L;

        when(nodeRepositoryMock.findById(srcId)).thenReturn(Optional.of(node1));
        when(nodeRepositoryMock.findById(destId)).thenReturn(Optional.of(node2));
        when(connectionRepositoryMock.countById(srcId)).thenReturn(4);
        when(connectionRepositoryMock.countById(destId)).thenReturn(3);

        assertThrows(IllegalArgumentException.class, () -> connectionServiceMock.establishConnection(srcId, destId),
                "Connection limit exceeded for source or destination node.");
    }

    @Test
    void whenExistingConnectionThenThrowException() {
        Long srcId = 1L;
        Long destId = 2L;

        when(nodeRepositoryMock.findById(srcId)).thenReturn(Optional.of(node1));
        when(nodeRepositoryMock.findById(destId)).thenReturn(Optional.of(node2));
        when(connectionRepositoryMock.findBySourceAndDestination(srcId, destId)).thenReturn(Optional.of(new Connection()));

        assertThrows(IllegalArgumentException.class, () -> connectionServiceMock.establishConnection(srcId, destId),
                "There is already a connection exists between the nodes.");
    }

    @Test
    void whenNoConnectionExistsThenCreateConnection() {
        Long srcId = 1L;
        Long destId = 2L;

        when(nodeRepositoryMock.findById(srcId)).thenReturn(Optional.of(node1));
        when(nodeRepositoryMock.findById(destId)).thenReturn(Optional.of(node2));
        when(connectionRepositoryMock.findBySourceAndDestination(srcId, destId)).thenReturn(Optional.empty());
        when(connectionRepositoryMock.countById(srcId)).thenReturn(3);
        when(connectionRepositoryMock.countById(destId)).thenReturn(3);

        connectionServiceMock.establishConnection(srcId, destId);

        Mockito.verify(connectionRepositoryMock, Mockito.times(1)).save(Mockito.any(Connection.class));
    }

    @Test
    void whenValidNodesProvidedAndDirectConnectionExistsThenReturnRoute() throws NoSuchFieldException, IllegalAccessException {
        long sourceNodeId = 1L;
        long destinationNodeId = 2L;

        Field idField = Node.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(node1, sourceNodeId);
        idField.set(node2, destinationNodeId);
        Connection connection = new Connection(node1, node2, 5.0);

        when(nodeRepositoryMock.findById(sourceNodeId)).thenReturn(Optional.of(node1));
        when(nodeRepositoryMock.findById(destinationNodeId)).thenReturn(Optional.of(node2));
        when(connectionRepositoryMock.findBySourceAndDestination(sourceNodeId, destinationNodeId)).thenReturn(Optional.of(connection));

        Route result = connectionServiceMock.getOptimalRoute(sourceNodeId, destinationNodeId);

        assertEquals(List.of(node1.getName(), node2.getName()), result.getPath());
        assertEquals(5.0, result.getDistance());
    }

    @Test
    void whenMultipleIndirectConnectionExistsThenReturnOptimalRoute() throws NoSuchFieldException, IllegalAccessException {
        long sourceNodeId = 1L;
        long destinationNodeId = 4L;
        long intermediateNodeId1 = 2L;
        long intermediateNodeId2 = 3L;

        Field idField = Node.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(node1, sourceNodeId);
        idField.set(node4, destinationNodeId);
        idField.set(node2, intermediateNodeId1);
        idField.set(node3, intermediateNodeId2);
        Connection c1 = new Connection(node1, node2, 5.0);
        Connection c2 = new Connection(node2, node3, 5.0);
        Connection c3 = new Connection(node3, node4, 5.0);
        Connection c4 = new Connection(node2, node4, 5.0);

        when(nodeRepositoryMock.findById(sourceNodeId)).thenReturn(Optional.of(node1));
        when(nodeRepositoryMock.findById(destinationNodeId)).thenReturn(Optional.of(node4));
        when(connectionRepositoryMock.findBySourceAndDestination(sourceNodeId, destinationNodeId)).thenReturn(Optional.empty());
        when(connectionRepositoryMock.findBySourceOrDestination(sourceNodeId, sourceNodeId)).thenReturn(List.of(c1));
        when(connectionRepositoryMock.findBySourceOrDestination(node2.getId(), node2.getId())).thenReturn(List.of(c2, c4));
        when(connectionRepositoryMock.findBySourceOrDestination(node3.getId(), node3.getId())).thenReturn(List.of(c2, c3));
        when(connectionRepositoryMock.findBySourceOrDestination(destinationNodeId, destinationNodeId)).thenReturn(List.of(c3, c4));

        Route result = connectionServiceMock.getOptimalRoute(sourceNodeId, destinationNodeId);

        assertEquals(List.of(node1.getName(), node2.getName(), node4.getName()), result.getPath());
        assertEquals(10.0, result.getDistance());
    }
}
