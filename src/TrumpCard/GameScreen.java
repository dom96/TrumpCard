package TrumpCard;

import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GameScreen extends AnimationTimer {

    private double width;
    private double height;
    private GraphicsContext graphicsContext;

    private GameState state;

    private Image background;

    private long time;
    private double glowIntensity;
    private boolean glowDecreasing;

    GameScreen(double width, double height, CharacterName name, String userName, String characterHideout)
    {
        this.width = width;
        this.height = height;

        this.background = new Image("file:images/background2.jpg");

        this.state = new GameState(new Character(name, userName, characterHideout));
    }

    public void show(Stage stage)
    {
        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add("TrumpCard/css/style.css");

        // Create canvas to draw things onto.
        Canvas canvas = new Canvas(width, height);
        this.graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
    }

    @Override
    public void handle(long now) {
        if (now - time >= 50000000)
        {
            if (glowDecreasing)
            {
                glowIntensity -= 0.1;
                if (glowIntensity <= 0.0)
                {
                    glowDecreasing = false;
                }
            }
            else
            {
                glowIntensity += 0.1;
                if (glowIntensity >= 1.0)
                {
                    glowDecreasing = true;
                }
            }
            time = now;
        }


        graphicsContext.setEffect(null);
        // Draw background
        graphicsContext.drawImage(background, 0, 0, width, height);

        // Draw name badge
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#ff0000"));
        shadow.setWidth(20);
        shadow.setHeight(20);
        shadow.setRadius(110);
        shadow.setSpread(0.96);
        BoxBlur blur = new BoxBlur();
        blur.setWidth(100);
        blur.setHeight(50);
        blur.setIterations(3);
        graphicsContext.setEffect(blur);
        graphicsContext.setFill(Color.web("#ff0000"));
        graphicsContext.fillRect(80, 390, 130, 35);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setEffect(null);
        graphicsContext.setFont(Font.font("Courier New", 20));
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.fillText(this.state.getCharacter().getFriendlyName(), 145, 425, 240);

        // Draw character
        //graphicsContext.setEffect(new Glow(glowIntensity*0.4));
        graphicsContext.drawImage(state.getCharacter().getImage(), 20, 20, 250, 380);


    }
}
