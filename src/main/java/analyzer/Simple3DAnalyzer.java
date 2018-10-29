package analyzer;

import config.Config;
import javafx.geometry.Point3D;
import scene.bee.Bee;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Simple3DAnalyzer implements I3DAnalyzer {

    public static final LinkedList<String> lines = new LinkedList<String>();

    public static final ThreadLocalRandom current = ThreadLocalRandom.current();
    public static final List<Thread> threads = new ArrayList<Thread>();

    @Override
    public LinkedList<Bee> make(boolean print) {

        LinkedList<Bee> bees = new LinkedList<>();
        FileWriter FWRITER = null;

        try {
            FWRITER = new FileWriter(Config.FILES.get("output_path"), false);
        } catch (IOException e) { e.printStackTrace(); }

        final PrintWriter WRITER = new PrintWriter(FWRITER);
        final String PATH = Config.FILES.get("input_path");

        FileReader READER;
        BufferedReader BUFFER;

        try {

            READER = new FileReader(PATH);
            BUFFER = new BufferedReader(READER);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
            return bees;

        }

        try {

            String line;
            while ((line = BUFFER.readLine()) != null)
                lines.add(line);

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

            WRITER.flush();
            WRITER.close();

        } catch (IOException e) {

            e.printStackTrace();
            return bees;

        }

        return bees;
    }

}
