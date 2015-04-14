package TrumpCard;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class CharacterSelection extends AnimationTimer {

    private final double width;
    private final double height;

    private GraphicsContext graphicsContext;

    private Image background;
    private Image backgroundNeutral;
    private Image backgroundVillain;
    private Image backgroundHero;

    private Image characterUltron;

    private CharacterName hoverSelection;
    private CharacterName currentSelection;
    private boolean selectionLocked;

    private ColorAdjust blackAndWhite;
    private ArrayList<ImageView> characterBtns;

    private Button playBtn;

    CharacterSelection(double width, double height)
    {
        this.width = width;
        this.height = height;

        background = new Image("file:images/character_selection.png");
        backgroundNeutral = new Image("file:images/character_selection_neutral.png");
        backgroundVillain = new Image("file:images/character_selection_evil.png");
        backgroundHero = new Image("file:images/character_selection_good.png");

        characterUltron = new Image("file:images/character_ultron.jpg");

        this.blackAndWhite = new ColorAdjust();
        this.blackAndWhite.setSaturation(-1);

        this.characterBtns = new ArrayList<ImageView>();
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

        // Create play button
        this.playBtn = UIUtils.createButton("CHOOSE", Font.font("Courier New", 25), "playBtn");
        UIUtils.addAt(root, this.playBtn, width / 2 - 100, 600);
        this.playBtn.setOnAction(this::onPlayBtnClicked);
        this.playBtn.setVisible(false);

        addCharacters(root, 20, 160, new CharacterName[]{CharacterName.Ultron,
                CharacterName.Joker, CharacterName.GreenGoblin});
        addCharacters(root, 440, 160, new CharacterName[]{CharacterName.TonyStark,
                CharacterName.BruceWayne, CharacterName.PeterParker});
        addCharacters(root, 860, 160, new CharacterName[]{CharacterName.IronMan,
                CharacterName.Batman, CharacterName.Spiderman});
    }

    private void onPlayBtnClicked(ActionEvent ev)
    {
        this.selectionLocked = true;
        this.playBtn.setText("PLAY");

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
            // TODO: Other character images.
            switch (c)
            {
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
        if (CharacterName.isVillain(selection))
        {
            return new ImagePattern(backgroundVillain);
        }

        if (CharacterName.isHuman(selection))
        {
            return new ImagePattern(backgroundNeutral);
        }

        if (CharacterName.isHero(selection))
        {
            return new ImagePattern(backgroundHero);
        }

        return new ImagePattern(background);
    }

    private void drawInfo(CharacterName selection)
    {
        String alignment = CharacterName.getAlignment(selection);
        String name = CharacterName.getFriendlyName(selection);

        graphicsContext.setFont(Font.font("Courier New", 20));
        graphicsContext.setTextAlign(TextAlignment.CENTER);

        graphicsContext.fillText(name + " - " + alignment, width / 2, 380);

        if (!selectionLocked) {
            graphicsContext.setFont(Font.font("Courier New", 18));
            graphicsContext.setTextAlign(TextAlignment.LEFT);
            graphicsContext.fillText(CharacterName.getDescription(selection),
                    20, 410);

            graphicsContext.fillText("Strength: " + CharacterName.getStrength(selection),
                    width / 2 - 140, 410);
            graphicsContext.fillText("Intelligence: " + CharacterName.getIntelligence(selection),
                    width / 2 - 140, 430);
            graphicsContext.fillText("Durability: " + CharacterName.getDurability(selection),
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
