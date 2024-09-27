package com.ku.covigator.support;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeometryUtils {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static Point generatePoint(Double latitude, Double longitude) {
        Coordinate coordinate = new Coordinate(longitude, latitude);
        return geometryFactory.createPoint(coordinate);
    }

}
