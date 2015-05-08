package TrumpCard;

import javafx.scene.image.Image;

public abstract class Item implements java.io.Serializable {
    protected String name;
    protected transient Image image;
    protected int price;
    protected CharacterName eligibleCharacter;
    protected String description;

    protected Item(String name, Image image, int price, CharacterName eligibleCharacter, String description) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.eligibleCharacter = eligibleCharacter;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public CharacterName getEligibleCharacter() {
        return eligibleCharacter;
    }
}
