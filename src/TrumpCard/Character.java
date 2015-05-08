package TrumpCard;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.ArrayList;


public class Character implements java.io.Serializable {
    protected double actions; // Percentage
    protected double energy; // Percentage
    protected int score;

    protected transient Point2D pos; // Character position on the screen.
    protected transient Transition movement;

    protected CharacterName name;
    protected String userName;
    protected String hideout;

    protected transient Image image;
    protected transient boolean paused;

    protected CharacterStatus status; // Determines what character is doing.

    protected ArrayList<Item> items; // The items the player purchased.
    protected int strength;
    protected int durability;
    protected int intelligence;

    protected String clothing; // Color to make the player on map.

    protected String walkingSound;
    private transient AudioClip walkClip;

    Character(CharacterName name, String userName, String hideout)
    {
        this.name = name;
        this.userName = userName;
        this.hideout = hideout;

        this.image = this.name.loadImage();

        if (name.isVillain())
        {
            this.actions = 25.0;
        }
        if (name.isHuman())
        {
            this.actions = 50.0;
        }
        if (name.isHero())
        {
            this.actions = 75.0;
        }

        this.energy = 100;

        this.status = CharacterStatus.Still;

        // Put character in the middle of the map.
        pos = getHomePos();

        strength = name.getStrength();
        intelligence = name.getIntelligence();
        durability = name.getDurability();

        items = new ArrayList<Item>();
        clothing = "";

        walkingSound = new File("sounds/walk.wav").toURI().toString();
    }

    /**
     * Resets the fields which are not initialised after deserialization.
     */
    public void reset() {
        this.setPos(getHomePos());
        this.image = name.loadImage();
    }

    public void addItem(Item item) {
        if (item instanceof Clothing)
        {
            items.add(item);
            clothing = ((Clothing) item).getCharacterColor();
        }
        else if (item instanceof Gadget)
        {
            items.add(item);
            Gadget gadget = (Gadget) item;
            strength += gadget.getStrength();
            durability += gadget.getDurability();
            intelligence += gadget.getIntelligence();
        }
        else if (item instanceof Food)
        {
            // We don't add Food items to the list of Items because they can be re-bought.
            Food food = (Food) item;
            strength += food.getStrength();
            durability += food.getDurability();
            intelligence += food.getIntelligence();
            energy += food.getEnergy();
            actions += food.getActionPoints();
        }

    }

    public String getClothing() {
        return clothing;
    }

    public boolean hasItem(Item item) {
        // Check if it contains item with the same name.
        for (Item i : items)
        {
            if (i.getName().equals(item.name))
            {
                return true;
            }
        }
        return false;
    }

    public void moveTo(Point2D newPos) {
        // Make sure there isn't already a movement happening.
        if (movement != null)
        {
            movement.stop();
            movement = null;
            if (walkClip != null)
            {
                walkClip.stop();
            }
        }

        double distance = pos.distance(newPos);
        // Calculate how long the movement should take based on distance.
        // The higher the strength the lower the time should be.
        double timeToDest = distance * 5 * (6.0 / strength);
        // Create a brand new Transition animation which will update the Character icon's Position smoothly.
        movement = new Transition() {
            private double fromX, fromY;
            {
                setCycleDuration(Duration.millis(timeToDest));
                fromX = pos.getX();
                fromY = pos.getY();
            }

            protected void interpolate(double frac) {
                double x = fromX + Math.round((float)(frac * (newPos.getX() - fromX)));
                double y = fromY + Math.round((float)(frac * (newPos.getY() - fromY)));
                pos = new Point2D(x, y);
            }
        };
        status = CharacterStatus.Moving;
        movement.setOnFinished(event -> {
            status = CharacterStatus.Still;
            walkClip.stop();
        });
        movement.play();
        // Play walking sound.
        walkClip = new AudioClip(walkingSound);
        walkClip.setCycleCount(AudioClip.INDEFINITE);
        walkClip.play();

    }

    public void pause() {
        paused = true;
        if (movement != null && movement.getStatus() == Animation.Status.RUNNING)
        {
            movement.pause();
            walkClip.stop();
        }
    }

    public void resume() {
        paused = false;
        if (movement != null && movement.getStatus() == Animation.Status.PAUSED)
        {
            movement.play();
            walkClip.play();
        }
    }

    public int getDurability() {
        return durability;
    }

    public int getStrength() {
        return strength;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public boolean isPaused() {
        return paused;
    }

    public Point2D getHomePos() {
        return new Point2D(775, 210);
    }

    public boolean isAtHome() {
        return pos.equals(getHomePos());
    }

    public CharacterStatus getStatus() {
        return status;
    }

    public void setStatus(CharacterStatus status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getActions() {
        return actions;
    }

    public double getEnergy() {
        return energy;
    }

    public CharacterName getName() {
        return name;
    }

    public void setName(CharacterName name)
    {
        if (this.name != name) {
            this.image = name.loadImage();
        }
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public String getHideout() {
        return hideout;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setActions(double actions) {
        this.actions = actions;
    }

    public Image getImage() {
        return image;
    }

    public String getFriendlyName() {
        return name.getFriendlyName();
    }

    public Point2D getPos() {
        return pos;
    }

    public void setPos(Point2D pos) {
        this.pos = pos;
    }

    public enum CharacterStatus {
        Sleeping, Moving, Still

    }
}
