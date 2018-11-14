package io.beez.visualizer.console;

import io.beez.visualizer.analyzer.I3DAnalyzer;
import io.beez.visualizer.analyzer.Simple3DAnalyzer;
import io.beez.visualizer.config.Config;
import javafx.stage.Stage;
import io.beez.visualizer.scene.bee.Bee;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.LinkedList;

public class Console {

    public static Stage instance;
    public static LinkedList<Bee> bees;

    public static final I3DAnalyzer analyzer = new Simple3DAnalyzer();

    public static void start(String...args) throws IOException {

        System.out.println("[INFORMACIÓN]: Inicializado el sistema de visualización en 3D.");
        System.out.println("[ACCIÓN]: Leyendo el archivo visualizer.ini");

        Config.load(args);
        System.out.println("[ACCIÓN]: Leyendo el archivo de entrada " + Config.FILES.get("input_path") + "...");
        System.out.println("[ACCIÓN]: Generando entorno 3D...");

        Date date = makeDate();
        bees = analyzer.make(false, Files.readAllLines(new File(Config.FILES.get("input_path")).toPath()));

        System.out.println("[INFORMACIÓN]: Se ha generado en " + (makeDate().getTime() - date.getTime()) + " ms.");
        System.out.println("[ACCIÓN]: Mostrando ambiente...");

    }

    public static Date makeDate() {
        return new Date();
    }

}