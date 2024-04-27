package com.rivada.meshnetworkapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.geo.Point;

@Getter
@Entity
@Table(name = "nodes")
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "coordinates", columnDefinition = "POINT", nullable = false)
    private Point coordinates;
}
