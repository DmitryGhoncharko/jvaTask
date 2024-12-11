package com.example.jvaTask.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "location_result")
public class LocationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ip;
    private String country;
    private String city;
}

