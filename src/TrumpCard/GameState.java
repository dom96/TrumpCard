package TrumpCard;

public class GameState {
    private Character currentCharacter;

    GameState(Character character)
    {
        this.currentCharacter = character;

    }

    public Character getCharacter()
    {
        return this.currentCharacter;
    }

}
