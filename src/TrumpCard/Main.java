package TrumpCard;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {

    /*
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("title.fxml"));
        primaryStage.setTitle("Trump Card");
        primaryStage.setScene(new Scene(root, 1024, 640));
        primaryStage.show();
    }
*/
    private final int screenWidth = 1024;
    private final int screenHeight = 640;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Trump Cards");
        primaryStage.setWidth(screenWidth);
        primaryStage.setHeight(screenHeight);
        primaryStage.setResizable(false);

        Group root = new Group();
        primaryStage.setScene(new Scene(root));
        primaryStage.getScene().getStylesheets().add("TrumpCard/css/style.css");


        GameLoop gameLoop = new GameLoop(screenWidth, screenHeight, primaryStage, root);
        gameLoop.start();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
