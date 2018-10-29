package scene.bee;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import org.fxyz3d.shapes.composites.PolyLine3D;
import scene.VisualizerScene;
import util.CoordUtil;
import util.CoordinateConversions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Bee {

    public static final DecimalFormatSymbols formatsymbol = DecimalFormatSymbols.getInstance();
    public static final DecimalFormat format = new DecimalFormat("#.0000000", formatsymbol);

    public static LinkedList<Bee> linkedbees = new LinkedList<Bee>();
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

                    if (bee.sphere != null) {

                        bee.sphere.setVisible(false);
                        bee.sphere = null;

                    }

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
                showSpheres();

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

        System.out.println(earthPosition);

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

    public void showSpheres() {

        for(Bee bee : linkedbees) {

            PhongMaterial material = new PhongMaterial(new Color(0.0f, 0.54509807f, 0.54509807f, 0.5));

            bee.sphere = new Sphere();
            bee.sphere.setRadius(100.0);
            bee.sphere.setMaterial(material);

            bee.sphere.setTranslateX(bee.getXYZPosition().getX());
            bee.sphere.setTranslateY(bee.getXYZPosition().getY());
            bee.sphere.setTranslateZ(bee.getXYZPosition().getZ());

            visualizer.add3DObject(bee.sphere);

        }

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
