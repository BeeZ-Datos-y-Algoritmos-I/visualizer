package io.beez.visualizer.scene.bee;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import org.fxyz3d.shapes.composites.PolyLine3D;
import io.beez.visualizer.scene.VisualizerScene;
import io.beez.visualizer.util.CoordUtil;
import io.beez.visualizer.util.CoordinateConversions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class Bee {

    public static final PhongMaterial material = new PhongMaterial(new Color(0.0f, 0.54509807f, 0.54509807f, 0.5));
    public static final PhongMaterial collisionMaterial = new PhongMaterial(new Color(1.0f, 0.27058825f, 0.0f, 0.5));

    public static final DecimalFormatSymbols formatsymbol = DecimalFormatSymbols.getInstance();
    public static final DecimalFormat format = new DecimalFormat("#.0000000", formatsymbol);

    public static LinkedList<Bee> linkedbees = new LinkedList<Bee>();
    public static Map<Sphere, Bee> spheres = new HashMap<Sphere, Bee>();
    public static PolyLine3D lineBee;

    private Sphere sphere;
    private BeeModel model;
    private BooleanProperty selected;

    private VisualizerScene visualizer;
    private Point3D earthPosition, xyzPosition;

    static {

        formatsymbol.setDecimalSeparator('.');

    }

    public Bee(Point3D point) {
        this(point.getX(), point.getY(), point.getZ());
    }

    public Bee(double x, double y, double z) {

        this.earthPosition = new Point3D(x, y, z);
        this.xyzPosition = xyzPosition = CoordinateConversions.getPointXYZfromLatLongDegress(getEarthPosition());

        this.model = BeeModel.generate();
        this.model.applyDefault();

        this.sphere = new Sphere();
        this.sphere.setRadius(100.0);
        this.sphere.setMaterial(material);
        this.sphere.setVisible(false);

        spheres.put(sphere, this);

    }

    public void attach() {

        this.model.applyRandomise();

        this.model.getMesh().setOnMouseClicked(e -> {

            if(linkedbees.contains(this)) {

                if (lineBee != null) {

                    lineBee.setVisible(false);
                    lineBee = null;

                }

                for (Bee bee : linkedbees) {

                    bee.model.applyDefault();

                    if (bee.sphere != null)
                        bee.sphere.setVisible(false);

                }

                linkedbees.clear();
                updateBeeInfo();

                visualizer.getDistanceProperty().setValue("Distance 0.0 meters");
                return;
            }

            if(linkedbees.size() > 1)
                return;

            linkedbees.add(this);
            this.model.applySelection();
            this.sphere.setVisible(true);

            if(linkedbees.size() == 2) {

                List<org.fxyz3d.geometry.Point3D> points = new ArrayList<>();

                for(Bee bee : linkedbees)
                    points.add(CoordUtil.convertPoint3D(bee.xyzPosition));

                lineBee = new PolyLine3D(points, 10F, Color.CYAN);
                lineBee.setFocusTraversable(true);

                visualizer.getDistanceProperty().setValue("Distance " + format.format(CoordUtil.getDistance(linkedbees.get(0).getEarthPosition(),
                                                                                                            linkedbees.get(1).getEarthPosition())) + " meters");

                visualizer.add3DObject(lineBee);
                updateBeeInfo();

            }

        });

        this.model.getMesh().setOnMouseEntered(e -> {

            if(linkedbees.contains(this))
                return;

            this.model.applyPreSelection();

        });

        this.model.getMesh().setOnMouseExited(e -> {

            if(linkedbees.contains(this))
                return;

            this.model.applyDefault();

        });

        this.model.getMesh().setTranslateX(xyzPosition.getX());
        this.model.getMesh().setTranslateY(xyzPosition.getY());
        this.model.getMesh().setTranslateZ(xyzPosition.getZ());

        this.sphere.setTranslateX(this.getXYZPosition().getX());
        this.sphere.setTranslateY(this.getXYZPosition().getY());
        this.sphere.setTranslateZ(this.getXYZPosition().getZ());

        visualizer.add3DObject(this.sphere);

    }

    public void updateBeeInfo() {

        if(linkedbees.size() > 0)
            visualizer.getBee1InfoProperty().setValue("Bee 1: [" + format.format(linkedbees.get(0).earthPosition.getX()) + ","
                                                                 + format.format(linkedbees.get(0).earthPosition.getY()) + ","
                                                                 + format.format(linkedbees.get(0).earthPosition.getZ()) + "]");
        else
            visualizer.getBee1InfoProperty().setValue("Bee 1: [0.0, 0.0, 0.0]");

        if(linkedbees.size() > 1)
            visualizer.getBee2InfoProperty().setValue("Bee 2: [" + format.format(linkedbees.get(1).earthPosition.getX()) + ","
                                                                 + format.format(linkedbees.get(1).earthPosition.getY()) + ","
                                                                 + format.format(linkedbees.get(1).earthPosition.getZ()) + "]");
        else
            visualizer.getBee2InfoProperty().setValue("Bee 2: [0.0, 0.0, 0.0]");

    }

    public void setVisualizer(VisualizerScene visualizer) {
        this.visualizer = visualizer;
    }

    public void setSphere(Sphere sphere) {
        this.sphere = sphere;
    }

    public VisualizerScene getVisualizer(){
        return visualizer;
    }

    public Sphere getSphere() {
        return sphere;
    }

    public BeeModel getModel() {
        return model;
    }

    public Point3D getEarthPosition() {
        return earthPosition;
    }

    public Point3D getXYZPosition() {
        return xyzPosition;
    }

}
