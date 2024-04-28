package com.rivada.meshnetworkapp.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CityInfo {
    private String cityName;
    private List<Double> coordinates;
}
