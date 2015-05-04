package TrumpCard;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

public class GameScreen extends AnimationTimer {

    private double width;
    private double height;
    private GraphicsContext graphicsContext;
    private Label statusLabel;
    private ProgressBar actionsBar;
    private Label actionsBarLabel;
    private ProgressBar energyBar;
    private Label energyBarLabel;
    private Label scoreLabel;

    private Button sleepBtn;

    private GameState state;

    private Group root;
    private Image background;
    private Image map;
    private Image characterIcon;
    private Image homeIcon;

    private long time;
    private double glowIntensity;
    private boolean glowDecreasing;

    private Pane pausePane;

    GameScreen(double width, double height, CharacterName name, String userName, String characterHideout)
    {
        this.width = width;
        this.height = height;

        this.background = new Image("file:images/background2.jpg");
        this.characterIcon = new Image("file:images/character.png");
        this.homeIcon = new Image("file:images/home.png");

        // TODO: Difficulty
        this.state = new GameState(new Character(name, userName,
                characterHideout.isEmpty() ? "Belfast" : characterHideout), GameState.Difficulty.Hard);

        try
        {
            String darkBlueStyle = "feature:all|element:all|invert_lightness:true|" +
                    "saturation:10|lightness:-30|gamma:0.5|hue:0x0043FF";
            String googleMapsUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                    URLEncoder.encode(characterHideout, "UTF-8") + "&zoom=14&size=485x190&scale=2&format=png" +
                    "&style=" + URLEncoder.encode(darkBlueStyle, "UTF-8");

            this.map = new Image(googleMapsUrl);
        }
        catch (IOException exception)
        {
            // TODO: Use an offline map instead and show a warning.
            UIUtils.showErrorDialog("Could not load image from Google Maps.", "Error");
            System.exit(1);
        }
    }

    private void updateStatusLabel()
    {
        double actions = state.getCharacter().getActions();
        if (actions <= 100)
        {
            statusLabel.setText("Super hero");
        }

        if (actions <= 90)
        {
            statusLabel.setText("Hero");
        }

        if (actions <= 60)
        {
            statusLabel.setText("Human");
        }

        if (actions <= 40)
        {
            statusLabel.setText("Villain");
        }

        if (actions <= 10)
        {
            statusLabel.setText("Super villain");
        }
    }

    private void updateStatusBox()
    {
        updateStatusLabel();
        double actions = state.getCharacter().getActions();
        actionsBar.setProgress(actions / 100);
        actionsBarLabel.setText(String.format("%04.1f%%", actions));
        double energy = state.getCharacter().getEnergy();
        energyBar.setProgress(energy / 100);
        energyBarLabel.setText(String.format("%04.1f%%", energy));
        scoreLabel.setText(Integer.toString(state.getCharacter().getScore()));
    }

    private void updateLeftButtons()
    {
        // Check whether we can sleep.
        if (state.getCharacter().isAtHome())
        {
            sleepBtn.getStyleClass().remove("disabledBtn");
        }
        else if (!sleepBtn.getStyleClass().contains("disabledBtn"))
        {
            sleepBtn.getStyleClass().add("disabledBtn");
        }
    }

    private void updateCrimes()
    {
        for (Crime crime : state.getCrimes())
        {
            if (crime.getWindowPos().equals(state.getCharacter().getPos()))
            {
                crime.enable();
            }
            else
            {
                crime.disable();
            }
        }
    }

    private void onSleepBtnClicked(MouseEvent event)
    {
        if (!state.getCharacter().isAtHome())
        {
            UIUtils.showMessageDialog("You can only sleep at home.");
            return;
        }

        if (state.getCharacter().getStatus() == Character.CharacterStatus.Sleeping)
        {
            state.getCharacter().setStatus(Character.CharacterStatus.Still);
            sleepBtn.setText("Sleep");
        }
        else
        {
            state.getCharacter().setStatus(Character.CharacterStatus.Sleeping);
            sleepBtn.setText("Wake up");
        }
    }

    private void onPauseMenuBtnClicked(MouseEvent event)
    {
        pausePane.setVisible(true);
        pausePane.toFront();
        state.pause();
    }

    private void onResumeBtnClicked(MouseEvent event)
    {
        pausePane.setVisible(false);
        state.resume();
    }

    public void show(Stage stage)
    {
        this.root = new Group();
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add("TrumpCard/css/style.css");

        // Create canvas to draw things onto.
        Canvas canvas = new Canvas(width, height);
        this.graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Create home icon on map
        ImageView homeImage = new ImageView(homeIcon);
        homeImage.setFitWidth(30);
        homeImage.setFitHeight(28.4);
        homeImage.setLayoutX(775);
        homeImage.setLayoutY(210);
        homeImage.setCursor(Cursor.HAND);
        homeImage.setOnMouseClicked(
                event -> state.getCharacter().moveTo(state.getCharacter().getHomePos()));
        root.getChildren().add(homeImage);

        // Create widgets for showing the status
        TilePane statusBox = new TilePane();
        statusBox.setTileAlignment(Pos.CENTER_LEFT);
        statusBox.setPrefColumns(2);
        statusBox.setLayoutX(20);
        statusBox.setLayoutY(450);
        statusBox.setVgap(5);
        statusBox.setHgap(-70);
        statusBox.getStyleClass().add("statusBox");
        root.getChildren().add(statusBox);

        // Status of character (villain, hero, human).
        statusLabel = new Label();
        Font statusFont = Font.font("Courier New", 16);
        statusLabel.setFont(statusFont);
        UIUtils.createBoldLabel(statusBox, "Status: ", statusLabel, statusFont);

        // Actions progress bar (0-10: Super villain, 10 - 40: Villain ...)
        HBox actionsBox = new HBox(5);

        actionsBarLabel = new Label();
        actionsBarLabel.setAlignment(Pos.BOTTOM_CENTER);
        actionsBarLabel.setFont(Font.font("Courier New", 13));
        actionsBox.getChildren().add(actionsBarLabel);

        actionsBar = new ProgressBar();
        actionsBar.setPrefWidth(115);
        actionsBar.setPrefHeight(20);
        actionsBox.getChildren().add(actionsBar);
        UIUtils.createBoldLabel(statusBox, "Actions: ", actionsBox, statusFont);

        // Energy progress bar (shows how much energy hero has)
        HBox energyBox = new HBox(5);

        energyBarLabel = new Label();
        energyBarLabel.setAlignment(Pos.BOTTOM_CENTER);
        energyBarLabel.setFont(Font.font("Courier New", 13));
        energyBox.getChildren().add(energyBarLabel);

        energyBar = new ProgressBar();
        energyBar.setPrefWidth(115);
        energyBar.setPrefHeight(20);
        energyBox.getChildren().add(energyBar);
        UIUtils.createBoldLabel(statusBox, "Energy: ", energyBox, statusFont);

        // Score label
        scoreLabel = new Label();
        scoreLabel.setFont(Font.font("Courier New", 13));
        UIUtils.createBoldLabel(statusBox, "Score: ", scoreLabel, statusFont);

        // Set text of statusLabel, actionsBar and energyBar.
        updateStatusBox();

        // Create buttons below status box
        VBox buttonBox = new VBox(5);
        buttonBox.setLayoutX(20);
        buttonBox.setLayoutY(550);
        root.getChildren().add(buttonBox);

        // Sleep button
        ImageView sleepIcon = new ImageView(new Image("file:images/sleep.png"));
        sleepIcon.setPreserveRatio(true);
        sleepIcon.setFitWidth(30);
        sleepBtn = new Button("Sleep", sleepIcon);
        sleepBtn.setCursor(Cursor.HAND);
        sleepBtn.getStyleClass().add("sleepBtn");
        sleepBtn.setOnMouseClicked(this::onSleepBtnClicked);
        buttonBox.getChildren().add(sleepBtn);

        // Pause menu button
        Button pauseMenuBtn = new Button("Pause Menu");
        pauseMenuBtn.setCursor(Cursor.HAND);
        pauseMenuBtn.getStyleClass().add("pauseMenuBtn");
        pauseMenuBtn.setOnMouseClicked(this::onPauseMenuBtnClicked);
        buttonBox.getChildren().add(pauseMenuBtn);

        // Create pause pane
        createPauseMenu();
    }

    private void createPauseMenu() {
        // Create the pane which will darken the whole screen.
        pausePane = new Pane();
        pausePane.setMinWidth(width);
        pausePane.setMinHeight(height);
        pausePane.getStyleClass().add("pausePane");
        pausePane.setVisible(false);
        pausePane.setLayoutX(0);
        pausePane.setLayoutY(0);
        root.getChildren().add(pausePane);

        VBox menuBox = new VBox(10);
        menuBox.setLayoutX(590);
        menuBox.setLayoutY(300);
        pausePane.getChildren().add(menuBox);

        // Resume button
        Button resumeBtn = new Button("Resume");
        resumeBtn.setCursor(Cursor.HAND);
        resumeBtn.getStyleClass().add("resumeBtn");
        resumeBtn.setOnMouseClicked(this::onResumeBtnClicked);
        menuBox.getChildren().add(resumeBtn);
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
        BoxBlur blur = new BoxBlur();
        blur.setWidth(100);
        blur.setHeight(50);
        blur.setIterations(3);
        graphicsContext.setEffect(blur);
        graphicsContext.setFill(Color.web("#ff0000"));
        graphicsContext.fillRect(80, 370 + (20*glowIntensity), 130, 35);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setEffect(null);
        graphicsContext.setFont(Font.font("Courier New", 20));
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.fillText(this.state.getCharacter().getFriendlyName(), 145, 425, 240);

        // Draw character
        //graphicsContext.setEffect(new Glow(glowIntensity*0.4));
        graphicsContext.drawImage(state.getCharacter().getImage(), 20, 20, 250, 380);

        // Draw map
        graphicsContext.drawImage(this.map, 290, 20, 970, 380);

        // Draw character on map
        if (!state.getCharacter().isAtHome() && state.getCrimeAtCharacterPos() == null) {
            final Point2D characterPos = state.getCharacter().getPos();
            graphicsContext.drawImage(this.characterIcon, characterPos.getX(), characterPos.getY(), 30, 32);
        }

        if (!pausePane.isVisible()) {
            // Advance game
            this.state.poll(root, now);

            // Update status label, energy bar and actions bar.
            updateStatusBox();

            // Update buttons on the left of the screen.
            updateLeftButtons();

            // Update buttons in crime boxes.
            updateCrimes();
        }
    }
}
