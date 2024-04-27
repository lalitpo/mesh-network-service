package com.rivada.meshnetworkapp.entities;


import lombok.Setter;

import java.util.List;

@Setter
public class Route {
    private List<Node> path;
    private Double distance;
}
