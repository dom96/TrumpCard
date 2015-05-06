package TrumpCard;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Random;

public class Crime {
    private CrimeInfo info;
    private int countdown;
    private Point2D pos; // Coordinates of this crime on the map.
    private double expiresTimer;
    private boolean enabled;
    private boolean destroyed;
    private boolean paused;

    private ImageView crimeImageView;
    private VBox box;
    private TranslateTransition move;

    private Button leftBtn;
    private Button rightBtn;
    private Button ignoreBtn;

    private Label expiresInDesc;

    Crime(CrimeInfo info, Point2D pos) {
        this.info = info;
        this.pos = pos;

        this.countdown = 10;
    }

    public Point2D getWindowPos() {
        return new Point2D(pos.getX() + 290, pos.getY() + 20);
    }

    /**
     * Calculates the amount of energy that this crime will use.
     * @return
     */
    public int getEnergyUse(GameState.Difficulty difficulty) {
        switch (difficulty)
        {
            case Easy:
                return info.energyUse / 4;
            case Medium:
                return info.energyUse / 2;
            case Hard:
                return info.energyUse;
            default:
                return info.energyUse;
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void enable() {
        enabled = true;

        // Enable all the buttons.
        leftBtn.getStyleClass().remove("disabledBtn");
        rightBtn.getStyleClass().remove("disabledBtn");
    }

    public void disable() {
        if (enabled) {
            enabled = false;
            leftBtn.getStyleClass().add("disabledBtn");
            rightBtn.getStyleClass().add("disabledBtn");
        }
    }

    public void pause() {
        paused = true;
        if (move != null && move.getStatus().equals(Animation.Status.RUNNING)) {
            move.pause();
        }
    }

    public void resume() {
        paused = false;
        if (move != null && move.getStatus().equals(Animation.Status.PAUSED))
        {
            move.play();
        }
    }

    private void onCrimeImageClicked(MouseEvent event, GameState state, Label errorLabel) {
        if (state.getCharacter().getStatus() == Character.CharacterStatus.Sleeping) {
            UIUtils.showErrorLabel(errorLabel, "Cannot move whilst sleeping.");
            return;
        }

        state.getCharacter().moveTo(getWindowPos());
    }

    private void onCommitFightBtnClicked(MouseEvent event, GameState state, Group root, Label errorLabel, boolean fight)
    {
        if (!enabled)
        {
            UIUtils.showErrorLabel(errorLabel, "Your character must be at the location of the crime.");
            return;
        }

        if (getEnergyUse(state.getDifficulty()) > state.getCharacter().getEnergy())
        {
            UIUtils.showErrorLabel(errorLabel, "Your character does not have enough energy to perform this action.");
            return;
        }

        if (fight)
        {
            state.fightCrime(this, root);
        }
        else {
            state.commitCrime(this, root);
        }
    }

    private void onIgnoreBtnClicked(MouseEvent event, GameState state, Group root)
    {
        state.ignoreCrime(this, root);
    }

    public void show(GameState state, Group root, Label errorLabel) {
        this.crimeImageView = new ImageView(state.getCrimeIcon());
        this.crimeImageView.setFitWidth(100);
        this.crimeImageView.setPreserveRatio(true);
        final Point2D windowPos = getWindowPos();
        this.crimeImageView.setLayoutX(windowPos.getX() - 35);
        this.crimeImageView.setLayoutY(windowPos.getY() - 35);
        this.crimeImageView.setSmooth(true);

        root.getChildren().add(this.crimeImageView);

        ScaleTransition scale = new ScaleTransition(Duration.millis(1400), crimeImageView);
        scale.setByX(-0.70f);
        scale.setByY(-0.70f);
        scale.play();

        this.box = new VBox();
        box.setLayoutX(1280);
        box.setLayoutY(420);
        box.setPrefWidth(227.5);
        box.setPrefHeight(200);
        box.getStyleClass().add("crimeBox");
        root.getChildren().add(this.box);

        Label crimeDesc = new Label(info.getDescription());
        crimeDesc.setFont(Font.font("Courier New", 16));
        box.getChildren().add(crimeDesc);

        // Create the widgets to show properties of the crime.
        TilePane crimeFieldsBox = new TilePane();
        crimeFieldsBox.setTileAlignment(Pos.CENTER_LEFT);
        crimeFieldsBox.setPrefColumns(2);
        box.getChildren().add(crimeFieldsBox);

        Font crimeFieldsFont = Font.font("Courier New", 13);

        Label energyUseDesc = new Label(Integer.toString(info.getEnergyUse()));
        energyUseDesc.setFont(crimeFieldsFont);
        UIUtils.createBoldLabel(crimeFieldsBox, "Energy Use: ", energyUseDesc, crimeFieldsFont);

        expiresInDesc = new Label("10 seconds");
        expiresInDesc.setFont(crimeFieldsFont);
        UIUtils.createBoldLabel(crimeFieldsBox, "Expires in: ", expiresInDesc, crimeFieldsFont);

        HBox buttonBox = new HBox(5);
        buttonBox.setPadding(new Insets(80, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);
        box.getChildren().add(buttonBox);

        leftBtn = new Button("Commit");
        leftBtn.getStyleClass().addAll("commitBtn", "disabledBtn");
        leftBtn.setOnMouseClicked(event -> onCommitFightBtnClicked(event, state, root, errorLabel, false));
        rightBtn = new Button("Fight");
        rightBtn.getStyleClass().addAll("fightBtn", "disabledBtn");
        rightBtn.setOnMouseClicked(event -> onCommitFightBtnClicked(event, state, root, errorLabel, true));
        ignoreBtn = new Button("Ignore");
        ignoreBtn.getStyleClass().addAll("ignoreBtn");
        ignoreBtn.setOnMouseClicked(event -> onIgnoreBtnClicked(event, state, root));

        if (CharacterName.isVillain(state.getCharacter().getName()))
        {
            buttonBox.getChildren().addAll(leftBtn, ignoreBtn);
        }
        else if (CharacterName.isHuman(state.getCharacter().getName()))
        {
            buttonBox.getChildren().addAll(leftBtn, rightBtn, ignoreBtn);
        }
        else if (CharacterName.isHero(state.getCharacter().getName()))
        {
            buttonBox.getChildren().addAll(rightBtn, ignoreBtn);
        }

        // Event handling
        // Change color of crime box when hovering over crime image on map.
        this.crimeImageView.setOnMouseEntered(
                event -> {
                    box.getStyleClass().add("crimeHover");
                    crimeImageView.setImage(state.getCrimeIconHover());
                });
        this.crimeImageView.setOnMouseExited(
                event -> {
                    box.getStyleClass().remove("crimeHover");
                    crimeImageView.setImage(state.getCrimeIcon());
                });
        // Change color of crime image on map when hovering over crime box.
        box.setOnMouseEntered(
                event -> crimeImageView.setImage(state.getCrimeIconHover()));
        box.setOnMouseExited(
                event -> crimeImageView.setImage(state.getCrimeIcon()));
        // When crime image on map is clicked we want our character to go there.
        crimeImageView.setOnMouseClicked(
                event -> onCrimeImageClicked(event, state, errorLabel)
        );
    }

    public enum CrimeLocation {
        TrainStation, River, RailWay, Street, Home, NonSpecific
    }

    /**
     * Translates this crime's box to location `x`.
     * @param x
     */
    public void translateBox(double x) {
        if (move != null)
        {
            // If a transition exists, make sure it is stopped.
            move.stop();
        }
        double distance = x - box.getLayoutX() - box.getTranslateX();
        double time = Math.abs(distance) * 1.3;
        move = new TranslateTransition(Duration.millis(time), box);
        move.setInterpolator(Interpolator.LINEAR);
        move.setByX(distance);
        move.play();
        move.setOnFinished(event -> move = null);
    }

    /**
     * Updates the expiration label.
     * @return whether this crime has expired
     */
    public boolean update(long now) {
        if (now - expiresTimer >= 1.0e9)
        {
            countdown--;
            expiresTimer = now;
            expiresInDesc.setText(countdown + " seconds");
            return countdown <= 0;
        }
        return false;
    }

    public void destroy(Group root) {
        root.getChildren().remove(box);
        root.getChildren().remove(crimeImageView);
        destroyed = true;
    }

    public static class CrimeInfo {
        private String description;
        private int energyUse;
        private CrimeLocation location;

        public CrimeInfo(String description, int energyUse, CrimeLocation location) {
            this.description = description;
            this.energyUse = energyUse;
            this.location = location;
        }

        public int getEnergyUse() {
            return energyUse;
        }

        /**
         * The value of this determines the place where the crime takes place, e.g. Train Station.
         * @return The Crime's location type.
         */
        public CrimeLocation getLocation() {
            return location;
        }

        /**
         * Description of the crime.
         */
        public String getDescription() {
            return description;
        }
    }

    private static CrimeInfo[] crimes = new CrimeInfo[]{
            new CrimeInfo("Bomb activated", 8, CrimeLocation.NonSpecific),
            new CrimeInfo("Train derailed", 10, CrimeLocation.TrainStation),
            new CrimeInfo("Conductor shot", 5, CrimeLocation.TrainStation),
            new CrimeInfo("Civilian mugged", 2, CrimeLocation.NonSpecific),
            new CrimeInfo("Train ticket stolen", 3, CrimeLocation.TrainStation),
            new CrimeInfo("Car blown up", 6, CrimeLocation.NonSpecific),
            new CrimeInfo("Car chase", 3, CrimeLocation.Street),

    };

    public static Crime genCrime() {
        Random rand = new Random();
        CrimeInfo info = crimes[rand.nextInt(crimes.length)];
        // TODO: Use Google's Places API to determine better locations for these.
        return new Crime(info, new Point2D(rand.nextInt(940), rand.nextInt(340)));
    }

}
