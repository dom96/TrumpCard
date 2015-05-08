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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
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
    private Label crimeLikelihoodLabel;

    private Button sleepBtn;
    private Button shopBtn;

    private Label errorLabel; // Shown at bottom of screen if user tries to do something forbidden.

    private GameState state;

    private Group root;
    private Image background;
    private Image map;
    private Image characterIcon;
    private Image homeIcon;
    private Image shopIcon;

    private long time;
    private double glowIntensity;
    private boolean glowDecreasing;

    private Pane pausePane;
    private VBox menuBox;
    // Controls associated with the end of the game.
    private VBox endGameBox;
    private Label infoLbl; // Label showing information to user once game finishes.
    // Controls associated with the shop.
    private Shop shop;

    private MediaPlayer music;

    GameScreen(double width, double height, CharacterName name, String userName, String characterHideout,
               GameState.Difficulty difficulty)
    {
        this.width = width;
        this.height = height;

        this.background = UIUtils.loadImage("file:images/background2.jpg");
        this.characterIcon = UIUtils.loadImage("file:images/character.png");
        this.homeIcon = UIUtils.loadImage("file:images/home.png");
        this.shopIcon = UIUtils.loadImage("file:images/shop.png");

        this.state = new GameState(new Character(name, userName,
                characterHideout.isEmpty() ? "Belfast" : characterHideout), difficulty);

        this.shop = new Shop(state);

        try
        {
            String darkBlueStyle = "feature:all|element:all|invert_lightness:true|" +
                    "saturation:10|lightness:-30|gamma:0.5|hue:0x0043FF";
            String googleMapsUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                    URLEncoder.encode(state.getCharacter().getHideout(), "UTF-8") +
                    "&zoom=14&size=485x190&scale=2&format=png" +
                    "&style=" + URLEncoder.encode(darkBlueStyle, "UTF-8");

            this.map = new Image(googleMapsUrl);
        }
        catch (IOException exception)
        {
            // TODO: Use an offline map instead and show a warning.
            UIUtils.showErrorDialog("Could not load image from Google Maps.", "Error");
            System.exit(1);
        }

        // Music
        music = new MediaPlayer(new Media(
                new File("sounds/Samus Aran - The Bounty of a Brain.mp3").toURI().toString()));
        music.setVolume(0.3);
        music.play();
    }

    /**
     * Load game from saved file.
     */
    public static GameScreen loadFromFile(double width, double height)
    {
        GameState state = GameState.load();
        GameScreen result = new GameScreen(width, height, state.getCharacter().getName(),
                state.getCharacter().getUserName(), state.getCharacter().getHideout(), state.getDifficulty());
        result.state = state;
        return result;
    }

    private void updateStatusLabel()
    {
        double actions = state.getCharacter().getActions();
        String status = "";
        if (actions <= 100)
        {
            status = "Super hero";
        }

        if (actions < 90)
        {
            status = "Hero";
        }

        if (actions < 60)
        {
            status = "Human";
        }

        if (actions < 40)
        {
            status = "Villain";
        }

        if (actions <= 10)
        {
            status = "Super villain";
        }

        if (state.getCharacter().getStatus() == Character.CharacterStatus.Sleeping)
        {
            status = status + " (Asleep)";
            statusLabel.setFont(Font.font("Courier New", 12));
        }
        else
        {
            statusLabel.setFont(Font.font("Courier New", 16));
        }

        statusLabel.setText(status);
    }

    private void updateStatusBox(long now)
    {
        updateStatusLabel();

        double actions = state.getCharacter().getActions();
        actionsBar.setProgress(actions / 100);
        actionsBarLabel.setText(String.format("%04.1f%%", actions));

        double energy = state.getCharacter().getEnergy();
        energyBar.setProgress(energy / 100);
        energyBarLabel.setText(String.format("%04.1f%%", energy));

        scoreLabel.setText(Integer.toString(state.getCharacter().getScore()));

        double crimeLikelihood = state.crimeLikelihood(now);
        crimeLikelihoodLabel.setText(String.format("%04.1f%%", crimeLikelihood));
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

        // Check whether we can shop.
        if (state.isCharacterAtShop())
        {
            shopBtn.getStyleClass().remove("disabledBtn");
        }
        else if (!shopBtn.getStyleClass().contains("disabledBtn"))
        {
            shopBtn.getStyleClass().add("disabledBtn");
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

    private void checkForEnd()
    {
        // Check if we should end the game.
        if (state.getCharacter().getEnergy() == 0.0)
        {
            showPausePane();
            endGameBox.setVisible(true);
            infoLbl.setText("You finished with " +
                    state.getCharacter().getScore() + " points.");
        }
    }

    public void showPausePane() {
        pausePane.setVisible(true);
        pausePane.toFront();
        endGameBox.setVisible(false);
        menuBox.setVisible(false);
        shop.getShopBox().setVisible(false);
        state.pause();
    }

    private void onSleepBtnClicked(MouseEvent event)
    {
        if (!state.getCharacter().isAtHome())
        {
            UIUtils.showErrorLabel(errorLabel, "You can only sleep at home.");
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

    private void onShopBtnClicked(MouseEvent event)
    {
        if (!state.isCharacterAtShop())
        {
            UIUtils.showErrorLabel(errorLabel, "Your character is not at the shop.");
            return;
        }

        shop.showShop(this, pausePane);
    }

    private void onPauseMenuBtnClicked(MouseEvent event)
    {
        showPausePane();
        menuBox.setVisible(true);
    }

    private void onResumeBtnClicked(MouseEvent event)
    {
        pausePane.setVisible(false);
        state.resume();
    }

    private void onKeyboardReleased(KeyEvent event)
    {
        switch (event.getCode())
        {
            case A:
                double actions = state.getCharacter().getActions();
                if (event.isControlDown())
                {
                    actions--;
                }
                else
                {
                    actions++;
                }
                state.getCharacter().setActions(actions);
                break;
            case C:
                // Toggle generation of crimes.
                if (state.getCharacter().isPaused())
                {
                    state.resume();
                }
                else {
                    state.pause();
                }
                break;
            case ESCAPE:
                // Toggle pause menu.
                if (pausePane.isVisible())
                {
                    onResumeBtnClicked(null);
                }
                else
                {
                    onPauseMenuBtnClicked(null);
                }
                break;
            case S:
                // Shop
                shop.showShop(this, pausePane);
                break;
            case P:
                // Save game state
                state.save();
                break;
            case L:
                // Load game state
                for (Crime c : state.getCrimes())
                {
                    c.destroy(root);
                }
                state = GameState.load();
                break;
        }
    }

    private void onShopIconClicked(MouseEvent event)
    {
        if (state.getCharacter().getStatus() == Character.CharacterStatus.Sleeping)
        {
            UIUtils.showErrorLabel(errorLabel, "Cannot move whilst sleeping.");
            return;
        }

        state.getCharacter().moveTo(state.getShopPos());
    }

    public void show(Stage stage)
    {
        this.root = new Group();
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add("TrumpCard/css/style.css");

        // Events on root.
        root.setOnKeyReleased(this::onKeyboardReleased);

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

        // Create shop icon on map
        ImageView shopImage = new ImageView(shopIcon);
        shopImage.setFitWidth(30);
        shopImage.setFitHeight(28.4);
        shopImage.setLayoutX(295);
        shopImage.setLayoutY(25);
        shopImage.setCursor(Cursor.HAND);
        shopImage.setOnMouseClicked(this::onShopIconClicked);
        root.getChildren().add(shopImage);

        // Create widgets for showing the status
        TilePane statusBox = new TilePane();
        statusBox.setTileAlignment(Pos.CENTER_LEFT);
        statusBox.setPrefColumns(2);
        statusBox.setLayoutX(20);
        statusBox.setLayoutY(440);
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

        // Crime likelihood label
        crimeLikelihoodLabel = new Label();
        crimeLikelihoodLabel.setFont(Font.font("Courier New", 13));
        UIUtils.createBoldLabel(statusBox, "Crime: ", crimeLikelihoodLabel, statusFont);

        // Set text of statusLabel, actionsBar and energyBar.
        updateStatusBox(0);

        // Create buttons below status box
        VBox buttonBox = new VBox(5);
        buttonBox.setLayoutX(20);
        buttonBox.setLayoutY(570);
        root.getChildren().add(buttonBox);

        // Sleep button
        ImageView sleepIcon = new ImageView(new Image("file:images/sleep.png"));
        sleepIcon.setPreserveRatio(true);
        sleepIcon.setFitWidth(30);
        String sleepBtnText = "Sleep";
        // In case the game was reloaded from a state where the player was sleeping.
        if (state.getCharacter().getStatus() == Character.CharacterStatus.Sleeping)
        {
            sleepBtnText = "Wake up";
        }
        sleepBtn = new Button(sleepBtnText, sleepIcon);
        sleepBtn.setCursor(Cursor.HAND);
        sleepBtn.getStyleClass().add("sleepBtn");
        sleepBtn.setOnMouseClicked(this::onSleepBtnClicked);
        buttonBox.getChildren().add(sleepBtn);

        // Shop button
        shopBtn = new Button("Shop");
        shopBtn.setCursor(Cursor.HAND);
        shopBtn.getStyleClass().addAll("sleepBtn", "disabledBtn");
        shopBtn.setOnMouseClicked(this::onShopBtnClicked);
        buttonBox.getChildren().add(shopBtn);

        // Create error label at bottom of screen.
        errorLabel = new Label("");
        errorLabel.setFont(Font.font("Courier New", 18));
        errorLabel.getStyleClass().add("error");
        errorLabel.setLayoutX(290);
        errorLabel.setLayoutY(650);
        errorLabel.setVisible(false);
        root.getChildren().add(errorLabel);

        // Pause menu button
        Button pauseMenuBtn = new Button("Pause Menu");
        pauseMenuBtn.setCursor(Cursor.HAND);
        pauseMenuBtn.getStyleClass().add("pauseMenuBtn");
        pauseMenuBtn.setOnMouseClicked(this::onPauseMenuBtnClicked);
        buttonBox.getChildren().add(pauseMenuBtn);

        // Create pause pane, also includes the buttons which are shown once game ends.
        createPauseMenu(stage);
    }

    private void createPauseMenu(Stage stage) {
        // Create the pane which will darken the whole screen.
        pausePane = new Pane();
        pausePane.setMinWidth(width);
        pausePane.setMinHeight(height);
        pausePane.getStyleClass().add("pausePane");
        pausePane.setVisible(false);
        pausePane.setLayoutX(0);
        pausePane.setLayoutY(0);
        root.getChildren().add(pausePane);

        // Menu box
        menuBox = new VBox(10);
        menuBox.setLayoutX(590);
        menuBox.setLayoutY(300);
        pausePane.getChildren().add(menuBox);

        // Resume button
        Button resumeBtn = new Button("Resume");
        resumeBtn.setCursor(Cursor.HAND);
        resumeBtn.getStyleClass().add("resumeBtn");
        resumeBtn.setOnMouseClicked(this::onResumeBtnClicked);
        menuBox.getChildren().add(resumeBtn);

        // Save game button
        Button saveBtn = new Button("Save Game");
        saveBtn.setCursor(Cursor.HAND);
        saveBtn.getStyleClass().add("resumeBtn");
        saveBtn.setOnMouseClicked(event -> {
            state.save();
            onResumeBtnClicked(event);
        });
        menuBox.getChildren().add(saveBtn);

        // Main menu
        Button mainMenuBtn = new Button("Go to Main Menu");
        mainMenuBtn.setCursor(Cursor.HAND);
        mainMenuBtn.getStyleClass().add("resumeBtn");
        mainMenuBtn.setOnMouseClicked(event -> {
            MainMenu mainMenu = new MainMenu(1280, 720, stage);
            mainMenu.start();
        });
        menuBox.getChildren().add(mainMenuBtn);

        // Controls associated with the end of the game.
        // End game box
        endGameBox = new VBox(10);
        endGameBox.setAlignment(Pos.CENTER);
        endGameBox.setLayoutX(330);
        endGameBox.setLayoutY(260);
        pausePane.getChildren().add(endGameBox);

        // More info label
        Label infoMsgLbl = new Label("Your energy ran out!");
        infoMsgLbl.setTextAlignment(TextAlignment.CENTER);
        infoMsgLbl.setFont(Font.font("Courier New", 56));
        endGameBox.getChildren().add(infoMsgLbl);

        // Info label showing your score
        infoLbl = new Label();
        infoLbl.setTextAlignment(TextAlignment.CENTER);
        infoLbl.setFont(Font.font("Courier New", 40));
        endGameBox.getChildren().add(infoLbl);

        // Main menu button
        Button mainMenuBtn1 = new Button("Go to Main Menu");
        mainMenuBtn1.setCursor(Cursor.HAND);
        mainMenuBtn1.getStyleClass().add("resumeBtn");
        mainMenuBtn1.setOnMouseClicked(event -> {
            MainMenu mainMenu = new MainMenu(1280, 720, stage);
            mainMenu.start();
        });
        endGameBox.getChildren().add(mainMenuBtn1);

        // Initialise shop controls.
        shop.create(pausePane);
    }

    @Override
    public void handle(long now) {
        // Animate gradient below name.
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
        // Set color under name badge based on the alignment of the character.
        if (state.getCharacter().getName().isVillain())
        {
            graphicsContext.setFill(Color.web("#ff0000"));
        }
        else if (state.getCharacter().getName().isHuman())
        {
            graphicsContext.setFill(Color.web("#0059FF"));
        }
        else
        {
            graphicsContext.setFill(Color.web("#0AD300"));
        }
        graphicsContext.fillRect(80, 370 + (20*glowIntensity), 130, 35);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setEffect(null);
        graphicsContext.setFont(Font.font("Courier New", 20));
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.fillText(this.state.getCharacter().getUserName(), 145, 425, 240);

        // Draw character
        //graphicsContext.setEffect(new Glow(glowIntensity*0.4));
        graphicsContext.drawImage(state.getCharacter().getImage(), 20, 20, 250, 380);

        // Draw map
        graphicsContext.drawImage(this.map, 290, 20, 970, 380);

        // Draw character on map
        // We only draw it if it's not at the same position as the hideout, a crime or the shop. This way it looks
        // as if the character enters that landmark.
        if (!state.getCharacter().isAtHome() && state.getCrimeAtCharacterPos() == null && !state.isCharacterAtShop()) {
            final Point2D characterPos = state.getCharacter().getPos();
            Image icon = characterIcon;
            // Check if clothing item is changing this.
            if (state.getCharacter().getClothing().length() > 0)
            {
                icon = UIUtils.loadImage("file:images/character_" + state.getCharacter().getClothing() + ".png");
            }
            graphicsContext.drawImage(icon, characterPos.getX(), characterPos.getY(), 30, 32);
        }

        if (!pausePane.isVisible()) {
            // Advance game
            this.state.poll(root, now, errorLabel);

            // Update status label, energy bar and actions bar.
            updateStatusBox(now);

            // Update buttons on the left of the screen.
            updateLeftButtons();

            // Update buttons in crime boxes.
            updateCrimes();

            // Check whether game ended.
            checkForEnd();

            // Make sure music is playing
            if (music.getStatus() == MediaPlayer.Status.PAUSED)
            {
                music.play();
            }
        }
        else
        {
            // Make sure music is paused
            if (music.getStatus() == MediaPlayer.Status.PLAYING)
            {
                music.pause();
            }
        }
    }
}
