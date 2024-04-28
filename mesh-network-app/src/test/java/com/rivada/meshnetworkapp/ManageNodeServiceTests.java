package com.rivada.meshnetworkapp;

import com.rivada.meshnetworkapp.entities.Connection;
import com.rivada.meshnetworkapp.entities.Node;
import com.rivada.meshnetworkapp.repositories.ConnectionRepository;
import com.rivada.meshnetworkapp.repositories.NodeRepository;
import com.rivada.meshnetworkapp.requests.CityInfo;
import com.rivada.meshnetworkapp.services.NodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ManageNodeServiceTests {

    private final GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final Node node1 = new Node("Node 1", geoFactory.createPoint(new Coordinate(-74.0060, 40.7128)));
    private final Node node2 = new Node("Node 2", geoFactory.createPoint(new Coordinate(31.2357, 30.0444)));
    @Mock
    private NodeRepository nodeRepositoryMock;
    @Mock
    private ConnectionRepository connectionRepositoryMock;
    @InjectMocks
    private NodeService nodeServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenNodesAvailableThenReturnThem() {

        when(nodeRepositoryMock.findAll()).thenReturn(List.of(node1, node2));

        List<String> result = nodeServiceMock.getAllNodes();

        assertTrue(result.contains("Node 1"));
        assertTrue(result.contains("Node 2"));
        assertFalse(result.contains("Node 3"));
    }

    @Test
    void whenValidCoordinatesThenThrowException() {
        CityInfo city = new CityInfo("Test City 2", List.of(-140.7128, -94.0060));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            nodeServiceMock.createNode(city);
        });

        assertEquals("Latitude must be within the range -90 to 90 degrees", exception.getMessage());
    }

    @Test
    void whenConnectionExistThenThrowException() {
        Long nodeId = 1L;

        Connection connection = new Connection(node1, node2, 34.03);
        when(nodeRepositoryMock.existsById(nodeId)).thenReturn(true);
        when(connectionRepositoryMock.findBySourceOrDestination(nodeId, nodeId)).thenReturn(List.of(connection));

        SQLException exception = assertThrows(SQLException.class, () -> {
            nodeServiceMock.deleteNode(nodeId);
        });

        assertTrue(exception.getMessage().contains("Please remove connections first."));
    }
}
