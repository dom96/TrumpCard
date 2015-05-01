package TrumpCard;

import javafx.scene.image.Image;

public class Character {
    protected double actions; // Percentage
    protected double energy; // Percentage

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

    public enum CharacterStatus {
        Sleeping, Moving, Still

    }
}
