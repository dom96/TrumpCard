package TrumpCard;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {

    private final int screenWidth = 1280;
    private final int screenHeight = 720;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Trump Cards");
        primaryStage.setWidth(screenWidth);
        primaryStage.setHeight(screenHeight);
        primaryStage.setResizable(false);

        // Load resources
        if (Font.loadFont("file:fonts/HAGANE.TTF", 60) == null)
        {
            UIUtils.showErrorDialog("./fonts/HAGANE.TTF not found.", "Error");
            System.exit(1);
        }
        if (Font.loadFont("file:fonts/cour.TTF", 60) == null)
        {
            UIUtils.showErrorDialog("./fonts/cour.TTF not found.", "Error");
            System.exit(1);
        }

        MainMenu mainMenu = new MainMenu(screenWidth, screenHeight, primaryStage);
        mainMenu.start();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
