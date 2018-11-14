package io.beez.visualizer.scene.bee;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BeeModel {

    private static Image map;
    private static ObjModelImporter importer;

    private MeshView mesh;

    public BeeModel() {

        if(map == null)
            map = new Image(new File("./textures/" + "defaultMat" + ".png").toURI().toString());

        this.mesh = new MeshView(importer.getImport()[0].getMesh());

        applyModel();

    }

    public void applyDefault() {

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(map);

        applyTextures();

        this.mesh.setMaterial(material);

    }

    public void applySelection() {

        PhongMaterial material = new PhongMaterial(Color.MEDIUMVIOLETRED);
        this.mesh.setMaterial(material);

    }

    public void applyPreSelection() {

        PhongMaterial material = new PhongMaterial(Color.GREEN);
        this.mesh.setMaterial(material);

    }

    public void applyModel() {

        mesh.setScaleY(50);
        mesh.setScaleX(50);
        mesh.setScaleZ(50);

    }

    public void applyTextures() {

        Map<String, PhongMaterial> mapTexs = importer.getNamedMaterials();
        Iterator<String> it = mapTexs.keySet().iterator();

        while(it.hasNext()) {
            String key = it.next();
            mapTexs.get(key).setDiffuseMap(map);
        }

    }

    public void applyRandomise() {

        mesh.getTransforms().addAll(new Rotate(ThreadLocalRandom.current().nextDouble(0.0, 50.0), Rotate.X_AXIS));
        mesh.getTransforms().addAll(new Rotate(ThreadLocalRandom.current().nextDouble(0.0, 50.0), Rotate.Y_AXIS));

    }

    public ObjModelImporter getImporter() {
        return importer;
    }

    public MeshView getMesh() {
        return mesh;
    }

    public static BeeModel generate() {

        if(importer == null) {

            importer = new ObjModelImporter();
            importer.read(new File("./model/bee.obj"));

        }

        return new BeeModel();

    }

}
