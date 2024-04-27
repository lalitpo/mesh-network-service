package com.rivada.meshnetworkapp.repositories;

import com.rivada.meshnetworkapp.entities.Node;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends CrudRepository<Node, String> {

}

