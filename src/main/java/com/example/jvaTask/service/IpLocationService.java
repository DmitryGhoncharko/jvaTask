package com.example.jvaTask.service;

import com.example.jvaTask.cache.Cache;
import com.example.jvaTask.entity.LocationResult;
import com.example.jvaTask.repository.LocationResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IpLocationService {
    private final Cache cache;
    private final LocationResultRepository repository;

    public Map<String, Object> fetchLocation(String ip) {
        String apiUrl = "http://ip-api.com/json/" + ip;
        if (cache.get(ip) != null) {
            return cache.get(ip);
        }
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    Map<String, Object> location = new ObjectMapper().readValue(result, Map.class);
                    cache.put(ip,location);
                    return location;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch IP location", e);
        }
        return Collections.emptyMap();
    }

    //LAB 1
    public Map<String, Object> fetchLocationBasicREST() {
        try {
            return new ObjectMapper().readValue("{\"status\":\"success\",\"country\":\"Belarus\",\"countryCode\":\"BY\",\"region\":\"HM\",\"regionName\":\"Minsk City\",\"city\":\"Minsk\",\"zip\":\"220073\",\"lat\":53.9007,\"lon\":27.5709,\"timezone\":\"Europe/Minsk\",\"isp\":\"Republican Unitary Telecommunication Enterprise Beltelecom\",\"org\":\"\",\"as\":\"AS6697 Republican Unitary Telecommunication Enterprise Beltelecom\",\"query\":\"37.214.64.229\"}", Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, Object> fetchLocationFromDb(String ip) {
        String apiUrl = "http://ip-api.com/json/" + ip;
        if (cache.get(ip) != null) {
            return cache.get(ip);
        }
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    Map<String, Object> location = new ObjectMapper().readValue(result, Map.class);
                    cache.put(ip,location);
                    LocationResult resultLocation = new LocationResult();
                    resultLocation.setIp(ip);
                    resultLocation.setCountry((String) location.get("country"));
                    resultLocation.setCity((String) location.get("city"));
                    repository.save(resultLocation);
                    return location;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch IP location", e);
        }
        return Collections.emptyMap();
    }
}
