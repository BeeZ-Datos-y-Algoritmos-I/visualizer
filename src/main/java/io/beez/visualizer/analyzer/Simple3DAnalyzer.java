package io.beez.visualizer.analyzer;

import io.beez.visualizer.config.Config;
import javafx.geometry.Point3D;
import io.beez.visualizer.scene.bee.Bee;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Simple3DAnalyzer implements I3DAnalyzer {

    @Override
    public LinkedList<Bee> make(boolean print, List<String> lines) {

        LinkedList<Bee> bees = new LinkedList<>();

        for(int i = 0; i < lines.size(); i++) {

            try {

                String[] compose = lines.get(i).split(",");
                Point3D point = new Point3D(
                        Double.valueOf(compose[0]),
                        Double.valueOf(compose[1]),
                        Double.valueOf(compose[2]));

                Bee bee = new Bee(point);
                bees.add(bee);

            } catch(Exception e) {
                e.printStackTrace();
                continue;
            }

        }

        return bees;
    }

}
