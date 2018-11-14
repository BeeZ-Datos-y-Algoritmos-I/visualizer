import console.Console;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scene.VisualizerScene;

import java.io.IOException;

public class VisualizerMain extends Application {

    public static void main(String...args) throws IOException {

        new JFXPanel();
        Console.start(args);

        launch();
        return;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        VisualizerScene scene = new VisualizerScene();
        AnchorPane pane = scene.craft();

        scene.loadBees(Console.bees);

        Scene fxscene = new Scene(pane);
        primaryStage.setScene(fxscene);
        primaryStage.show();
        return;

    }
}