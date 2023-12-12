package com.example.myapplication2.Formula;

public class GeoUtils {

    private static final double EARTH_RADIUS = 6371; // Earth radius in kilometers

    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        // Convert latitude and longitude from degrees to radians
        double startLatRad = Math.toRadians(startLatitude);
        double startLonRad = Math.toRadians(startLongitude);
        double endLatRad = Math.toRadians(endLatitude);
        double endLonRad = Math.toRadians(endLongitude);

        // Calculate the change in coordinates
        double deltaLat = endLatRad - startLatRad;
        double deltaLon = endLonRad - startLonRad;

        // Haversine formula for distance calculation
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(startLatRad) * Math.cos(endLatRad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        return EARTH_RADIUS * c;
    }
}

