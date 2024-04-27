package com.rivada.meshnetworkapp.repositories;

import com.rivada.meshnetworkapp.entities.Connection;
import com.rivada.meshnetworkapp.entities.Node;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends CrudRepository<Connection, Long> {

    @Query(value = "SELECT ST_Distance(" +
            "ST_MakePoint(:sourcePoint)," +
            "ST_MakePoint(:destPoint))",
            nativeQuery = true)
    double calculateDistance(@Param("sourcePoint") Point sourcePoint,
                             @Param("destPoint") Point destPoint);

    List<Connection> findBySourceOrDestination(Node source, Node destination);

    @Query("SELECT c " +
            "FROM Connection c " +
            "WHERE c.source.id = :node1Id AND c.destination.id = :node2Id " +
            "OR c.source.id = :node2Id AND c.destination.id = :node1Id")
    Optional<Connection> findBySourceAndDestination(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);
}
