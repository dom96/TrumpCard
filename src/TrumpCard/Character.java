package TrumpCard;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;


public class Character {
    protected double actions; // Percentage
    protected double energy; // Percentage
    protected int score;

    protected Point2D pos; // Character position on the screen.
    protected Transition movement;

    protected CharacterName name;
    protected String userName;
    protected String hideout;

    protected Image image;
    protected boolean paused;

    protected CharacterStatus status; // Determines what character is doing.

    protected ArrayList<Item> items; // The items the player purchased.
    protected int strength;
    protected int durability;
    protected int intelligence;

    protected String clothing; // Color to make the player on map.

    Character(CharacterName name, String userName, String hideout)
    {
        this.name = name;
        this.userName = userName;
        this.hideout = hideout;

        this.image = CharacterName.loadImage(this.name);

        if (CharacterName.isVillain(name))
        {
            this.actions = 25.0;
        }
        if (CharacterName.isHuman(name))
        {
            this.actions = 50.0;
        }
        if (CharacterName.isHero(name))
        {
            this.actions = 75.0;
        }

        this.energy = 100;

        this.status = CharacterStatus.Still;

        // Put character in the middle of the map.
        pos = getHomePos();

        items = new ArrayList<Item>();
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

    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    public void moveTo(Point2D newPos) {
        double distance = pos.distance(newPos);
        // TODO: Tweak this based on difficulty.
        double timeToDest = distance * 5;
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
        movement.setOnFinished(event -> status = CharacterStatus.Still);
        movement.play();
    }

    public void pause() {
        paused = true;
        if (movement != null && movement.getStatus() == Animation.Status.RUNNING)
        {
            movement.pause();
        }
    }

    public void resume() {
        paused = false;
        if (movement != null && movement.getStatus() == Animation.Status.PAUSED)
        {
            movement.play();
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
            this.image = CharacterName.loadImage(name);
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
        return CharacterName.getFriendlyName(name);
    }

    public Point2D getPos() {
        return pos;
    }

    public enum CharacterStatus {
        Sleeping, Moving, Still

    }
}
