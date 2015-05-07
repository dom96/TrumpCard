package TrumpCard;

import javafx.scene.image.Image;

public class Gadget extends Item {

    private int strength; // How much strength to add.
    private int intelligence; // How much intelligence to add.
    private int durability; // How much durability to add.

    public Gadget(String name, Image image, int price,  CharacterName eligibleCharacter, String description,
                     int strength, int intelligence, int durability) {
        super(name, image, price, eligibleCharacter, description);
        this.strength = strength;
        this.intelligence = intelligence;
        this.durability = durability;
    }

    public int getStrength() {
        return strength;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getDurability() {
        return durability;
    }
}
