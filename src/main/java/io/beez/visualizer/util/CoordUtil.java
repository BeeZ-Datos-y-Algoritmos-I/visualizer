package io.beez.visualizer.util;

import javafx.geometry.Point3D;

public class CoordUtil {

    public static final Point3D reference = new Point3D(6.331944, -75.558056, 0);

    public static final double PI = 3.1415927;
    public static final double TPI = 6.2831854;

    public static final double RADIANS = 57.2957795;
    public static final double DEGREE_TO_RADIANS = 0.01745329252;

    public static Point3D LL2XY(Point3D point) {

        final int mapWidth = 200;
        final int mapHeight = 100;

        double xx = 0, yy = 0, cache = 0, r = 0;

        cache = Math.log(Math.tan(PI / 4) + (((point.getY() * PI) / 180) / 2));
        xx = (point.getX() + 180) * (mapWidth / 360);
        yy = (mapHeight / 2) - (mapWidth * cache / (2 * PI));

        return new Point3D(xx, yy, point.getZ());

    }

    public double metersDegLon(double degree) {
        final double radians = degreeToRadians(degree);
        return ((111415.13 * Math.cos(radians))- (94.55 * Math.cos(3.0 * radians)) + (0.12 * Math.cos(5.0 * radians)));
    }

    public double metersDegLat(double degree) {
        final double radians = degreeToRadians(degree);
        return (111132.09 - (566.05 * Math.cos(2.0 * radians))+ (1.20 * Math.cos(4.0 * radians)) - (0.002 * Math.cos(6.0 * radians)));
    }

    public static double getDistance(Point3D abeja1, Point3D abeja2){
        return Math.sqrt(Math.pow((abeja1.getX() - abeja2.getX())*111111,2) +
                Math.pow((abeja1.getY() - abeja2.getY())*111111,2) +
                Math.pow(abeja1.getZ() - abeja2.getZ(),2)
        );
    }

    public static org.fxyz3d.geometry.Point3D convertPoint3D(Point3D point) {
        return new org.fxyz3d.geometry.Point3D((float) point.getX(), (float) point.getY(), (float) point.getZ());
    }

    public double degreeToRadians(double degree) {
        return degree / RADIANS;
    }

}
