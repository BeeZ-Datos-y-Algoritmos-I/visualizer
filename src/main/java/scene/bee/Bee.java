package scene.bee;

import javafx.geometry.Point3D;

public class Bee {

    private BeeModel model;

    private double x;
    private double y;
    private double z;

    public Bee(Point3D point) {
        this(point.getX(), point.getY(), point.getZ());
    }

    public Bee(double x, double y, double z) {

        this.x = x;
        this.y = y;
        this.z = z;

        this.model = BeeModel.generate();
        this.model.applyDefault();

    }

    public void attach() {
        this.model.getMesh().setTranslateX(this.x);
        this.model.getMesh().setTranslateY(this.y);
        this.model.getMesh().setTranslateZ(this.z);
    }

    public BeeModel getModel() {
        return model;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

}
