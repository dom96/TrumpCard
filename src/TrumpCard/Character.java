package TrumpCard;

import javafx.scene.image.Image;

public class Character {
    protected int actions; // Percentage
    protected int energy; // Percentage

    protected CharacterName name;
    protected String userName;
    protected String hideout;

    protected Image image;

    Character(CharacterName name, String userName, String hideout)
    {
        this.name = name;
        this.userName = userName;
        this.hideout = hideout;

        this.image = CharacterName.loadImage(this.name);
    }

    public int getActions() {
        return actions;
    }

    public int getEnergy() {
        return energy;
    }

    public CharacterName getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getHideout() {
        return hideout;
    }

    public Image getImage() {
        return image;
    }

    public String getFriendlyName() {
        return CharacterName.getFriendlyName(name);
    }
}
