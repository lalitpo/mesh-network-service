package com.rivada.meshnetworkapp.repositories;

import com.rivada.meshnetworkapp.entities.Node;
import org.locationtech.jts.geom.Point;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NodeRepository extends CrudRepository<Node, Long> {

    Optional<Node> findByCoordinates(Point coordinates);
}

