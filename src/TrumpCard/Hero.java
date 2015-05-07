package TrumpCard;


import java.util.ArrayList;

public class Hero extends Character {

    // There aren't very many unique fields I could come up with to put in here.
    private int goodLevel; // How good this character is.
    private ArrayList<Crime> crimesFought;

    Hero(CharacterName name, String userName, String hideout) {
        super(name, userName, hideout);
    }

    public void increaseGood(int level) {
        goodLevel += level;
    }

    public void addCrimeFought(Crime crime) {
        crimesFought.add(crime);
    }

}
