package com.rivada.meshnetworkapp.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class Route {
    private List<String> path;
    private Double distance;
}
