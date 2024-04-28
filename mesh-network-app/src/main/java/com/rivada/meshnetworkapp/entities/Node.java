package com.rivada.meshnetworkapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "nodes")
public class Node implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "coordinates")
    private Point coordinates;

    public Node(String name, Point coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }
}
