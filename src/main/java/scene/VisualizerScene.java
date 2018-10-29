package scene;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.stage.Stage;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.composites.PolyLine3D;
import org.fxyz3d.utils.CameraTransformer;
import scene.bee.Bee;
import scene.zoom.AnimatedZoomOperator;
import util.CoordUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VisualizerScene extends Application {

    private static int moveSpeed = 1;
    private static boolean freeZoom = false;

    private static LinkedList<PolyLine3D> linkedlines = new LinkedList<PolyLine3D>();
    private static LinkedList<Bee> bees;

    private Group root;
    private SmartGroup group;

    private final StringProperty distance = new SimpleStringProperty("Distance: 0.0 meters");
    private final StringProperty bee1info = new SimpleStringProperty("Bee 1: [0,0 - 0,0 - 0,0]");
    private final StringProperty bee2info = new SimpleStringProperty("Bee 2: [0,0 - 0,0 - 0,0]");

    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    @Override
    public void start(Stage stage) {

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

        final PerspectiveCamera cam = new PerspectiveCamera();

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

        final Scene scene = new Scene(ui2d, 800, 600, false, SceneAntialiasing.BALANCED);
        scene.setFill(Color.SKYBLUE);

        SubScene sub = new SubScene(group,1024,768,false,SceneAntialiasing.BALANCED);
        sub.setCamera(cam);
        ui2d.getChildren().add(sub);

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

        //group.translateXProperty().set(800 / 2);
        //group.translateYProperty().set(600 / 2);
        //group.translateZProperty().set(-1200);

        initMouseControl(group, scene, stage);

        stage.setScene(scene);
        stage.setTitle("BeeZ Visualizer");
        stage.show();

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case H:
                    for(Bee bee : bees) {

                        cam.setTranslateX(bee.getModel().getMesh().getTranslateX() + scene.getWidth());
                        cam.setTranslateY(bee.getModel().getMesh().getTranslateY() - scene.getHeight());
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

    }

    /*private MeshView mesh;
    private Scene scene;
    private PerspectiveCamera camera;

    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final double TURN_FACTOR = 0.5;

    public Parent createContent() throws Exception {
        // Box

        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            File file = new File("test.obj");
            System.out.println(file.length());
            objImporter.read(file);
        }
        catch (ImportException e) {
            // handle exception
        }

        MeshView[] meshViews = objImporter.getImport();

        System.out.println(meshViews.length);
        objImporter.close();

        mesh = meshViews[0];

        // Create and position camera
        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
                new Rotate(-20, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.X_AXIS),
                new Translate(0, 1, -5));

        camera.getTransforms().addAll(rotateZ, rotateY, rotateX);

        PointLight greenLight = new PointLight();
        greenLight.setColor(Color.ORANGE);
        greenLight.setTranslateX(250);
        greenLight.setTranslateY(300);
        greenLight.setTranslateZ(300);

        // Build the Scene Graph
        Group root = new Group(greenLight);
        //root.getChildren().add(camera);
        root.getChildren().addAll(meshViews);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 300, 300, true,
                SceneAntialiasing.BALANCED);

        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        return new Group(subScene);
    }

    private double mousePosX, mousePosY = 0;
    private void handleMouseEvents() {
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });

        scene.setOnMouseDragged((MouseEvent me) -> {
            double dx = (mousePosX - me.getSceneX());
            double dy = (mousePosY - me.getSceneY());
            if (me.isPrimaryButtonDown()) {
                rotateX.setAngle(rotateX.getAngle() -
                        (dy / scene.getHeight() * 360) * (Math.PI / 180));
                rotateY.setAngle(rotateY.getAngle() -
                        (dx / scene.getWidth() * -360) * (Math.PI / 180));
            } else {


                rotateZ.setAngle(rotateZ.getAngle() - (dz));

            }
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });

        scene.setOnKeyPressed(e -> {

            String code = e.getCode().toString();
            System.out.println(code);

            if(code.equalsIgnoreCase("T"))
                camera.getTransforms().addAll(new Rotate(-5, Rotate.Z_AXIS));

            if(code.equalsIgnoreCase("G"))
                camera.getTransforms().addAll(new Rotate(5, Rotate.Z_AXIS));

            if(code.equalsIgnoreCase("Y"))
                camera.getTransforms().addAll(new Rotate(-5, Rotate.X_AXIS));

            if(code.equalsIgnoreCase("H"))
                camera.getTransforms().addAll(new Rotate(5, Rotate.X_AXIS));

            if(code.equalsIgnoreCase("U"))
                camera.getTransforms().addAll(new Rotate(-5, Rotate.Y_AXIS));

            if(code.equalsIgnoreCase("J"))
                camera.getTransforms().addAll(new Rotate(5, Rotate.Y_AXIS));

            if(code.equalsIgnoreCase("A")) {
                camera.setTranslateX(camera.getTranslateX() + 0.10);
            }

            if(code.equalsIgnoreCase("D")) {
                camera.setTranslateX(camera.getTranslateX() - 0.10);
            }

            if(code.equalsIgnoreCase("W")) {
                camera.setTranslateY(camera.getTranslateY() - 0.10);
            }

            if(code.equalsIgnoreCase("S")) {
                camera.setTranslateY(camera.getTranslateY() + 0.10);
            }
            if(code.equalsIgnoreCase("Q")) {
                camera.setTranslateZ(camera.getTranslateZ() + 0.10);
            }

            if(code.equalsIgnoreCase("E")) {
                camera.setTranslateZ(camera.getTranslateZ() - 0.10);
            }

        });

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        scene = new Scene(createContent());
        Label text = new Label("POS:");
        text.setFont(Font.font("Times New Roman", FontWeight.BOLD,
                FontPosture.ITALIC, 20));
        //primaryStage.get
        handleMouseEvents();
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/


    /*private int numColors = 10;

    @Override
    public void start(Stage primaryStage) {

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-5);

        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            File file = new File("test.obj");
            System.out.println(file.length());
            objImporter.read(file);
        }
        catch (ImportException e) {
            // handle exception
        }

        MeshView[] meshViews = objImporter.getImport();

        System.out.println(meshViews.length);
        objImporter.close();

        IcosahedronMesh icoFaces = new IcosahedronMesh(100, 0);
        icoFaces.setTextureModeFaces(numColors);
        icoFaces.getTransforms().addAll(new Rotate(20, Rotate.X_AXIS), new Rotate(-10, Rotate.Y_AXIS));

        final Group group = new Group(meshViews);

        Scene scene = new Scene(group, 600, 400, true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);

        primaryStage.setScene(scene);
        primaryStage.setTitle(("Icosahedron - FXyz3D"));
        primaryStage.show();

        scene.setOnKeyPressed(e -> {

            String code = e.getCode().toString();
            System.out.println(code);

            if(code.equalsIgnoreCase("T"))
                camera.getTransforms().addAll(new Rotate(-5, Rotate.Z_AXIS));

            if(code.equalsIgnoreCase("G"))
                camera.getTransforms().addAll(new Rotate(5, Rotate.Z_AXIS));

            if(code.equalsIgnoreCase("Y"))
                camera.getTransforms().addAll(new Rotate(-5, Rotate.X_AXIS));

            if(code.equalsIgnoreCase("H"))
                camera.getTransforms().addAll(new Rotate(5, Rotate.X_AXIS));

            if(code.equalsIgnoreCase("U"))
                camera.getTransforms().addAll(new Rotate(-5, Rotate.Y_AXIS));

            if(code.equalsIgnoreCase("J"))
                camera.getTransforms().addAll(new Rotate(5, Rotate.Y_AXIS));

            if(code.equalsIgnoreCase("A")) {
                camera.setTranslateX(camera.getTranslateX() + 0.01);
            }

            if(code.equalsIgnoreCase("D")) {
                camera.setTranslateX(camera.getTranslateX() - 0.01);
            }

            if(code.equalsIgnoreCase("W")) {
                camera.setTranslateY(camera.getTranslateY() - 0.01);
            }

            if(code.equalsIgnoreCase("S")) {
                camera.setTranslateY(camera.getTranslateY() - 0.01);
            }

        });

        icoFaces.setOnMouseClicked(e -> {
            ObservableFaceArray faces = ((TriangleMesh) icoFaces.getMesh()).getFaces();
            int selectedFace = e.getPickResult().getIntersectedFace();

            int colorId = faces.get(6 * selectedFace + 1);
            int newColorId = colorId + 1 >= numColors ? 0 : colorId + 1;
            faces.set(6 * selectedFace + 1, newColorId);
            faces.set(6 * selectedFace + 3, newColorId);
            faces.set(6 * selectedFace + 5, newColorId);
        });
    }*/

    /*@Override
    //objimport teil
    public void start(Stage primaryStage) throws Exception {


        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            File file = new File("test.obj");
            System.out.println(file.length());
            objImporter.read(file);
        }
        catch (ImportException e) {
            // handle exception
        }

        MeshView[] meshViews = objImporter.getImport();

        System.out.println(meshViews.length);
        objImporter.close();

        Group views = new Group(meshViews);

        //javafx part
        primaryStage.setTitle("BeeZ - Visualizador del Resultado en Tercera Dimensión");
        StackPane layout = new StackPane();
        layout.getChildren().add(views);

        Scene scene = new Scene(layout, 1200, 600);
        scene.setFill(Color.ORANGE);
        primaryStage.setScene(scene);
        primaryStage.show();

    }*/


    private void initMouseControl(SmartGroup group, Scene scene, Stage stage) {
        Rotate xRotate;
        Rotate yRotate;

        group.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );

        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

/*
        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        scene.setOnMouseDragged(event -> {
            angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
            angleY.set(anchorAngleY + anchorX - event.getSceneX());
        });*/


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

    class SmartGroup extends Group {

        Rotate r;
        Transform t = new Rotate();

        void rotateByX(double ang) {
            r = new Rotate(ang, Rotate.X_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }

        void rotateByY(double ang) {
            r = new Rotate(ang, Rotate.Y_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
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

    //main method
    public static void show(LinkedList<Bee> display) {
        System.out.println(display.size());
        bees = display;
        launch();
    }
}
