package TrumpCard;

import javafx.scene.Group;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;


public class GameState {
    private Character currentCharacter;
    private ArrayList<Crime> crimes;

    private long lastCrimeGen;
    private long lastCrimeCheck;

    private final int MAX_CRIMES = 10; // Max amount of crimes at the same time.

    GameState(Character character)
    {
        this.currentCharacter = character;
        this.crimes = new ArrayList<Crime>();
    }

    public Character getCharacter()
    {
        return this.currentCharacter;
    }

    public ArrayList<Crime> getCrimes() {
        return this.crimes;
    }

    /**
     * Likelihood of a crime being generated as a percentage. 100% doesn't guarantee that one will be generated!
     */
    private double crimeLikelihood(long now)
    {
        int crimeNumber = this.crimes.size();
        // -- Likelihood that a new crime will be generated should decrease with the amount of crimes on the map.
        // -- But increase with the amount of time since the last crime was generated.

        double result = (MAX_CRIMES - crimeNumber);
        if (crimeNumber == 0) {
            result += 40;
        }
        else
        {
            long secsSinceLastCrime = (now - this.lastCrimeGen) / (long)1e9;
            result += secsSinceLastCrime * (crimeNumber/25.0);
        }
        return result;
    }

    public void poll(Group root, Image crimeIcon, long now) {
        // Decide whether a new crime/opportunity should be generated. Check only every 2 seconds.
        if (now - lastCrimeCheck >= 2e9) {
            lastCrimeCheck = now;
            double likelihood = crimeLikelihood(now);

            Random rand = new Random();
            double randDouble = rand.nextDouble();
            System.out.println(likelihood + "%" + ", " + randDouble);


            if (randDouble < likelihood / 100.f && this.crimes.size() < MAX_CRIMES) {
                Crime.CrimeInfo info = Crime.genCrime();
                Crime crime = new Crime(info, crimeIcon, root, now + 10e9);
                this.crimes.add(crime);
                this.lastCrimeGen = now;
            }
        }
        // Check crimes for expiry.


    }

}
