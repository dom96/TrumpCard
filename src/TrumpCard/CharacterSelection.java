package TrumpCard;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.ArrayList;

public class CharacterSelection extends AnimationTimer {

    private final double width;
    private final double height;
    private Stage stage;

    private GraphicsContext graphicsContext;

    private Image background;
    private Image backgroundNeutral;
    private Image backgroundVillain;
    private Image backgroundHero;

    private Image characterUltron;
    private Image characterIronman;
    private Image characterBatman;
    private Image characterSpiderman;
    private Image characterTonyStark;
    private Image characterBruceWayne;
    private Image characterPeterParker;
    private Image characterCatwoman;
    private Image characterGreenGoblin;

    private CharacterName hoverSelection;
    private CharacterName currentSelection;
    private boolean selectionLocked;

    private ColorAdjust blackAndWhite;
    private ArrayList<ImageView> characterBtns;

    private Button playBtn;
    private VBox inputControls;

    private Label errorLbl;
    private ComboBox<GameState.Difficulty> difficultyChoice;
    private TextField addressField;
    private TextField nameField;


    CharacterSelection(double width, double height)
    {
        this.width = width;
        this.height = height;

        background = new Image("file:images/character_selection.png");
        backgroundNeutral = new Image("file:images/character_selection_neutral.png");
        backgroundVillain = new Image("file:images/character_selection_evil.png");
        backgroundHero = new Image("file:images/character_selection_good.png");

        characterUltron = CharacterName.Ultron.loadImage();
        characterIronman = CharacterName.IronMan.loadImage();
        characterBatman = CharacterName.Batman.loadImage();
        characterSpiderman = CharacterName.Spiderman.loadImage();
        characterTonyStark = CharacterName.TonyStark.loadImage();
        characterBruceWayne = CharacterName.BruceWayne.loadImage();
        characterPeterParker = CharacterName.PeterParker.loadImage();
        characterCatwoman = CharacterName.Catwoman.loadImage();
        characterGreenGoblin = CharacterName.GreenGoblin.loadImage();

        this.blackAndWhite = new ColorAdjust();
        this.blackAndWhite.setSaturation(-1);

        this.characterBtns = new ArrayList<ImageView>();
    }

    private void onKeyboardReleased(KeyEvent event)
    {
        switch (event.getCode())
        {
            case ESCAPE:
                // Go back to main menu.
                MainMenu mainMenu = new MainMenu(1280, 720, stage);
                mainMenu.start();
                break;
        }
    }

    public void show(Stage stage)
    {
        Group root = new Group();

        root.setOnKeyReleased(this::onKeyboardReleased);

        this.stage = stage;
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add("TrumpCard/css/style.css");
        stage.getScene().setOnKeyReleased(this::onKeyboardReleased);

        // Create canvas to draw things onto.
        Canvas canvas = new Canvas(width, height);
        this.graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Create play button
        this.playBtn = UIUtils.createButton("CHOOSE", Font.font("Courier New", 25), "playBtn");
        UIUtils.addAt(root, this.playBtn, width / 2 - 100, 630);
        this.playBtn.setOnAction(this::onPlayBtnClicked);
        this.playBtn.setVisible(false);

        // Create Vertical Box to hold input fields and labels.
        this.inputControls = new VBox();
        inputControls.setLayoutX(width / 2 - 100);
        inputControls.setLayoutY(400);
        inputControls.setSpacing(10);
        inputControls.setVisible(false);
        root.getChildren().add(inputControls);

        // -- Name
        Label nameLbl = new Label("Your Name");
        nameLbl.setFont(Font.font("Courier New", 20));
        inputControls.getChildren().add(nameLbl);

        nameField = new TextField();
        nameField.setPromptText("e.g. Bob");
        inputControls.getChildren().add(nameField);

        // -- Address
        Label addressLbl = new Label("Character hideout");
        addressLbl.setFont(Font.font("Courier New", 20));
        inputControls.getChildren().add(addressLbl);

        addressField = new TextField();
        addressField.setPromptText("e.g. BT71NN or London");
        inputControls.getChildren().add(addressField);

        // -- Difficulty
        Label difficultyLbl = new Label("Difficulty");
        difficultyLbl.setFont(Font.font("Courier New", 20));
        inputControls.getChildren().add(difficultyLbl);

        difficultyChoice = new ComboBox<GameState.Difficulty>();
        difficultyChoice.getItems().addAll(GameState.Difficulty.Easy, GameState.Difficulty.Medium,
                GameState.Difficulty.Hard);
        difficultyChoice.getSelectionModel().select(GameState.Difficulty.Medium);
        inputControls.getChildren().add(difficultyChoice);

        // -- Error label
        errorLbl = new Label();
        errorLbl.getStyleClass().add("error");
        errorLbl.setFont(Font.font("Courier New", 13));
        errorLbl.setVisible(false);
        inputControls.getChildren().add(errorLbl);

        // Add character images.
        addCharacters(root, 20, 160, new CharacterName[]{CharacterName.Ultron,
                CharacterName.Catwoman, CharacterName.GreenGoblin});
        addCharacters(root, 440, 160, new CharacterName[]{CharacterName.TonyStark,
                CharacterName.BruceWayne, CharacterName.PeterParker});
        addCharacters(root, 860, 160, new CharacterName[]{CharacterName.IronMan,
                CharacterName.Batman, CharacterName.Spiderman});
    }

    private void onPlayBtnClicked(ActionEvent ev)
    {
        if (!selectionLocked) {
            this.selectionLocked = true;
            this.playBtn.setText("PLAY");
            this.inputControls.setVisible(true);
        }
        else
        {
            String userName = nameField.getText();
            String characterHideout = addressField.getText();
            GameState.Difficulty difficulty = difficultyChoice.getValue();

            // Make sure the name is specified. Character hideout does not need to be specified.
            if (userName.isEmpty())
            {
                errorLbl.setText("Please specify your name.");
                errorLbl.setVisible(true);
                return;
            }

            // If the required data is valid we can move onto the game screen.
            GameScreen game = new GameScreen(width, height, currentSelection, userName, characterHideout, difficulty);
            game.show(stage);
            game.start();
        }

    }

    private void onCharacterImageMouseClicked(MouseEvent ev, CharacterName character)
    {
        if (this.selectionLocked)
        {
            // We don't want the user to be able to select a different character after they
            // already chose one.
            return;
        }

        // Loop through all the buttons to deselect them.
        for (ImageView view : characterBtns)
        {
            view.setEffect(blackAndWhite);
        }

        ImageView view = (ImageView)ev.getSource();
        view.setEffect(null);

        currentSelection = character;

        // Show play button so the user can move to the next stage.
        // At this point the button text is "CHOOSE".
        this.playBtn.setVisible(true);
    }

    private void onCharacterImageMouseEnter(MouseEvent ev, CharacterName character)
    {
        ImageView view = (ImageView)ev.getSource();
        view.setEffect(null);

        hoverSelection = character;

        // Hover button sound effect.
        AudioClip clip = new AudioClip(new File("sounds/hover.wav").toURI().toString());
        clip.play();
    }

    private void onCharacterImageMouseExit(MouseEvent ev, CharacterName character)
    {
        ImageView view = (ImageView)ev.getSource();

        // Make sure this character isn't selected.
        if (currentSelection != character) {
            view.setEffect(this.blackAndWhite);
        }

        hoverSelection = null;
    }

    private void addCharacters(Group root, double x, double y, CharacterName[] characters)
    {
        HBox hb = new HBox();
        hb.setLayoutX(x);
        hb.setLayoutY(y);
        hb.setSpacing(5);

        for (CharacterName c : characters)
        {
            ImageView view;
            switch (c)
            {
                case Ultron:
                    view = new ImageView(characterUltron);
                    break;
                case IronMan:
                    view = new ImageView(characterIronman);
                    break;
                case Batman:
                    view = new ImageView(characterBatman);
                    break;
                case Spiderman:
                    view = new ImageView(characterSpiderman);
                    break;
                case TonyStark:
                    view = new ImageView(characterTonyStark);
                    break;
                case BruceWayne:
                    view = new ImageView(characterBruceWayne);
                    break;
                case PeterParker:
                    view = new ImageView(characterPeterParker);
                    break;
                case Catwoman:
                    view = new ImageView(characterCatwoman);
                    break;
                case GreenGoblin:
                    view = new ImageView(characterGreenGoblin);
                    break;
                default:
                    view = new ImageView(characterUltron);
            }
            view.setFitWidth(130);
            view.setPreserveRatio(true);
            view.setOnMouseEntered(event -> this.onCharacterImageMouseEnter(event, c));
            view.setOnMouseExited(event -> this.onCharacterImageMouseExit(event, c));
            view.setOnMouseClicked(event -> this.onCharacterImageMouseClicked(event, c));

            view.setEffect(blackAndWhite);

            characterBtns.add(view);
            hb.getChildren().add(view);
        }

        root.getChildren().add(hb);
    }

    private ImagePattern setBackground(CharacterName selection)
    {
        if (selection.isVillain())
        {
            return new ImagePattern(backgroundVillain);
        }

        if (selection.isHuman())
        {
            return new ImagePattern(backgroundNeutral);
        }

        if (selection.isHero())
        {
            return new ImagePattern(backgroundHero);
        }

        return new ImagePattern(background);
    }

    private void drawInfo(CharacterName selection)
    {
        String alignment = selection.getAlignment();
        String name = selection.getFriendlyName();

        graphicsContext.setFont(Font.font("Courier New", 20));
        graphicsContext.setTextAlign(TextAlignment.CENTER);

        graphicsContext.fillText(name + " - " + alignment, width / 2, 380);

        if (!selectionLocked) {
            graphicsContext.setFont(Font.font("Courier New", 18));
            graphicsContext.setTextAlign(TextAlignment.LEFT);
            graphicsContext.fillText(selection.getDescription(),
                    20, 410);

            graphicsContext.fillText("Strength: " + selection.getStrength(),
                    width / 2 - 140, 410);
            graphicsContext.fillText("Intelligence: " + selection.getIntelligence(),
                    width / 2 - 140, 430);
            graphicsContext.fillText("Durability: " + selection.getDurability(),
                    width / 2 - 140, 450);
        }
    }

    @Override
    public void handle(long now) {
        // Fill whole screen with a background corresponding to the character choice.
        ImagePattern bgPattern = new ImagePattern(background);

        if (currentSelection != null)
        {
            bgPattern = setBackground(currentSelection);
        }

        if (hoverSelection != null)
        {
            bgPattern = setBackground(hoverSelection);
        }

        graphicsContext.setFill(bgPattern);
        graphicsContext.fillRect(0, 0, width, height);

        // Draw title text.
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("HAGANE", 60));
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.fillText("Choose a character", width / 2, 70);

        // Draw villains
        graphicsContext.setFont(Font.font("Courier New", 40));
        graphicsContext.fillText("Villains", 200, 140);

        // Draw humans
        graphicsContext.fillText("Humans", width / 2, 140);

        // Draw heroes
        graphicsContext.fillText("Heroes", width - 200, 140);


        if (hoverSelection != null)
        {
            drawInfo(hoverSelection);
        }
        else if (currentSelection != null)
        {
            drawInfo(currentSelection);
        }

    }

}
