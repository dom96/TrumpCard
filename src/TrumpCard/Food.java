package TrumpCard;

import javafx.scene.image.Image;

public class Food extends Item {

    private int strength; // How much strength to add.
    private int intelligence; // How much intelligence to add.
    private int durability; // How much durability to add.
    private int energy; // How much energy to add.
    private int actionPoints; // How much action points to increase (can be negative).

    public Food(String name, Image image, int price,  CharacterName eligibleCharacter, String description,
                   int strength, int intelligence, int durability, int energy, int actionPoints) {
        super(name, image, price, eligibleCharacter, description);
        this.strength = strength;
        this.intelligence = intelligence;
        this.durability = durability;
        this.energy = energy;
        this.actionPoints = actionPoints;
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

    public int getEnergy() {
        return energy;
    }

    public int getActionPoints() {
        return actionPoints;
    }
}
