package scene;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import scene.bee.Bee;
import scene.bee.BeeModel;

import java.awt.*;
import java.util.LinkedList;

public class VisualizerScene extends Application {

    private static LinkedList<Bee> bees;

    private static final float WIDTH = 1400;
    private static final float HEIGHT = 800;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    private boolean cam_mode = false;

    @Override
    public void start(Stage stage) {

        final Group root = new Group();
        final SmartGroup group = new SmartGroup();

        final AmbientLight light = new AmbientLight();
        light.setColor(Color.WHITE);

        final PerspectiveCamera cam = new PerspectiveCamera();
        cam.setFieldOfView(20);
        cam.setFarClip(10000);
        cam.setNearClip(0.01);

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

        final Scene scene = new Scene(group, 800, 600, true);
        scene.setFill(Color.SKYBLUE);
        scene.setCamera(cam);

        for (Bee bee : bees) {

            group.getChildren().add(bee.getModel().getMesh());
            bee.attach();

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
                case W:
                    group.translateZProperty().set(group.getTranslateZ() + 100);
                    break;
                case S:
                    group.translateZProperty().set(group.getTranslateZ() - 100);
                    break;
                case Q:
                    group.rotateByX(10);
                    break;
                case E:
                    group.rotateByX(-10);
                    break;
                case T:
                    group.rotateByY(10);
                    break;
                case R:
                    group.rotateByY(-10);
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


        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        scene.setOnMouseDragged(event -> {
            angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
            angleY.set(anchorAngleY + anchorX - event.getSceneX());
        });


        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double movement = event.getDeltaY();
            group.translateZProperty().set(group.getTranslateZ() + movement);
        });
    }

    class SmartGroup extends Group {

        Rotate r;
        Transform t = new Rotate();

        void rotateByX(int ang) {
            r = new Rotate(ang, Rotate.X_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }

        void rotateByY(int ang) {
            r = new Rotate(ang, Rotate.Y_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }

    }

    //main method
    public static void show(LinkedList<Bee> display) {
        System.out.println(display.size());
        bees = display;
        launch();
    }
}
