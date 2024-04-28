package com.rivada.meshnetworkapp.requests;

import lombok.Getter;

import java.util.List;

@Getter
public class CityInfo {
    private String cityName;
    private List<Double> coordinates;
}
