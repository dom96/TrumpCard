package TrumpCard;

import java.util.ArrayList;

public class Villain extends Character {

    // There aren't very many unique fields I could come up with to put in here.
    private int evilLevel; // How evil this character is.
    private ArrayList<Crime> crimesCommitted;

    Villain(CharacterName name, String userName, String hideout)
    {
        super(name, userName, hideout);
    }

    public void increaseEvil(int level) {
        evilLevel += level;
    }

    public void addCrimeCommitted(Crime crime) {
        crimesCommitted.add(crime);
    }
}
