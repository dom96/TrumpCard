package TrumpCard;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

    private ColorAdjust blackAndWhite;
    private ArrayList<ImageView> characterBtns;


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

        // Create canvas to draw things onto.
        Canvas canvas = new Canvas(width, height);
        this.graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        addCharacters(root, 20, 160, new CharacterName[]{CharacterName.Ultron,
                CharacterName.Joker, CharacterName.GreenGoblin});
        addCharacters(root, 440, 160, new CharacterName[]{CharacterName.TonyStark,
                CharacterName.BruceWayne, CharacterName.PeterParker});
        addCharacters(root, 860, 160, new CharacterName[]{CharacterName.IronMan,
                CharacterName.Batman, CharacterName.Spiderman});
    }

    private void onCharacterImageMouseClicked(MouseEvent ev, CharacterName character)
    {
        // Loop through all the buttons to deselect them.
        for (ImageView view : characterBtns)
        {
            view.setEffect(blackAndWhite);
        }

        ImageView view = (ImageView)ev.getSource();
        view.setEffect(null);

        currentSelection = character;
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
        graphicsContext.setFont(Font.font("Courier New", 40));
        graphicsContext.fillText("Humans", width / 2, 140);

        // Draw heroes
        graphicsContext.setFont(Font.font("Courier New", 40));
        graphicsContext.fillText("Heroes", width - 200, 140);

    }

}
