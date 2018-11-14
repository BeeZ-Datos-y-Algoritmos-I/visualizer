package scene;

import analyzer.I3DAnalyzer;
import analyzer.Simple3DAnalyzer;
import config.Config;
import scene.bee.Bee;

import java.io.File;
import java.util.LinkedList;

public class VisualizerAPI {

    public static VisualizerScene getVisualizer(String path) {
        Config.FILES.put("input_path", path);
        return getVisualizer(new File(path));
    }

    public static  VisualizerScene getVisualizer(File file) {

        Config.FILES.put("input_path", file.getAbsolutePath());
        I3DAnalyzer analyzer = new Simple3DAnalyzer();

        return getVisualizer(analyzer.make(false));

    }

    public static VisualizerScene getVisualizer(LinkedList<Bee> bees) {
        return new VisualizerScene();
    }

}
