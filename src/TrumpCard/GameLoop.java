package TrumpCard;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.nio.file.Paths;


public class GameLoop extends AnimationTimer {

    private GraphicsContext graphicsContext;
    private double width;
    private double height;
    private Stage stage;
    private Group root;

    private Image titleLeft;
    private Image titleRight;
    private Image titleBg;
    private Font titleFont;
    private Font menuFont;
    GameLoop(double width, double height, Stage stage, Group root)
    {
        this.stage = stage;
        this.root = root;
        this.width = width;
        this.height = height;

        this.titleLeft = new Image("file:images/title_left_small.jpg");
        this.titleRight = new Image("file:images/title_right_small.jpg");
        this.titleBg = new Image("file:images/title_bg_sized.jpg");

        this.titleFont = Font.loadFont("file:fonts/HAGANE.TTF", 60);
        this.menuFont = Font.loadFont("file:fonts/cour.TTF", 40);

        Canvas canvas = new Canvas(width, height);

        this.graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Create a Vertical Box to hold the main menu buttons.
        VBox vb = new VBox();
        vb.setLayoutX(width / 2 - 100);
        vb.setLayoutY(250);
        vb.setSpacing(20);
        vb.setPadding(new Insets(0, 0, 0, 0));
        vb.getChildren().add(createButton("START"));
        vb.getChildren().add(createButton("EXIT"));
        root.getChildren().add(vb);
    }

    private Button createButton(String text)
    {
        Button result = new Button();
        result.setText(text);
        result.getStyleClass().add("menuBtn");
        result.setFont(this.menuFont);
        // Set some padding on the buttons, and set their width to 200px.
        result.setPadding(new Insets(5, 10, 5, 10));
        result.setPrefWidth(200);
        // Make sure all buttons are the same size.
        result.setMaxWidth(Double.MAX_VALUE);
        return result;
    }

    @Override
    public void handle(long now) {
        // Fill whole screen with a dark blue color.
        graphicsContext.setFill(Color.web("#0D267A"));
        graphicsContext.fillRect(0, 0, width, height);

        // Draw title text.
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(this.titleFont);
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.fillText("TRUMP\nCARDS", width / 2, 70);

        // Draw title screen images on the left and right of the screen.
        graphicsContext.drawImage(titleLeft, -100, 0, 404, 640);
        graphicsContext.drawImage(titleRight, width - 290, 0, 404, 640);
    }
}
