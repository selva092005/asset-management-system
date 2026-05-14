package com.learn.demo.service;

import com.learn.demo.dto.response.CurrentLocationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * GeoLocationService
 *
 * Detects the caller's current location using ip-api.com (free, no API key needed).
 *
 * Flow:
 *   1. Extract real client IP from the request (handles reverse proxies / dev tunnels)
 *   2. Call http://ip-api.com/json/{ip} to get city, region, country, lat/lon
 *   3. Return as CurrentLocationResponseDTO
 *
 * NOTE: ip-api.com free tier allows 45 requests/minute from one IP.
 *       For production, consider a paid plan or caching.
 */
@Slf4j
@Service
public class GeoLocationService {

    private static final String GEO_API_URL = "http://ip-api.com/json/{ip}?fields=status,city,regionName,country,countryCode,timezone,lat,lon,query";

    private final RestTemplate restTemplate;

    public GeoLocationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Detect location of the caller based on their IP address.
     *
     * @param request the incoming HTTP request
     * @return CurrentLocationResponseDTO with city, region, country, lat/lon etc.
     */
    public CurrentLocationResponseDTO detectCurrentLocation(HttpServletRequest request) {
        String clientIp = extractClientIp(request);
        log.info("Detecting location for IP: {}", clientIp);
        return fetchLocationByIp(clientIp);
    }

    /**
     * Detect location for a specific IP address (useful for testing).
     *
     * @param ip the IP address string
     * @return CurrentLocationResponseDTO
     */
    public CurrentLocationResponseDTO detectLocationByIp(String ip) {
        log.info("Detecting location for provided IP: {}", ip);
        return fetchLocationByIp(ip);
    }

    // ─── private helpers ────────────────────────────────────────────────────────

    private CurrentLocationResponseDTO fetchLocationByIp(String ip) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(
                    GEO_API_URL, Map.class, ip
            );

            if (response == null || !"success".equals(response.get("status"))) {
                log.warn("ip-api.com returned non-success for IP {}: {}", ip, response);
                throw new RuntimeException("Could not determine location for IP: " + ip);
            }

            CurrentLocationResponseDTO dto = new CurrentLocationResponseDTO();
            dto.setIp((String) response.get("query"));
            dto.setCity((String) response.get("city"));
            dto.setRegion((String) response.get("regionName"));
            dto.setCountry((String) response.get("country"));
            dto.setCountryCode((String) response.get("countryCode"));
            dto.setTimezone((String) response.get("timezone"));

            // lat / lon come as Double from Jackson
            if (response.get("lat") != null) {
                dto.setLatitude(((Number) response.get("lat")).doubleValue());
            }
            if (response.get("lon") != null) {
                dto.setLongitude(((Number) response.get("lon")).doubleValue());
            }

            return dto;

        } catch (RuntimeException e) {
            throw e; // rethrow as-is
        } catch (Exception e) {
            log.error("Error calling ip-api.com for IP {}: {}", ip, e.getMessage());
            throw new RuntimeException("Location detection failed: " + e.getMessage(), e);
        }
    }

    /**
     * Extract the real client IP, respecting common proxy headers.
     * Priority: X-Forwarded-For → X-Real-IP → RemoteAddr
     */
    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // X-Forwarded-For can be "clientIp, proxy1, proxy2" — take the first
            return xff.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }
}