package com.example.jvaTask.controller;

import com.example.jvaTask.entity.LocationResult;
import com.example.jvaTask.repository.LocationResultRepository;
import com.example.jvaTask.service.IpLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class IpLocationController {

    private final IpLocationService ipLocationService;
    private final RequestCounter requestCounter;
    private final LocationResultRepository locationResultRepository;

    @GetMapping("/location")
    public ResponseEntity<?> getLocation(@RequestParam("ip") String ip) {
        requestCounter.increment();
        log.info("Received IP: {}", ip);
        if (!isValidIp(ip)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid IP address"));
        }
        try {
            Map<String, Object> location = ipLocationService.fetchLocation(ip);
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            log.error("Error fetching location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
    private boolean isValidIp(String ip) {
        String ipRegex =
                "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$";
        return ip.matches(ipRegex);
    }
    //LAB1
    @GetMapping("/basiclocation")
    public ResponseEntity<Map<String, Object>> getBasicLocation() {
        requestCounter.increment();
        Map<String, Object> location = ipLocationService.fetchLocationBasicREST();
        return ResponseEntity.ok(location);
    }
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getRequestCount() {
        requestCounter.increment();
        return ResponseEntity.ok(Map.of("requestCount", requestCounter.getCount()));
    }
    @PostMapping("/bulk-location")
    public ResponseEntity<List<Map<String, ?>>> getBulkLocations(@RequestBody List<String> ips) {
        requestCounter.increment();
        List<Map<String, ?>> results = ips.stream()
                .map(ip -> {
                    if (!isValidIp(ip)) {
                        return Map.of("ip", ip, "error", "Invalid IP address");
                    }
                    return ipLocationService.fetchLocation(ip);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/from-db")
    public ResponseEntity<List<Map<String, ?>>> getFromDbLocations(@RequestBody List<String> ips) {
        requestCounter.increment();
        List<Map<String, ?>> results = ips.stream()
                .map(ip -> {
                    if (!isValidIp(ip)) {
                        return Map.of("ip", ip, "error", "Invalid IP address");
                    }
                    return ipLocationService.fetchLocationFromDb(ip);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
    @GetMapping("/history")
    public ResponseEntity<List<LocationResult>> getHistory() {
        requestCounter.increment();
        return ResponseEntity.ok(locationResultRepository.findAll());
    }
}

