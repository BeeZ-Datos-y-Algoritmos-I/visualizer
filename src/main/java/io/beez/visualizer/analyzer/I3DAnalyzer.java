package io.beez.visualizer.analyzer;

import io.beez.visualizer.scene.bee.Bee;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public interface I3DAnalyzer {

    LinkedList<Bee> make(boolean print, List<String> lines);

}
