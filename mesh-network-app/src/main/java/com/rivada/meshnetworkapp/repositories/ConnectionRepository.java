package com.rivada.meshnetworkapp.repositories;

import com.rivada.meshnetworkapp.entities.Connection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends CrudRepository<Connection, Long> {

    @Query("SELECT c " +
            "FROM Connection c " +
            "WHERE c.source.id IN (:node1, :node2) OR c.destination.id IN (:node1, :node2)")
    List<Connection> findBySourceOrDestination(@Param("node1") Long node1, @Param("node2") Long node2);

    @Query("SELECT c " +
            "FROM Connection c " +
            "WHERE c.source.id = :node1Id AND c.destination.id = :node2Id " +
            "OR c.source.id = :node2Id AND c.destination.id = :node1Id")
    Optional<Connection> findBySourceAndDestination(@Param("node1Id") Long node1Id, @Param("node2Id") Long node2Id);

    @Query("SELECT count(*) " +
            "FROM Connection c " +
            "WHERE c.source.id = :nodeId OR c.destination.id = :nodeId ")
    int countById(@Param("nodeId") Long nodeId);
}
