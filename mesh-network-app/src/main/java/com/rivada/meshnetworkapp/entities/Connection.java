package com.rivada.meshnetworkapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "connections")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "source")
    private Node source;
    @ManyToOne
    @JoinColumn(name = "destination")
    private Node destination;
    private Double distance;

}
