package io.beez.visualizer.scene;

import io.beez.visualizer.analyzer.I3DAnalyzer;
import io.beez.visualizer.analyzer.Simple3DAnalyzer;
import io.beez.visualizer.config.Config;
import io.beez.visualizer.scene.bee.Bee;

import java.io.File;
import java.util.LinkedList;

public class VisualizerAPI {

    public static VisualizerScene getVisualizer() {
        return new VisualizerScene();
    }

}
