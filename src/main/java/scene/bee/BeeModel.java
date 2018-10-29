package scene.bee;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class BeeModel {

    private static Image map;
    private static ObjModelImporter importer;

    private MeshView mesh;

    public BeeModel() {

        if(map == null) {
            map = new Image(new File("./textures/" + "defaultMat" + ".png").toURI().toString());
            System.out.println("map -> " + map);
            System.out.println("file -> " + map.getHeight());
        }

    }

    public void applyDefault() {

        this.mesh = new MeshView(importer.getImport()[0].getMesh());

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(map);

        this.mesh.setMaterial(material);

        applyTextures();
        applyModel();

    }

    public void applyModel() {

        mesh.setScaleY(.50);
        mesh.setScaleX(.50);
        mesh.setScaleZ(.50);

        mesh.getTransforms().addAll(new Rotate(50, Rotate.X_AXIS));
        mesh.getTransforms().addAll(new Rotate(50, Rotate.Y_AXIS));

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
