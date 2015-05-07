package TrumpCard;

import javafx.scene.image.Image;

public class Clothing extends Item {
    private String characterColor; // Color on the map to use.

    public Clothing(String name, Image image, int price, CharacterName eligibleCharacter, String description, String characterColor) {
        super(name, image, price, eligibleCharacter, description);
        this.characterColor = characterColor;
    }
}
