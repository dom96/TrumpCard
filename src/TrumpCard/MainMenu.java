package TrumpCard;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;


public class MainMenu extends AnimationTimer {

    private GraphicsContext graphicsContext;
    private double width;
    private double height;
    private Stage stage;

    private Image titleLeft;
    private Image titleRight;
    private Font titleFont;
    private Font menuFont;

    MainMenu(double width, double height, Stage stage)
    {
        this.stage = stage;
        this.width = width;
        this.height = height;

        this.titleLeft = new Image("file:images/title_left_small.jpg");
        this.titleRight = new Image("file:images/title_right_small.jpg");
        if (this.titleLeft.isError() || this.titleRight.isError())
        {
            UIUtils.showErrorDialog("Could not load title images.", "Error");
            System.exit(1);
        }

        this.titleFont = Font.font("HAGANE", 60);
        this.menuFont = Font.font("Courier New", 40);

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add("TrumpCard/css/style.css");

        // Create canvas to draw things onto.
        Canvas canvas = new Canvas(width, height);
        this.graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Create a Vertical Box to hold the main menu buttons.
        VBox vb = new VBox();
        vb.setLayoutX(width / 2 - 100);
        vb.setLayoutY(250);
        vb.setSpacing(20);
        vb.setPadding(new Insets(0, 0, 0, 0));

        Button startBtn = UIUtils.createButton("START", this.menuFont, "menuBtn");
        startBtn.setOnAction(this::onStartBtnAction);
        startBtn.setOnMouseEntered(this::onMouseEnter);
        vb.getChildren().add(startBtn);

        Button loadBtn = UIUtils.createButton("LOAD", this.menuFont, "menuBtn");
        loadBtn.setOnAction(this::onLoadBtnAction);
        loadBtn.setOnMouseEntered(this::onMouseEnter);
        vb.getChildren().add(loadBtn);

        Button exitBtn = UIUtils.createButton("EXIT", this.menuFont, "menuBtn");
        exitBtn.setOnAction(this::onExitBtnAction);
        exitBtn.setOnMouseEntered(this::onMouseEnter);
        vb.getChildren().add(exitBtn);
        root.getChildren().add(vb);
    }

    private void onStartBtnAction(ActionEvent event) {
        CharacterSelection cs = new CharacterSelection(width, height);
        cs.show(stage);
        cs.start();
    }

    private void onLoadBtnAction(ActionEvent event) {
        GameScreen game = GameScreen.loadFromFile(width, height);
        game.show(stage);
        game.start();
    }

    private void onExitBtnAction(ActionEvent event) {
        System.exit(0);
    }

    private void onMouseEnter(MouseEvent event) {
        AudioClip clip = new AudioClip(new File("sounds/hover.wav").toURI().toString());
        clip.play();
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
        graphicsContext.drawImage(titleLeft, -100, 0, 455, 720);
        graphicsContext.drawImage(titleRight, width - 350, 0, 455, 720);
    }
}
