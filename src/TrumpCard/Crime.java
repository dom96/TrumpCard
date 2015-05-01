package TrumpCard;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Random;

public class Crime {
    private CrimeInfo info;
    private long expires;

    private ImageView crimeImageView;
    private VBox box;
    private TranslateTransition move;

    private Button leftBtn;
    private Button rightBtn;
    private Button ignoreBtn;

    private Label expiresInDesc;

    Crime(CrimeInfo info, long expires)
    {
        this.info = info;

        this.expires = expires;
    }

    public void show(GameState state, Group root) {
        this.crimeImageView = new ImageView(state.getCrimeIcon());
        this.crimeImageView.setFitWidth(100);
        this.crimeImageView.setPreserveRatio(true);
        this.crimeImageView.setLayoutX(this.info.getX() + 290);
        this.crimeImageView.setLayoutY(this.info.getY() + 20);
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

        Font crimeFieldsFont = Font.font("Courier New", 14);

        Label energyUseDesc = new Label(Integer.toString(info.getEnergyUse()));
        energyUseDesc.setFont(crimeFieldsFont);
        UIUtils.createBoldLabel(crimeFieldsBox, "Energy Use: ", energyUseDesc, crimeFieldsFont);

        expiresInDesc = new Label("10 seconds");
        expiresInDesc.setFont(crimeFieldsFont);
        UIUtils.createBoldLabel(crimeFieldsBox, "Expires in: ", expiresInDesc, crimeFieldsFont);

        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(90, 0, 0, 0));
        box.getChildren().add(buttonBox);

        leftBtn = new Button("Commit");
        rightBtn = new Button("Fight");
        ignoreBtn = new Button("Ignore");

        if (CharacterName.isVillain(state.getCharacter().getName()))
        {
            buttonBox.getChildren().addAll(leftBtn, ignoreBtn);
        }
        else if (CharacterName.isHuman(state.getCharacter().getName()))
        {
            buttonBox.getChildren().addAll(leftBtn, rightBtn, ignoreBtn);
        }
        else if (CharacterName.isHuman(state.getCharacter().getName()))
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

    }

    public enum CrimeLocation {
        TrainStation, River, RailWay, Street, Home, NonSpecific
    }

    /**
     * Translates this crime's box to location `x`.
     * @param x
     */
    public void translateBox(double x) {
        if (move == null)
        {
            move = new TranslateTransition(Duration.millis(1400), box);
            move.setByX(x - box.getLayoutX() - box.getTranslateX());
            move.play();
            move.setOnFinished(event -> move = null);
        }
    }

    /**
     * Updates the expiration label.
     * @return whether this crime has expired
     */
    public boolean update(long now) {
        int seconds = (int)Math.round((expires - now) / 1.0e9);
        expiresInDesc.setText(seconds + " seconds");
        return seconds <= 0;
    }

    public void destroy(Group root) {
        root.getChildren().remove(box);
        root.getChildren().remove(crimeImageView);

    }

    public static class CrimeInfo {
        private String description;
        private int energyUse;
        private CrimeLocation location;
        private int x, y;

        public CrimeInfo(String description, int energyUse, CrimeLocation location) {
            this.description = description;
            this.energyUse = energyUse;
            this.location = location;
        }

        public int getEnergyUse() {
            return energyUse;
        }

        /**
         *
         * @return X coordinates (on the map) of the crime's location.
         */
        public double getX() {
            return x;
        }

        /**
         *
         * @return Y coordinates (on the map) of the crime's location.
         */
        public double getY() {
            return y;
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

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
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

    public static CrimeInfo genCrime() {
        Random rand = new Random();
        CrimeInfo result = crimes[rand.nextInt(crimes.length)];
        // TODO: Use Google's Places API to determine better locations for these.
        result.setX(rand.nextInt(940));
        result.setY(rand.nextInt(340));
        return result;
    }

}
