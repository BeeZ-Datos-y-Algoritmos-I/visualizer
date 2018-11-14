package scene;

import console.Console;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.composites.PolyLine3D;
import org.fxyz3d.utils.CameraTransformer;
import scene.bee.Bee;
import scene.zoom.AnimatedZoomOperator;
import util.CoordUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VisualizerScene {

    private static LinkedList<PolyLine3D> linkedlines = new LinkedList<>();

    private final StringProperty distance = new SimpleStringProperty("Distance: 0.0 meters");
    private final StringProperty bee1info = new SimpleStringProperty("Bee 1: [0,0 - 0,0 - 0,0]");
    private final StringProperty bee2info = new SimpleStringProperty("Bee 2: [0,0 - 0,0 - 0,0]");

    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    private static int moveSpeed = 1;
    private static boolean freeZoom = false;

    private Group root;
    private SmartGroup group;

    private AnchorPane result;
    private PerspectiveCamera cam;

    public AnchorPane craft(int width, int height) {

        result = new AnchorPane();

        final AnchorPane ui2d = new AnchorPane();
        final VBox vbox = new VBox();

        final Label distanceLabel = new Label(distance.getValue());
        final Label bee1infoLabel = new Label(bee1info.getValue());
        final Label bee2infoLabel = new Label(bee2info.getValue());

        ui2d.setFocusTraversable(true);

        distanceLabel.setFont(new Font("Arial", 9));
        bee1infoLabel.setFont(new Font("Arial", 9));
        bee2infoLabel.setFont(new Font("Arial", 9));

        vbox.getChildren().addAll(distanceLabel, bee1infoLabel, bee2infoLabel);
        ui2d.getChildren().add(vbox);

        distance.addListener(e -> {
            distanceLabel.setText(distance.getValue());
        });

        bee1info.addListener(e -> {
            bee1infoLabel.setText(bee1info.getValue());
        });

        bee2info.addListener(e -> {
            bee2infoLabel.setText(bee2info.getValue());
        });

        root = new Group();
        group = new SmartGroup();

        final AmbientLight light = new AmbientLight();
        light.setColor(Color.WHITE);

        cam = new PerspectiveCamera();

        CameraTransformer cameraTransform = new CameraTransformer();
        cameraTransform.getChildren().add(cam);

        final Box floor = new Box(500, 500, 1);
        floor.setTranslateX(200);
        floor.setTranslateY(200);
        floor.setTranslateZ(50);
        floor.setMaterial(new PhongMaterial(Color.GRAY));
        root.getChildren().add(floor);

        final Rectangle rectangle = new Rectangle(400, 400, Color.TRANSPARENT);
        rectangle.setMouseTransparent(true);
        rectangle.setDepthTest(DepthTest.DISABLE);
        root.getChildren().add(rectangle);

        root.getChildren().add(light);
        group.getChildren().add(root);

        result.setMaxSize(width, height);
        result.setMinSize(width, height);
        result.setPrefSize(width, height);

        result.getChildren().add(ui2d);

        SubScene sub = new SubScene(group,1024,768,false,SceneAntialiasing.BALANCED);
        sub.setCamera(cam);

        ui2d.getChildren().add(sub);
        captureMouseControl(group, result);

        result.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case H:
                    for(Bee bee : Console.bees) {

                        cam.setTranslateX(bee.getModel().getMesh().getTranslateX() + result.getWidth());
                        cam.setTranslateY(bee.getModel().getMesh().getTranslateY() - result.getHeight());
                        cam.setTranslateZ(bee.getModel().getMesh().getTranslateZ());
                        break;

                    }

                case Q:
                    group.translateZProperty().set(group.getTranslateZ() + 25);
                    break;
                case E:
                    group.translateZProperty().set(group.getTranslateZ() - 25);
                    break;
                case W:
                    group.rotateByX(0.0001 * moveSpeed);
                    break;
                case S:
                    group.rotateByX(-0.0001 * moveSpeed);
                    break;
                case A:
                    group.rotateByY(-0.0001 * moveSpeed);
                    break;
                case D:
                    group.rotateByY(0.0001 * moveSpeed);
                    break;
                case N:
                    moveSpeed -= 1;
                    break;
                case M:
                    moveSpeed += 1;
                    break;
                case V:
                    freeZoom = !freeZoom;
                    break;
                case C:
                    for(PolyLine3D line : linkedlines)
                        line.setVisible(!line.isVisible());
                    break;

            }
        });

        return result;

    }

    public VisualizerScene loadBees(LinkedList<Bee> bees) {

        group.getChildren().clear();
        root.getChildren().clear();

        for (Bee bee : bees) {

            bee.setVisualizer(this);

            group.getChildren().add(bee.getModel().getMesh());
            bee.attach();

            List<Point3D> points = new ArrayList<>();

            points.add(CoordUtil.convertPoint3D(bee.getEarthPosition()));
            points.add(CoordUtil.convertPoint3D(bee.getXYZPosition()));

            PolyLine3D line = new PolyLine3D(points, 80F, Color.LIMEGREEN);
            line.setVisible(false);

            linkedlines.add(line);
            root.getChildren().add(line);

            cam.setTranslateX(bee.getModel().getMesh().getTranslateX());
            cam.setTranslateY(bee.getModel().getMesh().getTranslateY());
            cam.setTranslateZ(bee.getModel().getMesh().getTranslateZ());

        }

        return this;

    }

    private void captureMouseControl(SmartGroup group, Node stage) {

        Rotate xRotate;
        Rotate yRotate;

        group.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );

        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {

            if(freeZoom == false) {

                double movement = event.getDeltaY() * moveSpeed;
                group.translateZProperty().set(group.getTranslateZ() + -movement);
                return;

            }

            double zoomFactor = 1.5 * moveSpeed;

            if (event.getDeltaY() <= 0) {
                zoomFactor = 1 / zoomFactor;
            }

            zoomOperator.zoom(group, zoomFactor, event.getSceneX(), event.getSceneY());

        });
    }

    public class SmartGroup extends Group {

        public Rotate rotate;
        public Transform transform = new Rotate();

        public void rotateByX(double angle) {

            rotate = new Rotate(angle, Rotate.X_AXIS);
            transform = transform.createConcatenation(rotate);

            this.getTransforms().clear();
            this.getTransforms().addAll(transform);

        }

        public void rotateByY(double ang) {

            rotate = new Rotate(ang, Rotate.Y_AXIS);
            transform = transform.createConcatenation(rotate);

            this.getTransforms().clear();
            this.getTransforms().addAll(transform);

        }

    }

    public void add3DObject(Node node) {
        root.getChildren().add(node);
    }

    public StringProperty getDistanceProperty() {
        return distance;
    }

    public StringProperty getBee1InfoProperty() {
        return bee1info;
    }

    public StringProperty getBee2InfoProperty() {
        return bee2info;
    }

}