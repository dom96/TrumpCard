package TrumpCard;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Random;

public class Crime {
    private CrimeInfo info;
    private double expires;

    private ImageView crimeImageView;
    private HBox box;
    private TranslateTransition move;


    Crime(CrimeInfo info, Image crimeIcon, Group root, double expires)
    {
        this.info = info;

        this.expires = expires;

        this.crimeImageView = new ImageView(crimeIcon);
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

        this.box = new HBox();
        box.setLayoutX(1000);
        box.setLayoutY(420);
        box.setPrefWidth(227.5);
        box.setPrefHeight(200);
        box.getStyleClass().add("crimeBox");
        root.getChildren().add(this.box);

        Label crimeDesc = new Label(info.getDescription());
        crimeDesc.setFont(Font.font("Courier New", 16));
        box.getChildren().add(crimeDesc);

    }

    public enum CrimeLocation {
        TrainStation, River, RailWay, Street, Home, NonSpecific
    }

    /**
     * The time (in nanoseconds, relative to the animation timer) when this crime expires.
     */
    public double getExpires() {
        return expires;
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
