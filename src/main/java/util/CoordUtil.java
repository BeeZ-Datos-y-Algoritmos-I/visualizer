package util;

import javafx.geometry.Point3D;

public class CoordUtil {

    public static final Point3D reference = new Point3D(6.331944, -75.558056, 0);

    public static final double PI = 3.1415927;
    public static final double TPI = 6.2831854;

    public static final double RADIANS = 57.2957795;
    public static final double DEGREE_TO_RADIANS = 0.01745329252;

    public Point3D LL2XY(Point3D point) {

        Point3D result;
        double xx = 0, yy = 0, r = 0;

        xx = (point.getX() - reference.getX()) * metersDegLon(reference.getX());
        yy = (point.getY() - reference.getY()) * metersDegLat(reference.getY());

        r = Math.sqrt(xx * xx + yy * yy);

        return point;

    }

    public double metersDegLon(double degree) {
        final double radians = degreeToRadians(degree);
        return ((111415.13 * Math.cos(radians))- (94.55 * Math.cos(3.0 * radians)) + (0.12 * Math.cos(5.0 * radians)));
    }

    public double metersDegLat(double degree) {
        final double radians = degreeToRadians(degree);
        return (111132.09 - (566.05 * Math.cos(2.0 * radians))+ (1.20 * Math.cos(4.0 * radians)) - (0.002 * Math.cos(6.0 * radians)));
    }

    public double degreeToRadians(double degree) {
        return degree / RADIANS;
    }

}
